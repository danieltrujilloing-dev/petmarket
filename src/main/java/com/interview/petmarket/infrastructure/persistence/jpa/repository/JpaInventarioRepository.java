package com.interview.petmarket.infrastructure.persistence.jpa.repository;

import com.interview.petmarket.infrastructure.persistence.jpa.entity.InventarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para Inventario.
 */
@Repository
public interface JpaInventarioRepository extends JpaRepository<InventarioEntity, Long> {
    
    /**
     * Busca inventario por ID del producto.
     */
    Optional<InventarioEntity> findByProductoId(Long productoId);
    
    /**
     * Busca inventarios con stock bajo el umbral.
     */
    @Query("SELECT i FROM InventarioEntity i WHERE i.stockDisponible <= i.umbralReposicion")
    List<InventarioEntity> findByStockBelowThreshold();
    
    /**
     * Verifica si hay stock disponible para un producto.
     */
    @Query("SELECT CASE WHEN i.stockDisponible >= :cantidad THEN true ELSE false END " +
           "FROM InventarioEntity i WHERE i.productoId = :productoId")
    boolean hasStock(@Param("productoId") Long productoId, @Param("cantidad") int cantidad);
    
    /**
     * Reserva stock para un producto (disminuye stock disponible).
     */
    @Modifying
    @Query("UPDATE InventarioEntity i SET i.stockDisponible = i.stockDisponible - :cantidad " +
           "WHERE i.productoId = :productoId AND i.stockDisponible >= :cantidad")
    int reservarStock(@Param("productoId") Long productoId, @Param("cantidad") int cantidad);
    
    /**
     * Libera stock reservado (aumenta stock disponible).
     */
    @Modifying
    @Query("UPDATE InventarioEntity i SET i.stockDisponible = i.stockDisponible + :cantidad " +
           "WHERE i.productoId = :productoId")
    int liberarStock(@Param("productoId") Long productoId, @Param("cantidad") int cantidad);
}
