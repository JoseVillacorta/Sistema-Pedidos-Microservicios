package com.synopsis.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableFeignClients    // Para Feign reactivo
public class MsPedidosApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsPedidosApplication.class, args);
    }
}
