package org.example.handler;

import org.example.classes.Product;
import org.example.service.ProductService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ProductHandler {
    private final ProductService service;
    public ProductHandler(ProductService service){
        this.service= service;
    }

    public Mono<ServerResponse> getAll(ServerRequest request){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(service.findAll(), Product.class);
    }

    public Mono<ServerResponse> getById(ServerRequest request){
        int id= Integer.parseInt(request.pathVariable("id"));
        return service.findById(id)
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> create(ServerRequest request){
        return  request.bodyToMono(Product.class)
                .flatMap(service::save)
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> update(ServerRequest request){
        int id= Integer.parseInt(request.pathVariable("id"));
        return request.bodyToMono(Product.class)
                .flatMap(product -> service.update(id, product)
                        .then(service.findById(id)))
                .flatMap(updatedProduct -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(updatedProduct))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> increaseStock(ServerRequest request){
        int id= Integer.parseInt(request.pathVariable("id"));
        return request.bodyToMono(Map.class)
                .flatMap(body ->{
                    int amount= (int) body.get("amount");
                    return service.increaseStock(id, amount);
                })
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> decreaseStock(ServerRequest request){
        int id= Integer.parseInt(request.pathVariable("id"));
        return request.bodyToMono(Map.class)
                .flatMap(body ->{
                    int amount= (int) body.get("amount");
                    return service.decreaseStock(id, amount);
                })
                .flatMap(product -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(product))
                .switchIfEmpty(ServerResponse.notFound().build());
    }


    public Mono<ServerResponse> delete(ServerRequest request){
        int id= Integer.parseInt(request.pathVariable("id"));
        return  service.delete(id)
                .then(ServerResponse.noContent().build());
    }
}
