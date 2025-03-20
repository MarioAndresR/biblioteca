package com.egg.biblioteca.entidades;

import java.util.Date;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // By default @NaturalId marks an natural id attribute as immutable, so it never changes its value.
    // It is set as true to be able to modify it
    @NaturalId(mutable = true)
    @Size(min = 13, max = 13) // ISBN is a 13-digit number
    @Column(nullable = false)
    private String isbn;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private Integer ejemplares;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date alta;

    @ManyToOne // Muchos libros pueden estar asociados a un autor
    @JoinColumn(nullable = false)
    private Autor autor;
    
    @ManyToOne // Muchos libros pueden estar asociados a una editorial
    @JoinColumn(nullable = false)
    private Editorial editorial;
}
