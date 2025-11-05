package org.example.router;
import org.example.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class ProductRouter {

    @Bean
    public RouterFunction<ServerResponse> route(ProductHandler handler){
        return RouterFunctions
                .route()
                .GET("/products", handler::getAll)
                .GET("/products/{id}", handler::getById)
                .POST("/products", handler::create)
                .PUT("/products/{id}", handler::update)
                .PUT("/products/{id}/stock", handler::actualizarStock)
                .GET("/products/bajo-stock", handler::obtenerBajoStock)
                .DELETE("/products/{id}", handler::delete)
                .build();
    }
}
