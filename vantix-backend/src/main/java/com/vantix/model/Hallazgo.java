package com.vantix.model;

import com.vantix.model.enums.Severidad;
import com.vantix.model.enums.TipoHallazgo;
import jakarta.persistence.*;

/**
 * Resultado estructurado de una ejecución (CVE, malware, dato de sistema).
 * La salida cruda de la herramienta nunca se interpreta como código.
 */
@Entity
@Table(name = "hallazgo")
public class Hallazgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ejecucion_id", nullable = false)
    private Ejecucion ejecucion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoHallazgo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Severidad severidad;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String detalle;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Ejecucion getEjecucion() { return ejecucion; }
    public void setEjecucion(Ejecucion ejecucion) { this.ejecucion = ejecucion; }

    public TipoHallazgo getTipo() { return tipo; }
    public void setTipo(TipoHallazgo tipo) { this.tipo = tipo; }

    public Severidad getSeveridad() { return severidad; }
    public void setSeveridad(Severidad severidad) { this.severidad = severidad; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
}
