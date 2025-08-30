package com.interview.petmarket.infrastructure.persistence.jpa.repository;

import com.interview.petmarket.infrastructure.persistence.jpa.entity.CarritoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para Carrito.
 */
@Repository
public interface JpaCarritoRepository extends JpaRepository<CarritoEntity, Long> {
    
    /**
     * Busca un carrito por ID del cliente.
     */
    @Query("SELECT c FROM CarritoEntity c LEFT JOIN FETCH c.items WHERE c.clienteId = :clienteId")
    Optional<CarritoEntity> findByClienteIdWithItems(@Param("clienteId") Long clienteId);
    
    /**
     * Busca un carrito por ID del cliente (sin items).
     */
    Optional<CarritoEntity> findByClienteId(Long clienteId);
    
    /**
     * Elimina un carrito por ID del cliente.
     */
    void deleteByClienteId(Long clienteId);
    
    /**
     * Verifica si existe un carrito para el cliente.
     */
    boolean existsByClienteId(Long clienteId);
}
