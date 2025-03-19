package com.egg.biblioteca.entidades;

import java.util.Date;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.*;

@Entity
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // By default @NaturalId marks an natural id attribute as immutable, so it never changes its value.
    // It is set as true to be able to modify it
    @NaturalId(mutable = true)
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

    public Libro() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getEjemplares() {
        return ejemplares;
    }

    public void setEjemplares(Integer ejemplares) {
        this.ejemplares = ejemplares;
    }

    public Date getAlta() {
        return alta;
    }

    public void setAlta(Date alta) {
        this.alta = alta;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public Editorial getEditorial() {
        return editorial;
    }

    public void setEditorial(Editorial editorial) {
        this.editorial = editorial;
    }
}
