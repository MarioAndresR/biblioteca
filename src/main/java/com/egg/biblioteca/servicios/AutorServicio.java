package com.egg.biblioteca.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.egg.biblioteca.entidades.Autor;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.repositorios.AutorRepositorio;

@Service
public class AutorServicio {
    @Autowired
    private AutorRepositorio autorRepositorio;

    @Transactional
    public void crearAutor(String nombre) throws MiException {
        // Valida el valor del atributo, no nulo ni vacío
        validar(nombre);
        // Instancia un objeto del tipo Autor
        Autor autor = new Autor();
        // Setea el atributo, con el valor recibido como parámetro
        autor.setNombre(nombre);
        // Persisto el dato en mi BBDD
        autorRepositorio.save(autor);
    }

    @Transactional(readOnly = true)
    public List<Autor> listarAutores() {
        List<Autor> autores = new ArrayList<>();
        autores = autorRepositorio.findAll();
        return autores;
    }

    // Gets and returns an "autor" given its id
    @Transactional(readOnly = true)
    public Autor getOne(String id) {
        return autorRepositorio.getReferenceById(id);
    }

    @Transactional
    public void modificarAutor(String nombre, String id) throws MiException {
        // Valida el valor del atributo, no nulo ni vacío
        validar(nombre);
        Optional<Autor> respuesta = autorRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Autor autor = respuesta.get();
            autor.setNombre(nombre);
            autorRepositorio.save(autor);
        }
    }

    private void validar(String nombre) throws MiException {
        if (nombre.trim().isEmpty() || nombre == null) {
            throw new MiException("El nombre del autor es requerido.");
        }
    }

}