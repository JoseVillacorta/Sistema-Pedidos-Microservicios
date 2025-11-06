package com.synopsis.pedidos.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;

@Table("detalle_pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {

    @Id
    private Long id;

    @Column("pedido_id")
    private Long pedidoId;

    @Column("producto_id")
    private Long productoId;

    private Integer cantidad;
    private Double precioUnitario;

    public Double getSubtotal() {
        return cantidad * precioUnitario;
    }
}
