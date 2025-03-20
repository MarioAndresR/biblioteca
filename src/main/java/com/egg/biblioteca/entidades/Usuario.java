package com.egg.biblioteca.entidades;

import com.egg.biblioteca.enumeraciones.Rol;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern.Flag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Se define la clase Usuario como entidad que almacenará los usuarios en BD.
// También, se debe definir su clase Repositorio y Servicio.
@Setter
@Getter
@NoArgsConstructor
@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    @Email( regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", 
        flags = Flag.CASE_INSENSITIVE,
        message ="Please provide a valid email address")
    // @NotBlank(message = "El email del empleado es requerido")
    private String email;

    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    private Rol rol;

    // Una imagen está asociada a un solo usuario
    // Lazy Loading, solo se recuperará imagen cuando se llame explícitamente (con un getter u otro método)
    @OneToOne(fetch = FetchType.LAZY)    
    @JoinColumn(nullable = false)
    private Imagen imagen;

}
