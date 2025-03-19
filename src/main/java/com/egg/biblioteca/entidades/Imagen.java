package com.egg.biblioteca.entidades;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Imagen {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String nombre;

    // Para identificar el formato de la imagen (png, jpeg)
    @Column(nullable = false)
    private String mime;

    @Lob
    // Lazy Loading, solo se recuperará cuando se llame explícitamente (con un getter u otro método)
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGBLOB")
    private byte[] contenido;

    // @OneToOne(mappedBy = "imagen") // Correct bidirectional mapping
    // private Usuario usuario;
    
}
