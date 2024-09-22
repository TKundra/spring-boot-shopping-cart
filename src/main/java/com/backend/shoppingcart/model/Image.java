package com.backend.shoppingcart.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Blob;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Image {
    // used to specify how the primary key values of an entity are generated
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    private String fileType;

    // @Lob annotation specifies that the database should store the property as Large Object.
    @Lob
    private Blob image;
    private String downloadUrl;

    // @JoinColumn will join two entities, usually use along with @ManyToOne or @OneToOne
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
