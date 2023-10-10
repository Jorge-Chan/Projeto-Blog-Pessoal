package com.blog.pessoal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.blog.pessoal.model.Postagens;
import com.blog.pessoal.repository.PostagensRepository;
import com.blog.pessoal.repository.TemaRepository;


import jakarta.validation.Valid;




@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*" , allowedHeaders = "*")
public class PostagensController {

	@Autowired
	private PostagensRepository postagensRepository;
	
	@Autowired
	private TemaRepository temaRepository;
	
	@GetMapping
	public ResponseEntity<List<Postagens>> getAll(){
		return ResponseEntity.ok(postagensRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Postagens> getById(@PathVariable Long id){
		return postagensRepository.findById(id)
				.map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}	
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagens>> gerByTitulo(@PathVariable String titulo){
		return ResponseEntity.ok(postagensRepository.findAllByTituloContainingIgnoreCase(titulo));
	}
	@PostMapping
	public ResponseEntity<Postagens> post(@Valid @RequestBody Postagens postagens) {
		if (temaRepository.existsById(postagens.getTema().getId()))
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(postagensRepository.save(postagens));
		
		throw new  ResponseStatusException(HttpStatus.BAD_REQUEST, "Tema não existente", null);
	}
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
	java.util.Optional<Postagens> postagens = postagensRepository.findById(id);
	
	if(postagens.isEmpty())
		throw new ResponseStatusException(HttpStatus.NOT_FOUND);
	
	postagensRepository.deleteById(id);
	
	}
	@PutMapping 
	public ResponseEntity<Postagens> put(@Valid @RequestBody Postagens postagens){
		if (postagensRepository.existsById(postagens.getId())) {
			
			if(temaRepository.existsById(postagens.getTema().getId()))
				return ResponseEntity.status(HttpStatus.OK)
						.body(postagensRepository.save(postagens));
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria não existe!", null);
			
		}
		return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	}
}
