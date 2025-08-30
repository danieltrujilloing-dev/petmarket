package com.interview.petmarket.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA para el producto usando Lombok.
 */
@Entity
@Table(name = "productos", indexes = {
    @Index(name = "idx_producto_tipo", columnList = "tipo"),
    @Index(name = "idx_producto_especie", columnList = "especie_destino"),
    @Index(name = "idx_producto_activo", columnList = "activo"),
    @Index(name = "idx_producto_precio", columnList = "precio"),
    @Index(name = "idx_producto_nombre", columnList = "nombre")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "tipo", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private TipoProductoEntity tipo;

    @Column(name = "especie_destino", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EspecieAnimalEntity especie;

    @Column(name = "stock")
    private Integer stock;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime fechaActualizacion;
}
