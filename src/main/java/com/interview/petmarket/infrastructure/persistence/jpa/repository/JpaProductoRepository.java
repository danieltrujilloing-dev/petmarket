package com.interview.petmarket.infrastructure.persistence.jpa.repository;

import com.interview.petmarket.infrastructure.persistence.jpa.entity.ProductoEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.TipoProductoEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.EspecieAnimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Repositorio JPA para productos con consultas específicas.
 */
@Repository
public interface JpaProductoRepository extends JpaRepository<ProductoEntity, Long> {

    /**
     * Encuentra productos activos.
     */
    List<ProductoEntity> findByActivoTrueOrderByFechaCreacionDesc();

    /**
     * Encuentra productos por tipo.
     */
    List<ProductoEntity> findByTipoAndActivoTrueOrderByNombreAsc(TipoProductoEntity tipo);

    /**
     * Encuentra productos por especie.
     */
    List<ProductoEntity> findByEspecieAndActivoTrueOrderByNombreAsc(EspecieAnimalEntity especie);

    /**
     * Encuentra productos por rango de precios.
     */
    @Query("SELECT p FROM ProductoEntity p WHERE p.activo = true " +
           "AND (:precioMin IS NULL OR p.precio >= :precioMin) " +
           "AND (:precioMax IS NULL OR p.precio <= :precioMax) " +
           "ORDER BY p.precio ASC")
    List<ProductoEntity> findByPriceRange(@Param("precioMin") BigDecimal precioMin, 
                                         @Param("precioMax") BigDecimal precioMax);

    /**
     * Búsqueda por texto en nombre y descripción.
     */
    @Query("SELECT p FROM ProductoEntity p WHERE p.activo = true " +
           "AND (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
           "ORDER BY p.nombre ASC")
    List<ProductoEntity> findByTexto(@Param("texto") String texto);

    /**
     * Consulta compleja con múltiples filtros.
     */
    @Query("SELECT p FROM ProductoEntity p WHERE " +
           "(:activo IS NULL OR p.activo = :activo) " +
           "AND (:tipos IS NULL OR p.tipo IN :tipos) " +
           "AND (:especies IS NULL OR p.especie IN :especies) " +
           "AND (:precioMin IS NULL OR p.precio >= :precioMin) " +
           "AND (:precioMax IS NULL OR p.precio <= :precioMax) " +
           "AND (:texto IS NULL OR p.nombre LIKE CONCAT('%', :texto, '%') " +
           "     OR p.descripcion LIKE CONCAT('%', :texto, '%')) " +
           "AND (:soloDisponibles = false OR (p.stock IS NOT NULL AND p.stock > 0)) " +
           "ORDER BY p.fechaActualizacion DESC")
    List<ProductoEntity> findWithFilters(
            @Param("activo") Boolean activo,
            @Param("tipos") Set<TipoProductoEntity> tipos,
            @Param("especies") Set<EspecieAnimalEntity> especies,
            @Param("precioMin") BigDecimal precioMin,
            @Param("precioMax") BigDecimal precioMax,
            @Param("texto") String texto,
            @Param("soloDisponibles") Boolean soloDisponibles);

    /**
     * Cuenta productos activos.
     */
    long countByActivoTrue();

    /**
     * Encuentra productos con stock bajo.
     */
    @Query("SELECT p FROM ProductoEntity p WHERE p.activo = true " +
           "AND p.stock IS NOT NULL AND p.stock <= :stockMinimo " +
           "ORDER BY p.stock ASC")
    List<ProductoEntity> findWithLowStock(@Param("stockMinimo") Integer stockMinimo);

    /**
     * Encuentra productos disponibles (con stock).
     */
    @Query("SELECT p FROM ProductoEntity p WHERE p.activo = true " +
           "AND p.stock IS NOT NULL AND p.stock > 0 " +
           "ORDER BY p.fechaActualizacion DESC")
    List<ProductoEntity> findAvailable();

    /**
     * Busca productos por tipo y especie.
     */
    List<ProductoEntity> findByTipoAndEspecieAndActivoTrueOrderByPrecioAsc(
            TipoProductoEntity tipo, EspecieAnimalEntity especie);

    /**
     * Verifica si existe un producto con el mismo nombre.
     */
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    /**
     * Encuentra productos ordenados por precio.
     */
    List<ProductoEntity> findByActivoTrueOrderByPrecioAsc();

    /**
     * Encuentra productos más recientes.
     */
    @Query("SELECT p FROM ProductoEntity p WHERE p.activo = true " +
           "ORDER BY p.fechaCreacion DESC")
    List<ProductoEntity> findRecentProducts();
}
