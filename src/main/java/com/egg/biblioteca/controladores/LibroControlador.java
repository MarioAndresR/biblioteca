package com.egg.biblioteca.controladores;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.egg.biblioteca.entidades.Autor;
import com.egg.biblioteca.entidades.Editorial;
import com.egg.biblioteca.entidades.Libro;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.servicios.AutorServicio;
import com.egg.biblioteca.servicios.EditorialServicio;
import com.egg.biblioteca.servicios.LibroServicio;

@Controller
@RequestMapping("/libro") // localhost:8080/libro
public class LibroControlador {

    // Crear las instancias de las clases servicio para poder acceder a los métodos
    @Autowired
    private LibroServicio libroServicio;
    @Autowired
    private AutorServicio autorServicio;
    @Autowired
    private EditorialServicio editorialServicio;

    @GetMapping("/registrar") // localhost:8080/libro/registrar
    public String registrar(ModelMap modelo) {
        // Listas de autores y editoriales existentes para mostrar en la vista/UI
        List<Autor> autores = autorServicio.listarAutores();
        List<Editorial> editoriales = editorialServicio.listarEditoriales();
        modelo.addAttribute("autores", autores);
        modelo.addAttribute("editoriales", editoriales);
        return "libro_form.html"; // Vista del formulario de libro.
    }

    @PostMapping("/registro")
    public String registro(
            // Se manejan como no requerido, así entra al método, llegue a la validación
            // en LibroServicio, lanza la excepción y la muestra en la UI usando Model Map
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) Integer ejemplares,
            @RequestParam(required = false) String idAutor,
            @RequestParam(required = false) String idEditorial,
            ModelMap modelo) {
        try {
            libroServicio.crearLibro(isbn, titulo, ejemplares, idAutor, idEditorial);
            // Almacena el mensaje de éxito en la key para poder ser utilizado en la UI
            // (debe existir en libro_form.html)
            modelo.put("exito", "El libro fue creado con éxito.");
        } catch (Exception e) {
            // Cuando hay una excepción y se llama de nuevo el form, también se debe mostrar
            // las listas
            modelo.addAttribute("autores", autorServicio.listarAutores());
            modelo.addAttribute("editoriales", editorialServicio.listarEditoriales());
            Logger.getLogger(LibroControlador.class.getName()).log(Level.SEVERE, null, e);
            // Almacena el mensaje de error en la key para poder ser utilizado en la UI
            // (debe existir en libro_form.html)
            modelo.put("error", e.getMessage());
            return "libro_form.html"; // Vuelve a cargar el formulario.
        }
        return "index.html"; // Si todo sale bien se carga la vista principal.
    }

    @GetMapping("/lista") // localhost:8080/libro/lista
    public String listar(ModelMap modelo) {
        List<Libro> libros = libroServicio.listarLibros();
        modelo.addAttribute("libros", libros);
        return "libro_list.html";
    }

    // Maps the HTTP GET requests (from the data in the listar view/HTML)
    // and gets 'id' for retrieving an entity and send it to the modificar view/HTML
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {
        // Obtiene y almacena el dato/entidad en el modelo para enviarlo al
        // view/HTML y mostrarlo/inyectarlo en la UI
        Libro libro = libroServicio.getOne(id);
        modelo.put("libro", libro);
        // Add "autores" and "editoriales" to show their lists
        modelo.addAttribute("autores", autorServicio.listarAutores());
        modelo.addAttribute("editoriales", editorialServicio.listarEditoriales());

        // id's of the current "autor" and "editorial" to show them to the user
        modelo.addAttribute("autorSeleccionado", libro.getAutor().getId());
        modelo.addAttribute("editorialSeleccionada", libro.getEditorial().getId());
        return "libro_modificar.html";
    }

    // Maps the HTTP POST requests, gets 'id' and the rest of attributes (from the data in the
    // modificar view/HTML) for updating an entity and send it to the listar view/HTML
    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, String isbn, String titulo, Integer ejemplares,
        String idAutor, String idEditorial, ModelMap modelo) {
        try {
            // Add "autores" and "editoriales" to show their lists on the "listar" view/HTML
            modelo.addAttribute("autores", autorServicio.listarAutores());
            modelo.addAttribute("editoriales", editorialServicio.listarEditoriales());
            libroServicio.modificarLibro(id, isbn, titulo, ejemplares, idAutor, idEditorial);
            // Redirecciona a la vista de la tabla libro con la editorial modificada.
            return "redirect:../lista";
        } catch (MiException e) {
            modelo.addAttribute("autores", autorServicio.listarAutores());
            modelo.addAttribute("editoriales", editorialServicio.listarEditoriales());
            Logger.getLogger(LibroControlador.class.getName()).log(Level.SEVERE, null, e);
            modelo.put("error", e.getMessage());
            return "libro_modificar.html";
        }
    }
}