package com.generation.blogpessoal.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*") // permite requisições de qualquer origem e qualquer cabeçalho (pra receber o token de security)
public class PostagemController {
	
	@Autowired // injeção de dependência
	private PostagemRepository postagemRepository;
	
	@GetMapping // mapeamento do método GET
	public ResponseEntity<List<Postagem>> getAll() { // método para listar todas as postagens
		return ResponseEntity.ok(postagemRepository.findAll()); // retorna a lista de postagens (select * from tb_postagens)
	}
	
}
