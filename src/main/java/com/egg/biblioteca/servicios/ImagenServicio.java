package com.egg.biblioteca.servicios;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.egg.biblioteca.entidades.Imagen;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.repositorios.ImagenRepositorio;

@Service
public class ImagenServicio {

    @Autowired
    private ImagenRepositorio imagenRepositorio;

    // Método para guardar una nueva imagen
    @Transactional
    public Imagen guardar(MultipartFile archivo) throws MiException {
        validarArchivo(archivo);
        try {
            Imagen imagen = new Imagen();
            imagen.setNombre(archivo.getOriginalFilename());
            imagen.setMime(archivo.getContentType());
            imagen.setContenido(archivo.getBytes());
            return imagenRepositorio.save(imagen);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return null;
    }

    // Método para actualizar una imagen existente
    public Imagen actualizar(MultipartFile archivo, String idImagen) throws MiException {
        validarArchivo(archivo);
        try {
            Imagen imagen = new Imagen();
            validarIdImagen(idImagen);
            // Si se ingresó idImagen, obtiene la entidad correspondiente
            Optional<Imagen> respuesta = imagenRepositorio.findById(idImagen);
            // Si la entidad existe, la obtiene
            if (respuesta.isPresent()) {
                imagen = respuesta.get();
            }
            // Setea los nuevos valores
            imagen.setNombre(archivo.getOriginalFilename());
            imagen.setMime(archivo.getContentType());
            imagen.setContenido(archivo.getBytes());
            // Persiste en BD
            return imagenRepositorio.save(imagen);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return null;
    }

    // Método para listar todas las imágenes
    @Transactional(readOnly = true)
    public List<Imagen> listarTodos() {
        return imagenRepositorio.findAll();
    }

    private void validarArchivo(MultipartFile archivo) throws MiException {
        if (archivo.isEmpty()) {
            throw new MiException("El archivo es requerido.");
        }
    }

    private void validarIdImagen(String id) throws MiException {
        if (id == null || id.isEmpty()) {
            throw new MiException("El id de la imagen es requerido.");
        }
    }

}
