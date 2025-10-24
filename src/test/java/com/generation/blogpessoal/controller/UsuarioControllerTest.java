package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;
import com.generation.blogpessoal.util.JwtHelper;
import com.generation.blogpessoal.util.TestBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	private static final String BASE_URL = "/usuarios";
	private static final String USUARIO = "root@root.com.br";
	private static final String SENHA = "rootroot";
	
	@BeforeAll
	void inicio() {
		usuarioRepository.deleteAll();
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Root", USUARIO, SENHA));
	}
	
	@Test
	@DisplayName("01 - Deve cadastrar um novo usuário com sucesso")
	void deveCadastrarUsuario() {
		// given
		Usuario usuario = TestBuilder.criarUsuario(null, "Thuany", "thuany@email.com", "12345678");
		// when
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testRestTemplate
				.exchange(BASE_URL + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
		// then
		assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("02 - Não deve cadastrar usuário duplicado")
	void naoDeveCadastrarUsuarioDuplicado() {
		// given
		Usuario usuario = TestBuilder.criarUsuario(null, "Rafaela Lemes", "rafa_lemes@email.com", "12345678");
		usuarioService.cadastrarUsuario(usuario);
		// when
		HttpEntity<Usuario> requisicao = new HttpEntity<Usuario>(usuario);
		ResponseEntity<Usuario> resposta = testRestTemplate
				.exchange(BASE_URL + "/cadastrar", HttpMethod.POST, requisicao, Usuario.class);
		// then
		assertEquals(HttpStatus.BAD_REQUEST, resposta.getStatusCode());
		assertNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("03 - Deve atualizar os dados do usuário com sucesso")
	void deveAtualizarUsuario() {
		// given
		Usuario usuario = TestBuilder.criarUsuario(null, "Nadia", "nadia@email.com", "12345678");
		Optional<Usuario> usuarioCadastrado= usuarioService.cadastrarUsuario(usuario);
		
		Usuario usuarioUpdate = TestBuilder.criarUsuario(usuarioCadastrado.get().getId(), "Nadia Caricatto", "nadia@email.com", "abc12345");
		// when
		// gerar token
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA);
		//criar requisicao com token
		HttpEntity<Usuario> requisicao = JwtHelper.criarRequisicaoComToken(usuarioUpdate, token);
		// enviar requisicao PUT
		ResponseEntity<Usuario> resposta = testRestTemplate
				.exchange(BASE_URL + "/atualizar", HttpMethod.PUT, requisicao, Usuario.class);
		// then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("04 - Deve listar todos os usuários com sucesso")
	void deveListarTodosUsuarios() {
		
		// given
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Cintia Dourado", 
				"cintia@email.com", "12345678"));
		usuarioService.cadastrarUsuario(TestBuilder.criarUsuario(null, "Aline Romanini", 
				"aline@email.com", "12345678"));
		
		// when
		String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA);
		HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
		ResponseEntity<Usuario[]> resposta = testRestTemplate
				.exchange(BASE_URL + "/all", HttpMethod.GET, requisicao, Usuario[].class);
		
		// then
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
		assertNotNull(resposta.getBody());
	}
	
	@Test
	@DisplayName("05 - Deve listar um usuário por ID com sucesso")
	void deveListarUsuarioPorId() {

	    // given
	    Usuario usuario = TestBuilder.criarUsuario(null, "Cristina Coelho",
	            "cris@email.com", "12345678");
	    Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(usuario);
	    assertTrue(usuarioCadastrado.isPresent(), "Usuário deveria ter sido cadastrado");

	    Long id = usuarioCadastrado.get().getId();

	    // when
	    String token = JwtHelper.obterToken(testRestTemplate, USUARIO, SENHA);
	    HttpEntity<Void> requisicao = JwtHelper.criarRequisicaoComToken(token);
	    ResponseEntity<Usuario> resposta = testRestTemplate
	    		.exchange(BASE_URL + "/" + id, HttpMethod.GET, requisicao, Usuario.class);

	    // then
	    assertEquals(HttpStatus.OK, resposta.getStatusCode());
	    assertNotNull(resposta.getBody());
	    assertEquals(id, resposta.getBody().getId());
	    assertEquals("Cristina Coelho", resposta.getBody().getNome()); // não precisa, mas é pra garantir que não retornou só
	    																// algum usuário com esse id e sim o mesmo que cadastrou
	}
}
