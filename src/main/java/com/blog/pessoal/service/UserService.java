package com.blog.pessoal.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.blog.pessoal.model.User;
import com.blog.pessoal.model.UserLogin;
import com.blog.pessoal.repository.UserRepository;
import com.blog.pessoal.security.JwtService;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	public Optional<User> cadastrarUsuario(User user) {

		if (userRepository.findByUsuario(user.getUsuario()).isPresent())
			return Optional.empty();

		user.setSenha(criptografarSenha(user.getSenha()));

		return Optional.of(userRepository.save(user));

	}

	public Optional<User> atualizarUsuario(User user) {

		if (userRepository.findById(user.getId()).isPresent()) {

			Optional<User> buscaUsuario = userRepository.findByUsuario(user.getUsuario());

			if ((buscaUsuario.isPresent()) && (buscaUsuario.get().getId() != user.getId()))
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);

			user.setSenha(criptografarSenha(user.getSenha()));

			return Optional.ofNullable(userRepository.save(user));

		}

		return Optional.empty();

	}

	public Optional<UserLogin> autenticarUsuario(Optional<UserLogin> userLogin) {

		
		var credenciais = new UsernamePasswordAuthenticationToken(userLogin.get().getUsuario(),
				userLogin.get().getSenha());

	
		Authentication authentication = authenticationManager.authenticate(credenciais);

	
		if (authentication.isAuthenticated()) {

			
			Optional<User> usuario = userRepository.findByUsuario(userLogin.get().getUsuario());

			
			if (usuario.isPresent()) {

				
				userLogin.get().setId(usuario.get().getId());
				userLogin.get().setNome(usuario.get().getNome());
				userLogin.get().setFoto(usuario.get().getFoto());
				userLogin.get().setToken(gerarToken(userLogin.get().getUsuario()));
				userLogin.get().setSenha("");

			
				return userLogin;

			}

		}

		return Optional.empty();

	}

	private String criptografarSenha(String senha) {

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		return encoder.encode(senha);

	}

	private String gerarToken(String usuario) {
		return "Bearer " + jwtService.generateToken(usuario);
	}

}
