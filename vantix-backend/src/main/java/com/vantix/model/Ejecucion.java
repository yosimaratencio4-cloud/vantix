package com.vantix.model;

import com.vantix.model.enums.EstadoEjecucion;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Una solicitud de acción: pendiente → (aprobar) → ejecutando → terminada/error/timeout.
 * Guarda el comando_final exacto que el usuario ve y aprueba.
 */
@Entity
@Table(name = "ejecucion")
public class Ejecucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "accion_id", nullable = false)
    private AccionCatalogo accion;

    /** Quién solicitó la acción. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> parametros;

    /** Comando exacto (argumentos unidos) mostrado al aprobar. */
    @Column(name = "comando_final", nullable = false, columnDefinition = "TEXT")
    private String comandoFinal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private EstadoEjecucion estado = EstadoEjecucion.PENDIENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprobada_por")
    private Usuario aprobadaPor;

    @Column(name = "solicitada_en", nullable = false)
    private OffsetDateTime solicitadaEn = OffsetDateTime.now();

    @Column(name = "aprobada_en")
    private OffsetDateTime aprobadaEn;

    @Column(name = "iniciada_en")
    private OffsetDateTime iniciadaEn;

    @Column(name = "terminada_en")
    private OffsetDateTime terminadaEn;

    @Column(name = "codigo_salida")
    private Integer codigoSalida;

    @Column(name = "salida_truncada", columnDefinition = "TEXT")
    private String salidaTruncada;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AccionCatalogo getAccion() { return accion; }
    public void setAccion(AccionCatalogo accion) { this.accion = accion; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Map<String, Object> getParametros() { return parametros; }
    public void setParametros(Map<String, Object> parametros) { this.parametros = parametros; }

    public String getComandoFinal() { return comandoFinal; }
    public void setComandoFinal(String comandoFinal) { this.comandoFinal = comandoFinal; }

    public EstadoEjecucion getEstado() { return estado; }
    public void setEstado(EstadoEjecucion estado) { this.estado = estado; }

    public Usuario getAprobadaPor() { return aprobadaPor; }
    public void setAprobadaPor(Usuario aprobadaPor) { this.aprobadaPor = aprobadaPor; }

    public OffsetDateTime getSolicitadaEn() { return solicitadaEn; }
    public void setSolicitadaEn(OffsetDateTime solicitadaEn) { this.solicitadaEn = solicitadaEn; }

    public OffsetDateTime getAprobadaEn() { return aprobadaEn; }
    public void setAprobadaEn(OffsetDateTime aprobadaEn) { this.aprobadaEn = aprobadaEn; }

    public OffsetDateTime getIniciadaEn() { return iniciadaEn; }
    public void setIniciadaEn(OffsetDateTime iniciadaEn) { this.iniciadaEn = iniciadaEn; }

    public OffsetDateTime getTerminadaEn() { return terminadaEn; }
    public void setTerminadaEn(OffsetDateTime terminadaEn) { this.terminadaEn = terminadaEn; }

    public Integer getCodigoSalida() { return codigoSalida; }
    public void setCodigoSalida(Integer codigoSalida) { this.codigoSalida = codigoSalida; }

    public String getSalidaTruncada() { return salidaTruncada; }
    public void setSalidaTruncada(String salidaTruncada) { this.salidaTruncada = salidaTruncada; }
}
