package com.synopsis.pedidos.router;

import com.synopsis.pedidos.handler.PedidoHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class PedidoRouter {

    @Bean
    public RouterFunction<ServerResponse> pedidoRoutes(PedidoHandler handler) {
        return RouterFunctions.route()
                .GET("/api/pedidos", handler::obtenerTodos)
                .GET("/api/pedidos/{id}", handler::obtenerPorId)
                .GET("/api/pedidos/search/cliente", handler::obtenerPorCliente)
                .GET("/api/pedidos/search/estado", handler::obtenerPorEstado)
                .POST("/api/pedidos", handler::crearPedido)
                .PUT("/api/pedidos/{id}/estado", handler::actualizarEstado)
                .DELETE("/api/pedidos/{id}", handler::eliminarPedido)
                .build();
    }
}
