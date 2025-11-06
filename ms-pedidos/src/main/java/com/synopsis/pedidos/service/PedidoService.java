package com.synopsis.pedidos.service;

import com.synopsis.pedidos.entity.DetallePedido;
import com.synopsis.pedidos.entity.Pedido;
import com.synopsis.pedidos.repository.DetallePedidoRepository;
import com.synopsis.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoClient productoClient;

    public Flux<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public Mono<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Flux<Pedido> obtenerPorCliente(String cliente) {
        return pedidoRepository.findByCliente(cliente);
    }

    public Flux<Pedido> obtenerPorEstado(String estado) {
        return pedidoRepository.findByEstado(estado);
    }

    public Mono<Pedido> crearPedido(Pedido pedido, List<DetallePedido> detalles) {
        // Establecer valores por defecto
        pedido.setFecha(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");

        // Asignar detalles al pedido
        detalles.forEach(detalle -> detalle.setPedidoId(pedido.getId()));

        return validarPedido(detalles)
                .then(Mono.fromCallable(() -> {
                    double total = detalles.stream()
                            .mapToDouble(DetallePedido::getSubtotal)
                            .sum();
                    pedido.setTotal(total);
                    return pedido;
                }))
                .flatMap(p -> pedidoRepository.save(p))
                .flatMap(savedPedido -> {
                    // Guardar detalles
                    detalles.forEach(d -> d.setPedidoId(savedPedido.getId()));
                    return Flux.fromIterable(detalles)
                            .flatMap(detallePedidoRepository::save)
                            .then(Mono.just(savedPedido));
                })
                .flatMap(savedPedido ->
                        // Actualizar stock después de guardar
                        actualizarStockProductos(detalles)
                                .thenReturn(savedPedido)
                );
    }

    public Mono<Pedido> actualizarEstado(Long id, String nuevoEstado) {
        return pedidoRepository.findById(id)
                .flatMap(pedido -> {
                    // Si se cancela, devolver stock
                    if ("CANCELADO".equals(nuevoEstado) && !"CANCELADO".equals(pedido.getEstado())) {
                        return devolverStockProductos(id)
                                .then(Mono.fromCallable(() -> {
                                    pedido.setEstado(nuevoEstado);
                                    return pedido;
                                }));
                    } else {
                        pedido.setEstado(nuevoEstado);
                        return Mono.just(pedido);
                    }
                })
                .flatMap(pedidoRepository::save);
    }

    public Mono<Void> eliminarPedido(Long id) {
        return pedidoRepository.findById(id)
                .flatMap(pedido ->
                        devolverStockProductos(id)
                                .then(detallePedidoRepository.deleteByPedidoId(id))
                                .then(pedidoRepository.deleteById(id))
                );
    }

    private Mono<Void> validarPedido(List<DetallePedido> detalles) {
        return Flux.fromIterable(detalles)
                .flatMap(detalle ->
                        productoClient.obtenerProducto(detalle.getProductoId())
                                .doOnNext(producto -> {
                                    Integer stockDisponible = (Integer) producto.get("stock");
                                    if (stockDisponible < detalle.getCantidad()) {
                                        throw new RuntimeException("Stock insuficiente para producto: " + producto.get("name"));
                                    }
                                    // Establecer precio desde el producto
                                    detalle.setPrecioUnitario(((Number) producto.get("price")).doubleValue());
                                })
                )
                .then();
    }

    private Mono<Void> actualizarStockProductos(List<DetallePedido> detalles) {
        return Flux.fromIterable(detalles)
                .flatMap(detalle ->
                        productoClient.actualizarStock(detalle.getProductoId(), detalle.getCantidad())
                )
                .then();
    }

    private Mono<Void> devolverStockProductos(Long pedidoId) {
        return detallePedidoRepository.findByPedidoId(pedidoId)
                .flatMap(detalle ->
                        productoClient.actualizarStock(detalle.getProductoId(), -detalle.getCantidad())
                )
                .then();
    }
}
