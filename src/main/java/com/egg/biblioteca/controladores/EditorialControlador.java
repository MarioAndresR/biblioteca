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

import com.egg.biblioteca.entidades.Editorial;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.servicios.EditorialServicio;

@Controller
@RequestMapping("/editorial") // localhost:8080/editorial
public class EditorialControlador {

    // Crear una instancia de la clase servicio para poder acceder a los métodos
    @Autowired
    private EditorialServicio editoralServicio;

    @GetMapping("/registrar") // localhost:8080/editorial/registrar
    public String registrar() {
        return "editorial_form.html";
    }

    @PostMapping("/registro") // localhost:8080/editorial/registro
    public String registro(@RequestParam String nombre, ModelMap modelo) {
        try {
            // Llegua a la validación en LibroServicio lanza la excepción y la muestra en la UI usando Model Map
            editoralServicio.crearEditorial(nombre); // llama al servicio para persistir
            // Almacena el mensaje de éxito en la key para poder ser utilizado en la UI (debe existir en libro_form.html)
            modelo.put("exito", "La editorial fue creada con éxito.");
        } catch (MiException e) {
            Logger.getLogger(EditorialControlador.class.getName()).log(Level.SEVERE, null, e);
            // Almacena el mensaje de error en la key para poder ser utilizado en la UI (debe existir en libro_form.html)
            modelo.put("error", e.getMessage());
            return "editorial_form.html";
        }
        return "index.html";
    }

    @GetMapping("/lista") // localhost:8080/editorial/lista
    public String listar(ModelMap modelo){
        List<Editorial> editoriales = editoralServicio.listarEditoriales();
        modelo.addAttribute("editoriales", editoriales);
        return "editorial_list.html";
    }

    // Maps the HTTP GET requests (from the data in the listar view/HTML)
    // and gets 'id' for retrieving an entity and send it to the modificar view/HTML
    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable String id, ModelMap modelo) {
        // Obtiene y almacena el dato/entidad en el modelo para enviarlo al
        // view/HTML y mostrarlo/inyectarlo en la UI
        modelo.put("editorial", editoralServicio.getOne(id));
        return "editorial_modificar.html";
    }

    // Maps the HTTP POST requests, gets 'id' and nombre (from the data in the
    // modificar view/HTML) for updating an entity and send it to the listar view/HTML
    @PostMapping("/modificar/{id}")
    public String modificar(@PathVariable String id, String nombre, ModelMap modelo) {
        try {
            editoralServicio.modificarEditorial(nombre, id);
            // Redirecciona a la vista de la tabla editorial con la editorial modificada.
            return "redirect:../lista";
        } catch (MiException e) {
            Logger.getLogger(EditorialControlador.class.getName()).log(Level.SEVERE, null, e);
            modelo.put("error", e.getMessage());
            return "editorial_modificar.html";
        }
    }
}