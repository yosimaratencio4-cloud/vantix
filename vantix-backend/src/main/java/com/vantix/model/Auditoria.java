package com.vantix.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Registro de solo inserción: quién hizo qué y cuándo.
 * El rol de BD de la app no tiene UPDATE ni DELETE sobre esta tabla.
 */
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private OffsetDateTime fecha = OffsetDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    /** Ej. LOGIN_OK, SOLICITUD, APROBACION, RECHAZO, TERMINADA */
    @Column(nullable = false, length = 60)
    private String evento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ejecucion_id")
    private Ejecucion ejecucion;

    @Column(columnDefinition = "TEXT")
    private String detalle;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OffsetDateTime getFecha() { return fecha; }
    public void setFecha(OffsetDateTime fecha) { this.fecha = fecha; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }

    public Ejecucion getEjecucion() { return ejecucion; }
    public void setEjecucion(Ejecucion ejecucion) { this.ejecucion = ejecucion; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
}
