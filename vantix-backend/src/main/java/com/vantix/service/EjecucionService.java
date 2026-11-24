package com.vantix.service;

import com.vantix.dto.EjecucionDTO;
import com.vantix.dto.HallazgoDTO;
import com.vantix.dto.SolicitudEjecucionRequest;
import com.vantix.exception.VantixException;
import com.vantix.executor.EjecutorProceso;
import com.vantix.executor.ResultadoProceso;
import com.vantix.executor.ValidadorParametros;
import com.vantix.model.*;
import com.vantix.model.enums.EstadoEjecucion;
import com.vantix.parser.ParserRegistry;
import com.vantix.repository.EjecucionRepository;
import com.vantix.repository.HallazgoRepository;
import com.vantix.repository.UsuarioRepository;
import com.vantix.security.UsuarioPrincipal;
import com.vantix.tools.RunnerRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Orquesta el flujo: solicitar → aprobar/rechazar → ejecutar en background → parsear.
 * Es el ÚNICO servicio que cambia el estado de una ejecución.
 */
@Service
public class EjecucionService {

    private final EjecucionRepository ejecuciones;
    private final HallazgoRepository hallazgos;
    private final UsuarioRepository usuarios;
    private final CatalogoService catalogo;
    private final ValidadorParametros validadorParametros;
    private final RunnerRegistry runners;
    private final EjecutorProceso ejecutor;
    private final ParserRegistry parsers;
    private final AuditoriaService auditoria;
    private final ObjectProvider<EjecucionService> propio;

    public EjecucionService(EjecucionRepository ejecuciones, HallazgoRepository hallazgos,
                            UsuarioRepository usuarios, CatalogoService catalogo,
                            ValidadorParametros validadorParametros, RunnerRegistry runners,
                            EjecutorProceso ejecutor, ParserRegistry parsers,
                            AuditoriaService auditoria, ObjectProvider<EjecucionService> propio) {
        this.ejecuciones = ejecuciones;
        this.hallazgos = hallazgos;
        this.usuarios = usuarios;
        this.catalogo = catalogo;
        this.validadorParametros = validadorParametros;
        this.runners = runners;
        this.ejecutor = ejecutor;
        this.parsers = parsers;
        this.auditoria = auditoria;
        this.propio = propio;
    }

    private Usuario usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UsuarioPrincipal p)) {
            throw new VantixException("No autenticado", HttpStatus.UNAUTHORIZED);
        }
        return usuarios.findById(p.id())
                .orElseThrow(() -> new VantixException("Usuario no encontrado", HttpStatus.UNAUTHORIZED));
    }

    @Transactional
    public EjecucionDTO solicitar(SolicitudEjecucionRequest req) {
        Usuario u = usuarioActual();
        AccionCatalogo accion = catalogo.obtenerHabilitada(req.accionId());
        Map<String, Object> params = validadorParametros.validar(accion, req.parametros());
        List<String> args = runners.construir(accion.getCodigo(), params);

        Ejecucion e = new Ejecucion();
        e.setAccion(accion);
        e.setUsuario(u);
        e.setParametros(params);
        e.setComandoFinal(EjecutorProceso.formatearComando(args));
        e.setEstado(EstadoEjecucion.PENDIENTE);
        e = ejecuciones.save(e);

        auditoria.registrar(u, "SOLICITUD", e, "Acción " + accion.getCodigo());
        return aDto(e);
    }

    @Transactional
    public EjecucionDTO rechazar(Long id) {
        Usuario u = usuarioActual();
        Ejecucion e = obtener(id);
        if (e.getEstado() != EstadoEjecucion.PENDIENTE) {
            throw new VantixException("Solo se puede rechazar una ejecución PENDIENTE", HttpStatus.BAD_REQUEST);
        }
        e.setEstado(EstadoEjecucion.RECHAZADA);
        e.setAprobadaPor(u);
        e.setAprobadaEn(OffsetDateTime.now());
        e.setTerminadaEn(OffsetDateTime.now());
        ejecuciones.save(e);
        auditoria.registrar(u, "RECHAZO", e, null);
        return aDto(e);
    }

    /**
     * Marca EJECUTANDO y dispara la corrida async.
     * Responde 202 con estado EJECUTANDO; el frontend hace polling.
     */
    @Transactional
    public EjecucionDTO aprobar(Long id) {
        Usuario u = usuarioActual();
        Ejecucion e = obtener(id);
        if (e.getEstado() != EstadoEjecucion.PENDIENTE) {
            throw new VantixException("Solo se puede aprobar una ejecución PENDIENTE", HttpStatus.BAD_REQUEST);
        }
        e.setEstado(EstadoEjecucion.EJECUTANDO);
        e.setAprobadaPor(u);
        e.setAprobadaEn(OffsetDateTime.now());
        e.setIniciadaEn(OffsetDateTime.now());
        ejecuciones.save(e);
        auditoria.registrar(u, "APROBACION", e, e.getComandoFinal());

        // Reconstruir args desde lo guardado (comando_final es solo visual;
        // volvemos a construir con runner para no parsear shell).
        AccionCatalogo accion = e.getAccion();
        List<String> args = runners.construir(accion.getCodigo(), e.getParametros());
        propio.getObject().correrAsync(e.getId(), accion.getCodigo(), args);
        return aDto(e);
    }

    @Async
    public void correrAsync(Long ejecucionId, String codigoAccion, List<String> args) {
        ResultadoProceso res = ejecutor.ejecutar(args);
        finalizar(ejecucionId, codigoAccion, res);
    }

    @Transactional
    public void finalizar(Long ejecucionId, String codigoAccion, ResultadoProceso res) {
        Ejecucion e = ejecuciones.findById(ejecucionId).orElse(null);
        if (e == null) return;

        e.setCodigoSalida(res.codigoSalida());
        e.setSalidaTruncada(res.salida());
        e.setTerminadaEn(OffsetDateTime.now());

        if (res.timeout()) {
            e.setEstado(EstadoEjecucion.TIMEOUT);
        } else if (res.codigoSalida() != 0 && !"escaneo_carpeta".equals(codigoAccion)
                && !"inventario_vulns".equals(codigoAccion)) {
            // ClamAV/Grype pueden devolver !=0 con hallazgos; igual parseamos
            // Para otras herramientas, !=0 es ERROR
            e.setEstado(EstadoEjecucion.ERROR);
        } else {
            e.setEstado(EstadoEjecucion.TERMINADA);
        }

        // Si es clamav con FOUND, código 1 es normal → TERMINADA
        if ("escaneo_carpeta".equals(codigoAccion) || "inventario_vulns".equals(codigoAccion)) {
            e.setEstado(res.timeout() ? EstadoEjecucion.TIMEOUT : EstadoEjecucion.TERMINADA);
        }

        ejecuciones.save(e);

        List<Hallazgo> parsed = parsers.parsear(codigoAccion, res.salida());
        for (Hallazgo h : parsed) {
            h.setEjecucion(e);
            hallazgos.save(h);
        }
        auditoria.registrar(e.getAprobadaPor(), e.getEstado().name(), e,
                "código=" + res.codigoSalida() + " ms=" + res.milisegundos());
    }

    @Transactional(readOnly = true)
    public EjecucionDTO detalle(Long id) {
        return aDto(obtener(id));
    }

    @Transactional(readOnly = true)
    public List<EjecucionDTO> listar() {
        Usuario u = usuarioActual();
        List<Ejecucion> lista = "ADMIN".equals(u.getRol().name())
                ? ejecuciones.findAllByOrderBySolicitadaEnDesc()
                : ejecuciones.findByUsuarioIdOrderBySolicitadaEnDesc(u.getId());
        return lista.stream().map(this::aDto).toList();
    }

    @Transactional(readOnly = true)
    public List<HallazgoDTO> hallazgosDe(Long id) {
        obtener(id); // verifica existencia / acceso implícito
        return hallazgos.findByEjecucionIdOrderByIdAsc(id).stream()
                .map(h -> new HallazgoDTO(
                        h.getId(),
                        h.getTipo().name(),
                        h.getSeveridad().name(),
                        h.getTitulo(),
                        h.getDetalle()
                ))
                .toList();
    }

    private Ejecucion obtener(Long id) {
        Ejecucion e = ejecuciones.findById(id)
                .orElseThrow(() -> new VantixException("Ejecución no encontrada", HttpStatus.NOT_FOUND));
        Usuario u = usuarioActual();
        if (!"ADMIN".equals(u.getRol().name()) && !e.getUsuario().getId().equals(u.getId())) {
            throw new VantixException("Ejecución no encontrada", HttpStatus.NOT_FOUND);
        }
        return e;
    }

    private EjecucionDTO aDto(Ejecucion e) {
        return new EjecucionDTO(
                e.getId(),
                e.getAccion().getId(),
                e.getAccion().getCodigo(),
                e.getAccion().getDescripcion(),
                e.getParametros(),
                e.getComandoFinal(),
                e.getEstado().name(),
                e.getSolicitadaEn(),
                e.getIniciadaEn(),
                e.getTerminadaEn(),
                e.getCodigoSalida()
        );
    }
}
