package org.example.classes;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Table("product")
public class Product {
    @Id
    private int id;

    private String name;
    private String description;
    private double price;
    private int stock;
}
