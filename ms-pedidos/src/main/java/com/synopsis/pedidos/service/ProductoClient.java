package com.synopsis.pedidos.service;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PutExchange;
import org.springframework.cloud.openfeign.FeignClient;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@FeignClient(name = "ms-productos", url = "${ms-productos.url}")
public interface ProductoClient {

    @GetExchange("/products/{id}")
    Mono<Map<String, Object>> obtenerProducto(@PathVariable Long id);

    @PutExchange("/products/{id}/stock")
    Mono<Void> actualizarStock(@PathVariable Long id, @RequestParam Integer cantidad);
}
