package com.blog.pessoal.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.pessoal.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public Optional<User> findByUsuario(String usuario);
}
