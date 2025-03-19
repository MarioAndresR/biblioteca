package com.egg.biblioteca.servicios;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.egg.biblioteca.entidades.Imagen;
import com.egg.biblioteca.entidades.Usuario;
import com.egg.biblioteca.enumeraciones.Rol;
import com.egg.biblioteca.excepciones.MiException;
import com.egg.biblioteca.repositorios.UsuarioRepositorio;

import jakarta.servlet.http.HttpSession;

// Servicio que se conecte al repositorio e implementa la interfaz UserDetailsService.
@Service
public class UsuarioServicio implements UserDetailsService {

    // Crear una instancia de la clase para poder acceder a los métodos
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ImagenServicio imagenServicio;

    // loadUserByUsername: Forma parte de la implementación de UserDetailsService y
    // su función es cargar los datos del usuario con base en su correo electrónico.
    // Cuando un usuario se loggea, Spring Security se dirige a este método y otorga
    // los permisos a los que tiene acceso el usuario.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Obtiene el usuario con el correo dado
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);

        if (usuario != null) {
            // Crea una lista de permisos
            List<GrantedAuthority> permisos = new ArrayList<>();
            // Agrega el permiso correspondiente al rol del usuario.
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
            permisos.add(p);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario); // setea el usuario que inició sesión

            // Devuelve un objeto UserDetails con el email, la contraseña y los permisos.
            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        } else {
            return null;
        }
    }

    @Transactional
    public void registrar(MultipartFile archivo, String nombre, String email, String password, String password2) throws MiException {
        // Valida ingreso de datos, archivo de imagen y que las contraseñas coincidan
        validar(archivo, nombre, email, password, password2);

        Usuario usuario = new Usuario();

        // Guarda la nueva imagen en BD
        Imagen imagen = imagenServicio.guardar(archivo);
        // Setea la imagen del usuario
        usuario.setImagen(imagen);

        usuario.setNombre(nombre);
        usuario.setEmail(email);
        // Setea la contraseña encriptada
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setRol(Rol.USER);

        // Persiste los datos del nuevo usuario en BD
        usuarioRepositorio.save(usuario);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios = usuarioRepositorio.findAll();
        return usuarios;
    }

    // Gets and returns an "usuario" given its id
    @Transactional(readOnly = true)
    public Usuario getOne(String id) {
        return usuarioRepositorio.getReferenceById(id);
    }

    // Gets and returns an "usuario" given its Email
    @Transactional(readOnly = true)
    public Usuario getUserByEmail(String email) {
        return usuarioRepositorio.buscarPorEmail(email);
    }

    @Transactional
    public Usuario actualizar(MultipartFile archivo, String id, String nombre, String email, String password, String password2) throws MiException {

        validar(archivo, nombre, email, password, password2);

        Optional<Usuario> respuesta = usuarioRepositorio.findById(id);

        if (respuesta.isEmpty()) {
            throw new MiException("El usuario especificado no existe.");
        }

        Usuario usuario = respuesta.get();

        Imagen imagen = new Imagen();
        // Si el usuario ya tiene img asociada
        if (usuario.getImagen() != null) {
            // Actualiza la imagen existente
            imagen = imagenServicio.actualizar(archivo, usuario.getImagen().getId() );
        } else {
            // Si el usuario no tiene imagen asociada, crea una nueva
            imagen = imagenServicio.guardar(archivo);
        }
        // Setea la imagen del usuario
        usuario.setImagen(imagen);
        
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        // El uso de BCryptPasswordEncoder en Spring Security es una práctica
        // recomendada para almacenar contraseñas de manera segura en la base de datos.
        usuario.setPassword(new BCryptPasswordEncoder().encode(password)); // Encripta y setea el password

        // Persiste los datos del usuario
        return usuarioRepositorio.save(usuario);
    }

    @Transactional
    public void cambiarRol(String id){
        Optional<Usuario> usuarioRespuesta = usuarioRepositorio.findById(id);

        if (usuarioRespuesta.isPresent()) {
            Usuario usuario = usuarioRespuesta.get();

            if (usuario.getRol().equals(Rol.ADMIN)) {
                usuario.setRol(Rol.USER);
            } else if (usuario.getRol().equals(Rol.USER)) {
                usuario.setRol(Rol.ADMIN);
            }

            // usuarioRepositorio.save(usuario);
        }
    }

    private void validar(MultipartFile archivo, String nombre, String email, String password, String password2) throws MiException {
        if (archivo.isEmpty()) { // no file has been chosen or the chosen file has no content
            throw new MiException("El archivo es requerido.");
        }
        if (nombre == null || nombre.isEmpty()) {
            throw new MiException("El nombre es requerido.");
        }
        if (email == null || email.isEmpty()) {
            throw new MiException("El email es requerido.");
        }
        if (password == null || password.isEmpty()) {
            throw new MiException("La contraseña es requerida.");
        }
        if (password.length() <= 5) {
            throw new MiException("La contraseña debe tener más de 5 dígitos.");
        }
        if (!password.equals(password2)) {
            throw new MiException("Las contraseñas ingresadas deben ser iguales.");
        }
    }

}
