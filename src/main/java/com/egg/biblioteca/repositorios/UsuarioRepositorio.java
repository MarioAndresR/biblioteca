package com.egg.biblioteca.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.egg.biblioteca.entidades.Usuario;

@Repository
// El repositorio de usuarios es una interfaz que extienda JpaRepository.
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {

    // Busca usuarios por su correo electrónico, que en la mayoría de los casos, 
    // se utiliza como nombre de usuario.
    @Query("SELECT u FROM Usuario u WHERE u.email = :email")
    public Usuario buscarPorEmail(@Param("email") String email);
}
