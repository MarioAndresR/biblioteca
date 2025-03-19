package com.egg.biblioteca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Indica que la clase SeguridadWeb es una clase de configuración de Spring, 
// encargada de definir beans que serán administrados por el contenedor de Spring.
@Configuration
// Habilita la seguridad web en la aplicación, permitiendo definir reglas personalizadas.
@EnableWebSecurity
// Enables Spring Security Method Security: to be able to annotate any Spring-managed 
// class or method with @PreAuthorize, @PostAuthorize...
@EnableMethodSecurity
public class SeguridadWeb {
    // Marca el método como un bean administrado por Spring.
    // Al anotarlo con @Bean, Spring gestionará la instancia de BCryptPasswordEncoder, 
    // asegurando que sea utilizada en toda la aplicación.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    // SecurityFilterChain: define una cadena de filtros de seguridad que procesan las  
    // solicitudes HTTP. Aquí es donde se establecen las reglas de acceso.
    // filterChain: Configura las reglas de seguridad utilizando un objeto HttpSecurity.
    // authorizeHttpRequests: Permite definir qué solicitudes pueden acceder a la aplicación 
    // sin autenticación. En este caso, los archivos CSS, JS e imágenes son de acceso público.
    // csrf.disable(): Deshabilita la protección contra ataques CSRF (Cross-Site Request Forgery).
    // Esto es útil si no estás manejando formularios de autenticación en esta configuración.
    // http.build(): Finaliza la configuración de seguridad y devuelve un objeto SecurityFilterChain configurado.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()// "**" permite el acceso sin loguear
                        .requestMatchers("/login", "/registrar", "/registro").permitAll() // Permitir acceso a login y registrar                    
                        .anyRequest().authenticated()) // Any other request should be authenticated
                .formLogin((form) -> form // Configura el formulario de inicio de sesión
                        .loginPage("/login") // URL de la página de inicio de sesión.
                        .loginProcessingUrl("/logincheck") // Envía datos de inicio de sesión
                        .usernameParameter("email") // Nombre de los campos en el formulario de inicio de sesión
                        .passwordParameter("password")
                        .defaultSuccessUrl("/inicio", true) // URL a la que se redirige el usuario después de sesión exitosa.
                        .permitAll())
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/") // The URL to redirect to after logout has occurred
                        .permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}