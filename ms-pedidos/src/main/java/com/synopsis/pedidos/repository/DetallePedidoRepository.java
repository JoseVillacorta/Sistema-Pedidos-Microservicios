package com.synopsis.pedidos.repository;

import com.synopsis.pedidos.entity.DetallePedido;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface DetallePedidoRepository extends ReactiveCrudRepository<DetallePedido, Long> {

    Flux<DetallePedido> findByPedidoId(Long pedidoId);

    // Agregar este método
    Mono<Void> deleteByPedidoId(Long pedidoId);
}
