package org.example.service;
import org.example.classes.Product;
import org.example.repository.ProductRepository;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository){
        this.repository= repository;
    }

    public Flux<Product> findAll(){
        return repository.findAll();
    }

    public Mono<Product> findById(int id){
        return repository.findById(id);
    }

    public Mono<Product> save(Product product){
        return repository.save(product);
    }

    public Mono<Void> update(int id, Product updated){
        return repository.updateProduct(
                id,
                updated.getName(),
                updated.getDescription(),
                updated.getPrice(),
                updated.getStock()
        ).then();
    }

    public Mono<Product> increaseStock(int id, int amount){
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado")))
                .flatMap(product -> {
                    int newStock= product.getStock()+amount;
                    product.setStock(newStock);
                    return repository.save(product);
                });
    }

    public Mono<Product> decreaseStock(int id, int amount){
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto no encontrado")))
                .flatMap(product -> {
                    int newStock= product.getStock()-amount;
                    if(newStock<0){
                        return Mono.error(new IllegalArgumentException("No hay suficiente stock para la operación"));
                    }
                    product.setStock(newStock);
                    return repository.save(product);
                });
    }

    public Mono<Void> delete(int id){
        return repository.deleteById(id);
    }

    public Mono<Void> actualizarStock(int id, int cantidad) {
        return repository.actualizarStock(id, cantidad).then();
    }

    public Flux<Product> obtenerProductosBajoStock(int minimo) {
        return repository.obtenerProductosBajoStock(minimo);
    }
}
