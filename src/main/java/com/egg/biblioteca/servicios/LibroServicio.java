package com.egg.biblioteca.servicios;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.egg.biblioteca.entidades.Autor;
import com.egg.biblioteca.entidades.Editorial;
import com.egg.biblioteca.entidades.Libro;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.repositorios.AutorRepositorio;
import com.egg.biblioteca.repositorios.EditorialRepositorio;
import com.egg.biblioteca.repositorios.LibroRepositorio;

@Service
public class LibroServicio {
    @Autowired
    private LibroRepositorio libroRepositorio;
    @Autowired
    private AutorRepositorio autorRepositorio;
    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @Transactional
    public void crearLibro(String isbn, String titulo, Integer ejemplares, String idAutor, String idEditorial)
            throws MiException {
        // Valida el valor de los atributos, no nulo ni vacío
        validar(isbn, titulo, ejemplares, idAutor, idEditorial);
        Optional<Autor> respuestaAutor = autorRepositorio.findById(idAutor);
        Optional<Editorial> respuestaEditorial = editorialRepositorio.findById(idEditorial);

        if (respuestaAutor.isEmpty()) {
            throw new MiException("El autor con ID " + idAutor + " no existe.");
        }

        if (respuestaEditorial.isEmpty()) {
            throw new MiException("La editorial con ID " + idEditorial + " no existe.");
        }

        Libro libro = new Libro();
        libro.setIsbn(isbn);
        libro.setTitulo(titulo);
        libro.setEjemplares(ejemplares);
        libro.setAutor(respuestaAutor.get());
        libro.setEditorial(respuestaEditorial.get());
        // Current date
        libro.setAlta(new Date());
        libroRepositorio.save(libro);
    }

    @Transactional(readOnly = true)
    public List<Libro> listarLibros() {
        List<Libro> libros = new ArrayList<>();
        libros = libroRepositorio.findAll();
        return libros;
    }

    // Gets and returns an "editorial" given its id
    @Transactional(readOnly = true)
    public Libro getOne(Long id) {
        return libroRepositorio.getReferenceById(id);
    }

    @Transactional
    public void modificarLibro(Long id, String isbn, String titulo, Integer ejemplares, String idAutor,
            String idEditorial) throws MiException {
        validarId(id);
        // Valida el valor de los atributos, no nulo ni vacío
        validar(isbn, titulo, ejemplares, idAutor, idEditorial);

        Optional<Libro> respuesta = libroRepositorio.findById(id);
        Optional<Autor> respuestaAutor = autorRepositorio.findById(idAutor);
        Optional<Editorial> respuestaEditorial = editorialRepositorio.findById(idEditorial);

        if (respuesta.isEmpty()) {
            throw new MiException("El libro con ID " + id + " no existe.");
        }

        if (respuestaAutor.isEmpty()) {
            throw new MiException("El autor on ID " + idAutor + " no existe.");
        }

        if (respuestaEditorial.isEmpty()) {
            throw new MiException("La editorial on ID " + idEditorial + " no existe.");
        }

        // Obtiene el libro, autor, editorial, setea y actualiza los atributos nuevos
        // del libro
        Libro libro = respuesta.get();
        libro.setIsbn(isbn);
        libro.setTitulo(titulo);
        libro.setEjemplares(ejemplares);
        libro.setAutor(respuestaAutor.get());
        libro.setEditorial(respuestaEditorial.get());
        // Current date
        libro.setAlta(new Date());
        libroRepositorio.save(libro);
    }

    private void validarId(Long id) throws MiException {
        if (id == null) {
            throw new MiException("El ID del Libro no puede ser nulo.");
        }

        if (id <= 0) {
            throw new MiException("El ID del libro no puede ser negativo o cero.");
        }
    }

    private void validar(String isbn, String titulo, Integer ejemplares, String idAutor, String idEditorial)
            throws MiException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new MiException("El isbn del libro es requerido.");
        }

        if (titulo == null || titulo.trim().isEmpty()) {
            throw new MiException("El titulo del libro es requerido.");
        }

        if (ejemplares == null) {
            throw new MiException("La cantidad de ejemplares del libro es requerida.");
        }

        if (ejemplares <= 0) {
            throw new MiException("La cantidad de ejemplares debe ser mayor que cero.");
        }

        if (idAutor == null || idAutor.trim().isEmpty()) {
            throw new MiException("El ID del autor del libro es requerido.");
        }

        if (idEditorial == null || idEditorial.trim().isEmpty()) {
            throw new MiException("El ID de la editorial del libro es requerido.");
        }
    }

}