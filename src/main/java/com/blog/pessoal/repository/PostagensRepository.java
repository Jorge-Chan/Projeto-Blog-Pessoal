package com.blog.pessoal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.blog.pessoal.model.Postagens;


public interface PostagensRepository extends JpaRepository<Postagens, Long>{

	public List <Postagens> findAllByTituloContainingIgnoreCase(@Param("titulo") String titulo);
	
}
