package com.egg.biblioteca.controladores;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
// El RequestMapping está a nivel del método
// La clase implementa la interfaz ErrorController
public class ErroresControlador implements ErrorController {

    // Método para manejar las solicitudes a la ruta "/error".
    // Todo recurso que tenga "/error" (GET/POST), ingresa a este método.
    @RequestMapping(value = "/error", method = { RequestMethod.GET, RequestMethod.POST })
    // El método recibe una petición HTTP como parámetro y retorna la vista con los datos
    // del error de manera personalizada.
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {
        
        // ModelAndView funciona similar al ModelMap para enviar datos a la vista. Además,
        // retorna la vista. Entonces, se crea un objeto ModelAndView llamado "errorPage",
        // con la página/vista de error "error.html" para retornarla con el código y mensaje de error.
        ModelAndView errorPage = new ModelAndView("error");

        // Para personalizar el mensaje de error acorde con el código de error.
        String errorMsg = "";

        // Recupera el código de error que viene del servidor.
        int httpErrorCode = getErrorCode(httpRequest);

        if (httpErrorCode == -1) {
            httpErrorCode = 500; // Asigna un código de error por defecto
        }
        
        switch (httpErrorCode) {
            case 400: {
                errorMsg = "Solicitud incorrecta, aparente error de su parte.";
                break;
            }
            case 401: {
                errorMsg = "Sus credenciales de acceso no son válidas.";
                break;
            }
            case 403: {
                errorMsg = "Acceso denegado, no cuenta con los permisos necesarios.";
                break;
            }
            case 404: {
                errorMsg = "El recurso solicitado no fue encontrado.";
                break;
            }
            case 500: {
                errorMsg = "Ocurrió un error interno del servidor.";
                break;
            }
            default: {
                errorMsg = "Se produjo un error inesperado.";
                break;
            }
        }

        errorPage.addObject("codigo", httpErrorCode);
        errorPage.addObject("mensaje", errorMsg);
        // Retorna y renderiza la vista con los vaores de codigo y mensaje
        return errorPage;
    }

    // Recibe la petición y obtiene su atributo código de estado del error, como Integer
    private int getErrorCode(HttpServletRequest httpRequest) {
        // Obtiene el atributo código de estado del error, tipo Integer ("jakarta.servlet.error.status_code")
        Object statusCode = httpRequest.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        // Si el atributo es null O no es una instancia de Integer, 
        // retorna un valor por defecto, en este caso -1
        return (statusCode instanceof Integer) ? (Integer) statusCode : -1 ;
    }
}
