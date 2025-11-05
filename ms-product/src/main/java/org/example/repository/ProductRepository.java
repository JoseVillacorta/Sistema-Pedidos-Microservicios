package org.example.repository;

import org.example.classes.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {
    @Query("SELECT actualizar_stock(:id, :cantidad)")
    Mono<Void> actualizarStock(int id, int cantidad);

    @Query("SELECT * FROM productos_bajo_stock(:minimo)")
    Flux<Product> obtenerProductosBajoStock(int minimo);

    @Query("SELECT update_product(:id, :name, :description, :price, :stock)")
    Mono<Integer> updateProduct(int id, String name, String description, double price, int stock);

}
