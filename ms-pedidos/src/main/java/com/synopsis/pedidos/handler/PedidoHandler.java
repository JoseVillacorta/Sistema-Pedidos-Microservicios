package com.synopsis.pedidos.handler;

import com.synopsis.pedidos.entity.DetallePedido;
import com.synopsis.pedidos.entity.Pedido;
import com.synopsis.pedidos.service.PedidoService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Component
public class PedidoHandler {

    private final PedidoService pedidoService;

    public PedidoHandler(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    public Mono<ServerResponse> obtenerTodos(ServerRequest request) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(pedidoService.obtenerTodos(), Pedido.class);
    }

    public Mono<ServerResponse> obtenerPorId(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return pedidoService.obtenerPorId(id)
                .flatMap(pedido -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(pedido))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> obtenerPorCliente(ServerRequest request) {
        String cliente = request.queryParam("cliente").orElse("");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(pedidoService.obtenerPorCliente(cliente), Pedido.class);
    }

    public Mono<ServerResponse> obtenerPorEstado(ServerRequest request) {
        String estado = request.queryParam("estado").orElse("PENDIENTE");
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(pedidoService.obtenerPorEstado(estado), Pedido.class);
    }

    public Mono<ServerResponse> crearPedido(ServerRequest request) {
        return request.bodyToMono(PedidoRequest.class)
                .flatMap(pedidoRequest -> {
                    Pedido pedido = new Pedido();
                    pedido.setCliente(pedidoRequest.getCliente());

                    List<DetallePedido> detalles = pedidoRequest.getDetalles().stream()
                            .map(dr -> {
                                DetallePedido detalle = new DetallePedido();
                                detalle.setProductoId(dr.getProductoId());
                                detalle.setCantidad(dr.getCantidad());
                                return detalle;
                            })
                            .toList();

                    return pedidoService.crearPedido(pedido, detalles);
                })
                .flatMap(pedido -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(pedido));
    }

    public Mono<ServerResponse> actualizarEstado(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return request.bodyToMono(Map.class)
                .flatMap(body -> {
                    String nuevoEstado = (String) body.get("estado");
                    return pedidoService.actualizarEstado(id, nuevoEstado);
                })
                .flatMap(pedido -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(pedido));
    }

    public Mono<ServerResponse> eliminarPedido(ServerRequest request) {
        Long id = Long.parseLong(request.pathVariable("id"));
        return pedidoService.eliminarPedido(id)
                .then(ServerResponse.noContent().build());
    }

    // Clases auxiliares para el request
    public static class PedidoRequest {
        private String cliente;
        private List<DetalleRequest> detalles;

        // Getters y setters
        public String getCliente() { return cliente; }
        public void setCliente(String cliente) { this.cliente = cliente; }
        public List<DetalleRequest> getDetalles() { return detalles; }
        public void setDetalles(List<DetalleRequest> detalles) { this.detalles = detalles; }
    }

    public static class DetalleRequest {
        private Long productoId;
        private Integer cantidad;

        // Getters y setters
        public Long getProductoId() { return productoId; }
        public void setProductoId(Long productoId) { this.productoId = productoId; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}
