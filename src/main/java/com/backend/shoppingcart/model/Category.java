package com.backend.shoppingcart.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    // @JsonIgnore - to break the loop
    @JsonIgnore
    @OneToMany(mappedBy = "category")
    private List<Product> products;

    // args constructor
    public Category(String name) {
        this.name = name;
    }
}
