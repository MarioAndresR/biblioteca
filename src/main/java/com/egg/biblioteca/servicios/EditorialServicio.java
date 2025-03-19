package com.egg.biblioteca.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.egg.biblioteca.entidades.Editorial;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.repositorios.EditorialRepositorio;

@Service
public class EditorialServicio {
    @Autowired
    private EditorialRepositorio editorialRepositorio;

    @Transactional
    public void crearEditorial(String nombre) throws MiException {
        // Valida el valor del atributo, no nulo ni vacío
        validar(nombre);
        // Instancia un objeto del tipo Editorial
        Editorial editorial = new Editorial();
        // Setea el atributo, con el valor recibido como parámetro
        editorial.setNombre(nombre);
        // Persisto el dato en mi BBDD
        editorialRepositorio.save(editorial);
    }

    @Transactional(readOnly = true)
    public List<Editorial> listarEditoriales() {
        List<Editorial> editoriales = new ArrayList<>();
        editoriales = editorialRepositorio.findAll();
        return editoriales;
    }

    // Gets and returns an "editorial" given its id
    @Transactional(readOnly = true)
    public Editorial getOne(String id) {
        return editorialRepositorio.getReferenceById(id);
    }

    @Transactional
    public void modificarEditorial(String nombre, String id) throws MiException {
        // Valida el valor del atributo, no nulo ni vacío
        validar(nombre);
        Optional<Editorial> respuesta = editorialRepositorio.findById(id);
        if (respuesta.isPresent()) {
            Editorial editorial = respuesta.get();
            editorial.setNombre(nombre);
            editorialRepositorio.save(editorial);
        }
    }

    private void validar(String nombre) throws MiException {
        if (nombre.trim().isEmpty() || nombre == null) {
            throw new MiException("El nombre de la editorial es requerido.");
        }
    }

}