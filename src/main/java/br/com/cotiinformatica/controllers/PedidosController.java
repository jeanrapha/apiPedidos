package br.com.cotiinformatica.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cotiinformatica.domain.entities.Pedido;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/pedidos")
@Tag(name = "Controle de pedidos", description = "Serviços para gerenciamento de solicitiações de pedidos.")

public class PedidosController {

	@Operation(summary = "Cadastro de solicitações de pedido.", description = "Cria uma nova solicitação de pedido no sistema.")
	@PostMapping
	public ResponseEntity<?> post() {
		// TODO Implementar o cadastro do pedido
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Atualização de pedido.", description = "Modifica uma solicitação de pedido existente no sistema.")
	@PutMapping
	public ResponseEntity<?> put() {
		// TODO Implementar o atualização do pedido
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Inativação de solicitações de pedido.", description = "Inativa uma solicitação de pedido existente no sistema.")
	@DeleteMapping
	public ResponseEntity<?> delete() {
		// TODO Implementar o exclusão do pedido
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Consulta de solicitações de pedido.", description = "Retorna uma consulta paginada de solicitações de pedidos no sistema.")
	@GetMapping
	public ResponseEntity<?> get() {
		// TODO Implementar a consulta de pedidos
		List<Pedido> pedidos = new ArrayList<>();
		/*
		 * pedidos.add(new Pedido(1L, "Pedido 1")); pedidos.add(new Pedido(2L,
		 * "Pedido 2")); pedidos.add(new Pedido(3L, "Pedido 3"));
		 */

		return ResponseEntity.ok(pedidos);
	}

}
