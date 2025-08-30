package com.interview.petmarket.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA para Tarea de Reposición.
 * Mapea la entidad de dominio TareaReposicion a la base de datos.
 */
@Entity
@Table(name = "tareas_reposicion")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TareaReposicionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "producto_id", nullable = false)
    private Long productoId;
    
    @Column(name = "nombre_producto", nullable = false, length = 255)
    private String nombreProducto;
    
    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;
    
    @Column(name = "umbral_reposicion", nullable = false)
    private Integer umbralReposicion;
    
    @Column(name = "cantidad_sugerida", nullable = false)
    private Integer cantidadSugerida;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoTareaEntity estado;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", nullable = false, length = 20)
    private PrioridadTareaEntity prioridad;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDateTime fechaVencimiento;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Índices para optimizar consultas
    @Table(indexes = {
            @Index(name = "idx_tarea_producto_id", columnList = "producto_id"),
            @Index(name = "idx_tarea_estado", columnList = "estado"),
            @Index(name = "idx_tarea_prioridad", columnList = "prioridad"),
            @Index(name = "idx_tarea_fecha_vencimiento", columnList = "fecha_vencimiento"),
            @Index(name = "idx_tarea_producto_estado", columnList = "producto_id, estado")
    })
    public static class TableDefinition {}
}
