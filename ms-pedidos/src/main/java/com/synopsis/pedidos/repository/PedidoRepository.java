package com.synopsis.pedidos.repository;

import com.synopsis.pedidos.entity.Pedido;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface PedidoRepository extends ReactiveCrudRepository<Pedido, Long> {

    Flux<Pedido> findByCliente(String cliente);
    Flux<Pedido> findByEstado(String estado);
    Flux<Pedido> findByClienteAndEstado(String cliente, String estado);
}
