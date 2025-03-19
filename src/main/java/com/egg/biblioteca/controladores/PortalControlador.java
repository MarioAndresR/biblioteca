package com.egg.biblioteca.controladores;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
@RequestMapping("/")
public class PortalControlador {

    // Crear una instancia de la clase para poder acceder a los métodos
    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/") // Aquí se realiza el mapeo
    public String index() {
        return "index.html"; // Retorna la vista principal.
    }

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {
        // Envia un atributo que indica la ruta actual "registrar", para que el fragment navbar sepa.
        // Sirve para no mostrar el botón de Sign up en la vista registro.
        modelo.addAttribute("currentPage", "registrar");
        return "registro.html";
    }

    @PostMapping("/registro")
    public String registro(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String password2, 
            @RequestParam MultipartFile archivo,
            ModelMap modelo) {
        try {
            usuarioServicio.registrar(archivo, nombre, email, password, password2);
            modelo.put("exito", "El usuario fue creado correctamente.");
            return "index.html";
        } catch (MiException e) {
            Logger.getLogger(PortalControlador.class.getName()).log(Level.SEVERE, null, e);
            modelo.put("error", e.getMessage());
            // Carga los datos en la vista, cuando ocurre un error
            modelo.put("nombre", nombre);
            modelo.put("email", email);
            return "registro.html"; // Retornamos al formulario de registro con el mensaje de error.
        }
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {
        if (error != null) {
            modelo.put("error", "Usuario o Contraseña inválidos!");
        }
        // Envia un atributo que indica la ruta "login", para que el fragment navbar sepa.
        // Sirve para no mostrar el botón de Log in en la vista login
        modelo.addAttribute("currentPage", "login");
        return "login.html";
    }

    // Method to check if the authenticated user is Admin
    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                   .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));
    }

    // Aquí llega al iniciar sesión, lógica de autorización de acceso
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/inicio")
    // En principio se usó HttpSession para obtener el usuario loggeado
    // Pero la IA recomienda Authentication/SecurityContextHolder o @AuthenticationPrincipal.
    public String inicio(Authentication auth) {
        // Check if the authenticated user is an Admin
        if (isAdmin(auth)) {
            // Si el usuario loggeado es Admin, se redirige a:
            return "redirect:/admin/dashboard";
        }
        // Si el usuario loggeado es USER, renderiza:
        return "inicio.html";
    }

    // ------------------------------------------------------------------
    // ---------------- Get y Post de Actualizar Perfil -----------------
    // ------------------------------------------------------------------

    // Enviado por el botón "Editar Perfil" en el navbar de inicio
    // Muestra el form usuario_modificar.html para usuarios loggeados
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/perfil")
    public String editarPerfil(HttpSession session, ModelMap modelo) {
        // Obtiene los datos del usuario loggeado
        Usuario usuario = (Usuario) session.getAttribute("usuariosession");
        // Envía el usuario a la vista, para mostrar sus datos actuales por defecto
        modelo.put("usuario", usuario);
        // Envía al form el actionUrl, cuyo action dirigirá al método POST actualizarPerfil
        modelo.addAttribute("actionUrl", "/perfil/" + usuario.getId());// 👈 URL for form action
        // Renderiza la vista
        return "usuario_modificar.html";
    }

    // Enviado por el action botón "Actualizar" en usuario_modificar
    // Método POST, recibe el id del usuario y actualiza (Perfil)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/perfil/{id}")
    public String actualizarPerfil(
            MultipartFile archivo, 
            @PathVariable String id, 
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password, 
            @RequestParam String password2,
            Authentication auth, // para obtener el rol del usuario autenticado
            HttpServletRequest request, // Para actualizar el atributo usuario de la sesión
            RedirectAttributes redirAtt) {

        try {
            Usuario updUsuario = usuarioServicio.actualizar(archivo, id, nombre, email, password, password2);
            // Envía mensaje de éxito al redirect (con ModelMap no enviaba)
            redirAtt.addFlashAttribute("exito", "Tu perfil fue actualizado con éxito.");
            // Recarga la sesión del usuario con los nuevos datos del usuario
            // para que se actualice en la vista
            HttpSession session = request.getSession();
            session.setAttribute("usuariosession", updUsuario);
            // Lo envía al redirect
            redirAtt.addFlashAttribute("session", session);
            // Check if the authenticated user is an Admin
            if (isAdmin(auth)) {
                // Si el usuario loggeado es Admin, se redirige a:
                return "redirect:/admin/dashboard";
            }
            // Si el usuario loggeado es USER, renderiza:
            return "inicio.html";
            // // Redirige al método inicio() que renderiza la vista inicial según el rol del usuario
            // return "redirect:/inicio";
        } catch (MiException ex) {
            // Refill the form with the error, usuario and actionUrl
            redirAtt.addFlashAttribute("error", ex.getMessage());
            // Redirige al método que renderiza usuario_modificar.html nuevamemente
            return  "redirect:/perfil";
        }
    }

}
