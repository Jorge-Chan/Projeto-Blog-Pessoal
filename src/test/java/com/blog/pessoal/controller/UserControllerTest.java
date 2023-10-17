package com.blog.pessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;


import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.blog.pessoal.model.User;
import com.blog.pessoal.repository.UserRepository;
import com.blog.pessoal.service.UserService;



@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private UserService userService;

	@Autowired
	private  UserRepository userRepository;
	
	
	@BeforeAll
	void start(){

		userRepository.deleteAll();

		userService.cadastrarUsuario(new User(0L, 
			"Root", "root@root.com", "rootroot", "-"));

	}
	
	@Test
	@DisplayName("Cadastrar Um Usuário")
	public void deveCriarUmUsuario() {

		HttpEntity<User> corpoRequisicao = new HttpEntity<User>(new User(0L, 
			"Paulo Antunes", "paulo_antunes@email.com.br", "13465278", "-"));

		ResponseEntity<User> corpoResposta = testRestTemplate
			.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, User.class);

		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	
	}
	
	@Test
	@DisplayName("Não deve permitir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {

		userService.cadastrarUsuario(new User(0L, 
			"Maria da Silva", "maria_silva@email.com.br", "13465278", "-"));

		HttpEntity<User> corpoRequisicao = new HttpEntity<User>(new User(0L, 
			"Maria da Silva", "maria_silva@email.com.br", "13465278", "-"));

		ResponseEntity<User> corpoResposta = testRestTemplate
			.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, User.class);

		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {

		Optional<User> usuarioCadastrado = userService.cadastrarUsuario(new User(0L, 
			"Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "-"));

		User usuarioUpdate = new User(usuarioCadastrado.get().getId(), 
			"Juliana Andrews Ramos", "juliana_ramos@email.com.br", "juliana123" , "-");
		
		HttpEntity<User> corpoRequisicao = new HttpEntity<User>(usuarioUpdate);

		ResponseEntity<User> corpoResposta = testRestTemplate
			.withBasicAuth("root@root.com", "rootroot")
			.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, User.class);

		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
		
	}
	
	@Test
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {

		userService.cadastrarUsuario(new User(0L, 
			"Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "-"));
		
		userService.cadastrarUsuario(new User(0L, 
			"Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "-"));

		ResponseEntity<String> resposta = testRestTemplate
		.withBasicAuth("root@root.com", "rootroot")
			.exchange("/usuarios/all", HttpMethod.GET, null, String.class);

		assertEquals(HttpStatus.OK, resposta.getStatusCode());

	}

}
