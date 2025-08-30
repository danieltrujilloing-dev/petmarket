package com.interview.petmarket.infrastructure.persistence.repository;

import com.interview.petmarket.domain.model.producto.EspecieAnimal;
import com.interview.petmarket.domain.model.producto.FiltroProducto;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.model.producto.TipoProducto;
import com.interview.petmarket.domain.ports.out.ProductoRepositoryPort;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.EspecieAnimalEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.ProductoEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.TipoProductoEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.mapper.ProductoMapper;
import com.interview.petmarket.infrastructure.persistence.jpa.repository.JpaProductoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Adaptador que implementa ProductoRepositoryPort usando JPA.
 */
@Repository
public class ProductoRepositoryAdapter implements ProductoRepositoryPort {

    private static final Logger logger = LoggerFactory.getLogger(ProductoRepositoryAdapter.class);

    private final JpaProductoRepository jpaRepository;
    private final ProductoMapper mapper;

    public ProductoRepositoryAdapter(JpaProductoRepository jpaRepository, ProductoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Producto save(Producto producto) {
        logger.debug("Saving product: {}", producto.getNombre());
        
        ProductoEntity entity;
        if (producto.getId() != null) {
            // Actualizar producto existente
            entity = jpaRepository.findById(producto.getId()).orElse(new ProductoEntity());
            mapper.updateEntity(entity, producto);
        } else {
            // Crear nuevo producto
            entity = mapper.toEntity(producto);
        }
        
        ProductoEntity savedEntity = jpaRepository.save(entity);
        Producto savedProducto = mapper.toDomain(savedEntity);
        
        logger.debug("Product saved with ID: {}", savedProducto.getId());
        return savedProducto;
    }

    @Override
    public Optional<Producto> findById(Long id) {
        logger.debug("Finding product by ID: {}", id);
        
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Producto> findByFiltros(FiltroProducto filtro) {
        logger.debug("Finding products with filters: {}", filtro);
        
        if (filtro == null || !filtro.tieneAlgunFiltro()) {
            return findAllActive();
        }

        // Convertir filtros de dominio a entidades JPA
        Set<TipoProductoEntity> tiposEntity = null;
        if (filtro.getTipos() != null && !filtro.getTipos().isEmpty()) {
            tiposEntity = filtro.getTipos().stream()
                    .map(this::mapTipoToEntity)
                    .collect(Collectors.toSet());
        }

        Set<EspecieAnimalEntity> especiesEntity = null;
        if (filtro.getEspecies() != null && !filtro.getEspecies().isEmpty()) {
            especiesEntity = filtro.getEspecies().stream()
                    .map(this::mapEspecieToEntity)
                    .collect(Collectors.toSet());
        }

        Boolean activo = filtro.getSoloActivos();
        Boolean soloDisponibles = filtro.getSoloDisponibles() != null ? filtro.getSoloDisponibles() : false;

        List<ProductoEntity> entities = jpaRepository.findWithFilters(
                activo,
                tiposEntity,
                especiesEntity,
                filtro.getPrecioMinimo(),
                filtro.getPrecioMaximo(),
                filtro.getTextoBusqueda(),
                soloDisponibles
        );

        List<Producto> productos = mapper.toDomainList(entities);
        logger.debug("Found {} products with filters", productos.size());
        
        return productos;
    }

    @Override
    public List<Producto> findAllActive() {
        logger.debug("Finding all active products");
        
        List<ProductoEntity> entities = jpaRepository.findByActivoTrueOrderByFechaCreacionDesc();
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Producto> findByTipo(String tipo) {
        logger.debug("Finding products by type: {}", tipo);
        
        TipoProducto tipoProducto = TipoProducto.valueOf(tipo);
        TipoProductoEntity tipoEntity = mapTipoToEntity(tipoProducto);
        
        List<ProductoEntity> entities = jpaRepository.findByTipoAndActivoTrueOrderByNombreAsc(tipoEntity);
        return mapper.toDomainList(entities);
    }

    @Override
    public List<Producto> findByEspecie(String especie) {
        logger.debug("Finding products by species: {}", especie);
        
        EspecieAnimal especieAnimal = EspecieAnimal.valueOf(especie);
        EspecieAnimalEntity especieEntity = mapEspecieToEntity(especieAnimal);
        
        List<ProductoEntity> entities = jpaRepository.findByEspecieAndActivoTrueOrderByNombreAsc(especieEntity);
        return mapper.toDomainList(entities);
    }

    @Override
    public boolean existsById(Long id) {
        logger.debug("Checking if product exists: {}", id);
        
        return jpaRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting product: {}", id);
        
        jpaRepository.deleteById(id);
    }

    @Override
    public long countActive() {
        logger.debug("Counting active products");
        
        return jpaRepository.countByActivoTrue();
    }

    @Override
    public List<Producto> findWithLowStock(Integer stockMinimo) {
        logger.debug("Finding products with low stock: {}", stockMinimo);
        
        List<ProductoEntity> entities = jpaRepository.findWithLowStock(stockMinimo);
        return mapper.toDomainList(entities);
    }

    // ================ MÉTODOS ADICIONALES ÚTILES ================

    /**
     * Busca productos por tipo y especie específicos.
     */
    public List<Producto> findByTipoAndEspecie(String tipo, String especie) {
        logger.debug("Finding products by type {} and species {}", tipo, especie);
        
        TipoProducto tipoProducto = TipoProducto.valueOf(tipo);
        EspecieAnimal especieAnimal = EspecieAnimal.valueOf(especie);
        
        TipoProductoEntity tipoEntity = mapTipoToEntity(tipoProducto);
        EspecieAnimalEntity especieEntity = mapEspecieToEntity(especieAnimal);
        
        List<ProductoEntity> entities = jpaRepository.findByTipoAndEspecieAndActivoTrueOrderByPrecioAsc(
                tipoEntity, especieEntity);
        return mapper.toDomainList(entities);
    }

    /**
     * Busca productos disponibles (con stock).
     */
    public List<Producto> findAvailable() {
        logger.debug("Finding available products");
        
        List<ProductoEntity> entities = jpaRepository.findAvailable();
        return mapper.toDomainList(entities);
    }

    /**
     * Busca productos por texto.
     */
    public List<Producto> findByTexto(String texto) {
        logger.debug("Finding products by text: {}", texto);
        
        List<ProductoEntity> entities = jpaRepository.findByTexto(texto);
        return mapper.toDomainList(entities);
    }

    /**
     * Busca productos por rango de precios.
     */
    public List<Producto> findByPriceRange(java.math.BigDecimal min, java.math.BigDecimal max) {
        logger.debug("Finding products by price range: {} - {}", min, max);
        
        List<ProductoEntity> entities = jpaRepository.findByPriceRange(min, max);
        return mapper.toDomainList(entities);
    }

    // ================ MAPPERS PRIVADOS ================

    private TipoProductoEntity mapTipoToEntity(TipoProducto domain) {
        return switch (domain) {
            case ALIMENTO -> TipoProductoEntity.ALIMENTO;
            case ACCESORIO -> TipoProductoEntity.ACCESORIO;
            case JUGUETE -> TipoProductoEntity.JUGUETE;
            case MEDICINA -> TipoProductoEntity.MEDICINA;
            case HIGIENE -> TipoProductoEntity.HIGIENE;
            case CAMA -> TipoProductoEntity.CAMA;
            case COLLAR -> TipoProductoEntity.COLLAR;
            case CORREA -> TipoProductoEntity.CORREA;
            case TRANSPORTADORA -> TipoProductoEntity.TRANSPORTADORA;
            case ENTRENAMIENTO -> TipoProductoEntity.ENTRENAMIENTO;
        };
    }

    private EspecieAnimalEntity mapEspecieToEntity(EspecieAnimal domain) {
        return switch (domain) {
            case PERRO -> EspecieAnimalEntity.PERRO;
            case GATO -> EspecieAnimalEntity.GATO;
            case AVE -> EspecieAnimalEntity.AVE;
            case PEZ -> EspecieAnimalEntity.PEZ;
            case REPTIL -> EspecieAnimalEntity.REPTIL;
            case ROEDOR -> EspecieAnimalEntity.ROEDOR;
            case CONEJO -> EspecieAnimalEntity.CONEJO;
            case HAMSTER -> EspecieAnimalEntity.HAMSTER;
            case UNIVERSAL -> EspecieAnimalEntity.UNIVERSAL;
        };
    }
}
