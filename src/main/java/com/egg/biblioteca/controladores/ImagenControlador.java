package com.egg.biblioteca.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.egg.biblioteca.entidades.Imagen;
import com.egg.biblioteca.entidades.Usuario;
import com.egg.biblioteca.servicios.UsuarioServicio;

// import com.egg.biblioteca.servicios.UsuarioServicio;

@Controller
@RequestMapping("/imagen")
public class ImagenControlador {
    // Crea una instancia de la clase para poder acceder a los métodos
    // @Autowired
    // private ImagenServicio imagenServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;

    // El campo "Foto de Perfil" de usuario_list, toma la fuente desde este método
    // GET Request, con el id del usuario obtiene la imagen de perfil y la retorna
    // con ResponseEntity<byte[]> que encapsula contenido binario (imágenes o archivos).
    @GetMapping("/perfil/{id}")
    public ResponseEntity<byte[]> imagenUsuario(@PathVariable String id){
        Usuario usuario = usuarioServicio.getOne(id);
        // Obtiene la imagen del usuario

        Imagen imagen = usuario.getImagen();
        if (usuario == null || usuario.getImagen() == null || usuario.getImagen().getContenido() == null) {
            // Shorthand for returning an HTTP 404 Not Found response.
            return ResponseEntity.notFound().build();
        }
        
        // HttpHeaders Indica al navegador que el contenido es una imagen
        // Setea los parámetros de las cabeceras para enviar la imagen
        HttpHeaders responseHeaders  = new HttpHeaders();
        // Set content type (image/png, image/jpeg, etc.)
        responseHeaders.setContentType(MediaType.parseMediaType(imagen.getMime()));
        // Specifies the size of the response in bytes. Not mandatory but 
        // recommended for performance optimization.
        responseHeaders.setContentLength(imagen.getContenido().length);

        return new ResponseEntity<>(imagen.getContenido(), responseHeaders, HttpStatus.OK);
    }

    // Esto no es necesario, el post mapping que actualiza el usuario
    // en PortalControlador, también actualiza la imagen con usuarioServicio.actualizar

    // // Subir imagen de perfil para un usuario
    // @PostMapping("/perfil/{id}")
    // public ResponseEntity<String> actualizarImagenPerfil(@PathVariable String id,
    //         @RequestParam("archivo") MultipartFile archivo) {
    //     try {
    //         // Llamada al servicio para guardar la imagen
    //         imagenServicio.actualizar(archivo, id);
    //         return new ResponseEntity<>("Imagen actualizada exitosamente", HttpStatus.OK);
    //     } catch (Exception e) {
    //         return new ResponseEntity<>("Error al actualizar la imagen", HttpStatus.BAD_REQUEST);
    //     }
    // }
}
