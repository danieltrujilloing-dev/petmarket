package com.interview.petmarket.infrastructure.persistence.jpa.repository;

import com.interview.petmarket.infrastructure.persistence.jpa.entity.EstadoPedidoEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para Pedido.
 */
@Repository
public interface JpaPedidoRepository extends JpaRepository<PedidoEntity, Long> {
    
    /**
     * Busca un pedido por ID con sus items.
     */
    @Query("SELECT p FROM PedidoEntity p LEFT JOIN FETCH p.items WHERE p.id = :id")
    Optional<PedidoEntity> findByIdWithItems(@Param("id") Long id);
    
    /**
     * Busca pedidos por cliente.
     */
    @Query("SELECT p FROM PedidoEntity p WHERE p.clienteId = :clienteId ORDER BY p.createdAt DESC")
    List<PedidoEntity> findByClienteId(@Param("clienteId") Long clienteId);
    
    /**
     * Busca pedidos por estado.
     */
    List<PedidoEntity> findByEstado(EstadoPedidoEntity estado);
    
    /**
     * Busca pedidos por cliente y estado.
     */
    List<PedidoEntity> findByClienteIdAndEstado(Long clienteId, EstadoPedidoEntity estado);
}
