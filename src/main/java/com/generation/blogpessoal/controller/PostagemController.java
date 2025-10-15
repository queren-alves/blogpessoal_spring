package com.generation.blogpessoal.controller;

import java.util.List;
import java.util.Optional;

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

import com.generation.blogpessoal.model.Postagem;
import com.generation.blogpessoal.repository.PostagemRepository;
import com.generation.blogpessoal.repository.TemaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/postagens")
@CrossOrigin(origins = "*", allowedHeaders = "*") // permite requisições de qualquer origem e qualquer cabeçalho (pra receber o token de security)
public class PostagemController {
	
	@Autowired // injeção de dependência
	private PostagemRepository postagemRepository;
	
	@Autowired 
	private TemaRepository temaRepository;
	
	@GetMapping // mapeamento do método GET
	public ResponseEntity<List<Postagem>> getAll() { // método para listar todas as postagens
		return ResponseEntity.ok(postagemRepository.findAll()); // retorna a lista de postagens (select * from tb_postagens)
	}
	
	@GetMapping("/{id}") // mapeamento do método GET por id (variavel de caminho)
	public ResponseEntity<Postagem> getById(@PathVariable Long id) { // método para listar postagem por id
		return postagemRepository.findById(id) // busca postagem por id (select * from tb_postagens where id = id)
				.map(resposta -> ResponseEntity.ok(resposta)) // se encontrar, retorna a postagem com status 200 (OK)
				.orElse(ResponseEntity.notFound().build()); // se não encontrar, retorna status 404 (Not Found)
	}
	
	@GetMapping("/titulo/{titulo}") // mapeamento do método GET por título (variavel de caminho)
	public ResponseEntity<List<Postagem>> getAllByTitulo(@PathVariable String titulo) { // método para listar postagem por  título
		return ResponseEntity.ok(postagemRepository.findAllByTituloContainingIgnoreCase(titulo)); // retorna a lista de postagens que contém o título (select * from tb_postagens where titulo like %titulo%)																																																																																																					
	}
	
	@PostMapping // mapeamento do método POST
	public ResponseEntity<Postagem> post(@Valid @RequestBody Postagem postagem) { // método para criar uma nova postagem (@Valid valida os campos da postagem conforme as anotações na classe Postagem) e (@RequestBody indica que o objeto postagem será recebido no corpo da requisição)
		
		if (temaRepository.existsById(postagem.getTema().getId())) { // verifica se o tema existe (select * from tb_temas where id = id)
																		
			postagem.setId(null); // garante que o id será nulo para criar uma nova postagem (o frontend envia id 0, entao da erro no banco de dados)
			
			return ResponseEntity.status(201).body(postagemRepository.save(postagem)); // salva a postagem no banco de dados e retorna a postagem com status 201 (Created)																																								
		}
		
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Tema não existe.", null); // se o tema não existir, lança exceção com status 400 (Bad Request)
	}
	
	@PutMapping // mapeamento do método PUT
	public ResponseEntity<Postagem> put(@Valid @RequestBody Postagem postagem) { // método para atualizar uma postagem existente
		
		if (postagemRepository.existsById(postagem.getId())) { // verifica se a postagem existe (select * from tb_postagens where id = id)
			
			if (temaRepository.existsById(postagem.getTema().getId())) { // verifica se o tema existe (select * from tb_temas where id = id)
									
				return ResponseEntity.status(HttpStatus.OK).body(postagemRepository.save(postagem)); // salva a postagem no banco de dados e retorna a postagem com status 201 (Created)																																								
			}
			
		    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Tema não existe.", null); // se o tema não existir, lança exceção com status 400 (Bad Request)
		}
	
		return ResponseEntity.notFound().build(); // se a postagem não existir, retorna status 404 (Not Found)
	}
	
	@ResponseStatus(HttpStatus.NO_CONTENT) // mapeamento do método DELETE
	@DeleteMapping("/{id}") // mapeamento do método DELETE por id
	public void delete(@PathVariable Long id) { // método para deletar uma postagem por id
		Optional<Postagem> postagem = postagemRepository.findById(id); // busca postagem por id (select * from tb_postagens where id = id)
		if(postagem.isEmpty()) // verifica se a postagem existe
			throw new ResponseStatusException(HttpStatus.NOT_FOUND); // se não existir, lança exceção com status 404 (Not Found)
		postagemRepository.deleteById(id); // se existir, deleta a postagem (delete from tb_postagens where id = id)
	}
}
