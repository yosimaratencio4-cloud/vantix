package com.vantix.model;

import com.vantix.model.enums.RiesgoAccion;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;

/**
 * Acción de la lista blanca.
 * La IA (futuro) o el frontend solo eligen un código de aquí;
 * nunca inventan comandos de shell.
 */
@Entity
@Table(name = "accion_catalogo")
public class AccionCatalogo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código estable, ej. osquery_puertos */
    @Column(nullable = false, unique = true, length = 60)
    private String codigo;

    /** Familia de herramienta: osquery | syft_grype | clamav */
    @Column(nullable = false, length = 30)
    private String herramienta;

    @Column(nullable = false)
    private String descripcion;

    /** Esquema JSON de parámetros esperados, ej. {"ruta":{"tipo":"ruta"}} */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "esquema_parametros", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> esquemaParametros;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RiesgoAccion riesgo;

    @Column(nullable = false)
    private boolean habilitada = true;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getHerramienta() { return herramienta; }
    public void setHerramienta(String herramienta) { this.herramienta = herramienta; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Map<String, Object> getEsquemaParametros() { return esquemaParametros; }
    public void setEsquemaParametros(Map<String, Object> esquemaParametros) { this.esquemaParametros = esquemaParametros; }

    public RiesgoAccion getRiesgo() { return riesgo; }
    public void setRiesgo(RiesgoAccion riesgo) { this.riesgo = riesgo; }

    public boolean isHabilitada() { return habilitada; }
    public void setHabilitada(boolean habilitada) { this.habilitada = habilitada; }
}
