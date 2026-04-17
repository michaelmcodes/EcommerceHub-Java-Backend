package com.michaelmark.ecommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @NotBlank
    @Size(min = 3, message = "Product name must be at least 3 characters long")
    private String productName;

    @NotBlank
    @Size(min = 6, message = "Product description must be at least 6 characters long")
    private String description;

    private String image;
    private Integer quantity;
    private Double price; //100
    private Double discount; //25
    private Double specialPrice; //100*0.25

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;
}
