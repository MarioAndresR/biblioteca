package com.egg.biblioteca.controladores;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.egg.biblioteca.entidades.Usuario;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.servicios.UsuarioServicio;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AdminControlador {

    // Crea una instancia de la clase UsuarioServicio para poder acceder a los métodos
    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/dashboard") // localhost:8080/admin/dashboard
    public String panelAdministrativo() {
        return "panel.html";
    }

    @GetMapping("/usuarios")
    public String listar(ModelMap modelo){
        List<Usuario> usuarios = usuarioServicio.listarUsuarios();
        modelo.addAttribute("usuarios", usuarios);
        // Flash attributes will be automatically available here ()
        return "usuario_list";
    }

    // Maps the HTTP GET request (from the data in usuario_list view/HTML) and gets 'id'
    // to retrieve the user, modify its rol and send it back to the list
    @GetMapping("/modificarRol/{id}")
    public String cambiarRol(@PathVariable String id){
        usuarioServicio.cambiarRol(id);
        // Vuelve a cargar la vista con la vista de usuarios, con el cambio
        return "redirect:/admin/usuarios";
    }

    // Dirigido por Acción/Botón Modificar en la vista usuario_list (admin/usuarios)
    // Obtiene el usuario dado el id en la ruta y lo envía a la vista usuario_modificar
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/usuario/{id}")
    public String modificarUsuario(@PathVariable String id, ModelMap modelo){ 
        Usuario usuario = usuarioServicio.getOne(id);
        modelo.addAttribute("usuario", usuario);
        // Para que envíe al form la url que dirige al método post
        modelo.addAttribute("actionUrl", "/admin/modificar/" + usuario.getId()); // 👈 URL for form action
        return "usuario_modificar.html";
    }

    // Método POST para modificar los datos de un usuario, desde admin
    @PostMapping("/modificar/{id}")
    public String modificarUsuarioPorAdmin(
            MultipartFile archivo, 
            @PathVariable String id, 
            @RequestParam String nombre, 
            @RequestParam String email, 
            @RequestParam String password, 
            @RequestParam String password2,
            HttpServletRequest request, // Para actualizar el atributo usuario de la sesión
            RedirectAttributes redirAtt) {
        
        try{
            Usuario updUsuario = usuarioServicio.actualizar(archivo, id, nombre, email, password, password2);
            // Envía el mensaje de éxito al redirect (con ModelMap no enviaba)
            redirAtt.addFlashAttribute("exito", "Usuario actualizado con éxito.");
            // Recarga la sesión del usuario con los nuevos datos del usuario
            // para que se actualice en la vista
            HttpSession session = request.getSession();
            session.setAttribute("usuariosession", updUsuario);
            // Lo envía al redirect
            redirAtt.addFlashAttribute("session", session);
            // Redirige al método que renderiza la lista de usuarios
            return "redirect:../usuarios";
        } catch (MiException ex) {
            redirAtt.addFlashAttribute("error", ex.getMessage());
            // Refill the form with the error, user data
            redirAtt.addFlashAttribute("usuario", usuarioServicio.getOne(id));
            // Redirige al método que renderiza el form de modificar usuario
            return "redirect:../usuario/"+id;
        }
    }

}