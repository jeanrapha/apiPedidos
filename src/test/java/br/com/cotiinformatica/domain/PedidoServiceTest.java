package br.com.cotiinformatica.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;

import br.com.cotiinformatica.domain.entities.Pedido;
import br.com.cotiinformatica.domain.enums.StatusPedido;
import br.com.cotiinformatica.domain.exceptions.PedidoNaoEncontradoException;
import br.com.cotiinformatica.domain.models.PedidoRequestModel;
import br.com.cotiinformatica.domain.services.impl.PedidoServiceImpl;
import br.com.cotiinformatica.infrastructure.outbox.OutboxMessage;
import br.com.cotiinformatica.infrastructure.repositories.OutboxMessageRepository;
import br.com.cotiinformatica.infrastructure.repositories.PedidoRepository;

public class PedidoServiceTest {
	// Atributos para cada injeção de dependência
	private PedidoServiceImpl pedidoServiceImpl;
	private PedidoRepository pedidoRepository;
	private OutboxMessageRepository outboxMessageRepository;
	private ModelMapper modelMapper;
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		// Criando os mocks (simulações de comportamento)
		pedidoRepository = mock(PedidoRepository.class);
		outboxMessageRepository = mock(OutboxMessageRepository.class);
		objectMapper = mock(ObjectMapper.class);
		modelMapper = new ModelMapper();

		// Instanciando o PedidoServiceImpl com as dependências mockadas
		pedidoServiceImpl = new PedidoServiceImpl(pedidoRepository, outboxMessageRepository, modelMapper, objectMapper);
	}

	@Test
	@DisplayName("Deve criar um pedido com sucesso.")
	public void testCriarPedidoComSucesso() throws Exception {

		// Preparando os dados do teste
		var request = gerarPedidoRequestModel(); // objeto que iremos enviar na requisição
		var pedido = gerarPedido(UUID.randomUUID(), request); // objeto que queremos obter na resposta

		// Configurando os comportamentos

		/*
		 * Criando um comportamento MOCKADO onde o pedidoRepository irá receber qualquer
		 * objeto Pedido e a partir daí retornar um objeto de 'pedido' gerad na linha 66
		 * pelo método gerarPedido
		 */
		when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

		/*
		 * Criando um comportamento MOCKADO onde o objectMapper irá gerar um JSON fake
		 * contendo o dado "{\"pedido\": true}"
		 */
		when(objectMapper.writeValueAsString(any())).thenReturn("{\"pedido\": true}");

		// Executar a lógica do método
		var response = pedidoServiceImpl.criarPedido(request);

		// Verificando se o retorno não é null
		assertNotNull(response);

		// Verificar se um ID foi obtido
		assertNotNull(response.getId());

		// Verificando se os dados obtidos são iguais aos enviados
		assertEquals(request.getNomeCliente(), response.getNomeCliente());
		assertEquals(request.getDataPedido(), response.getDataPedido());
		assertEquals(request.getValorPedido(), response.getValorPedido());
		assertEquals(request.getDescricaoPedido(), response.getDescricaoPedido());
		assertEquals(request.getStatus(), response.getStatus());

		// Verificando se salvou a mensagem na outbox
		verify(outboxMessageRepository, times(1)).save(any(OutboxMessage.class));
	}

	@Test
	@DisplayName("Deve alterar os dados de um pedido existente com sucesso.")
	public void testAlterarPedidoComSucesso() {

		UUID id = UUID.randomUUID(); // gerando um ID para o pedido

		var request = gerarPedidoRequestModel(); // objeto que iremos enviar na requisição
		var pedido = gerarPedido(id, request); // objeto que queremos obter na resposta

		// configurando os comportamentos do Mockito
		when(pedidoRepository.findByIdAndAtivo(id)).thenReturn(Optional.of(pedido));
		when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

		// Executar a lógica do método
		var response = pedidoServiceImpl.alterarPedido(id, request);

		// Verificando se o retorno não é null
		assertNotNull(response);

		// Verificando se os dados obtidos são iguais aos enviados
		assertEquals(id, response.getId());
		assertEquals(request.getNomeCliente(), response.getNomeCliente());
		assertEquals(request.getDataPedido(), response.getDataPedido());
		assertEquals(request.getValorPedido(), response.getValorPedido());
		assertEquals(request.getDescricaoPedido(), response.getDescricaoPedido());
		assertEquals(request.getStatus(), response.getStatus());
	}

	@Test
	@DisplayName("Deve lançar erro se tentar alterar um pedido não encontrado.")
	public void testAlterarPedidoNaoEncontrado() {

		UUID id = UUID.randomUUID(); // gerando um ID para o pedido

		var request = gerarPedidoRequestModel(); // objeto que iremos enviar na requisição

		// configurando os comportamentos do Mockito
		when(pedidoRepository.findByIdAndAtivo(id)).thenReturn(Optional.empty());

		// executar a lógica de alteração do pedido e verificar se uma exceção é lançada
		// do tipo PedidoNaoEncontradoException
		assertThrows(PedidoNaoEncontradoException.class, () -> {
			pedidoServiceImpl.alterarPedido(id, request);
		});
	}

	@Test
	@DisplayName("Deve inativar um pedido existente com sucesso.")
	public void testInativarPedidoComSucesso() {
		UUID id = UUID.randomUUID(); // gerando um ID para o pedido

		var request = gerarPedidoRequestModel(); // objeto que iremos enviar na requisição
		var pedido = gerarPedido(id, request); // objeto que queremos obter na resposta

		// configurando os comportamentos do Mockito
		when(pedidoRepository.findByIdAndAtivo(id)).thenReturn(Optional.of(pedido));
		when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

		// Executar a lógica do método
		var response = pedidoServiceImpl.inativarPedido(id);

		// Verificando se o retorno não é null
		assertNotNull(response);

		// Verificando se os dados obtidos são iguais aos enviados
		assertEquals(id, response.getId());
		assertEquals(request.getNomeCliente(), response.getNomeCliente());
		assertEquals(request.getDataPedido(), response.getDataPedido());
		assertEquals(request.getValorPedido(), response.getValorPedido());
		assertEquals(request.getDescricaoPedido(), response.getDescricaoPedido());
		assertEquals(request.getStatus(), response.getStatus());
	}

	@Test
	@DisplayName("Deve lançar erro se tentar inativar um pedido não encontrado.")
	public void testInativarPedidoNaoEncontrado() {

		UUID id = UUID.randomUUID(); // gerando um ID para o pedido

		// configurando os comportamentos do Mockito
		when(pedidoRepository.findByIdAndAtivo(id)).thenReturn(Optional.empty());

		// executar a lógica de alteração do pedido e verificar
		// se retornou uma exceção do tipo PedidoNaoEncontradoException
		assertThrows(PedidoNaoEncontradoException.class, () -> {

			// executando o método que deverá falhar
			pedidoServiceImpl.inativarPedido(id);
		});
	}

	@Test
	@DisplayName("Deve consultar pedidos com sucesso.")
	public void testConsultarPedidosComSucesso() {
		//Gerando dois modelos de requisição de pedido
		var request1 = gerarPedidoRequestModel();
		var request2 = gerarPedidoRequestModel();
		
		//Gerando dois pedidos
		var pedido1 = gerarPedido(UUID.randomUUID(), request1);
		var pedido2 = gerarPedido(UUID.randomUUID(), request2);
		
		//Criando uma lista de pedidos
		var pedidos = Arrays.asList(pedido1, pedido2);
		var pageable = PageRequest.of(0, 2);
		var page = new PageImpl<Pedido>(pedidos, pageable, pedidos.size());
		
		//Configurando o comportamento do mock
		when(pedidoRepository.findAtivos(pageable)).thenReturn(page);
		
		//Executando a ação
		var response = pedidoServiceImpl.consultarPedidos(pageable);
		
		//Verificando se o retorno não é null
		assertNotNull(response);
		
		for(var i = 0; i < pedidos.size(); i++) {
			
			var pedido = pedidos.get(i);
			
			assertEquals(pedido.getId(), response.getContent().get(i).getId());
			assertEquals(pedido.getNomeCliente(), response.getContent().get(i).getNomeCliente());
			assertEquals(pedido.getDataPedido(), response.getContent().get(i).getDataPedido());
			assertEquals(pedido.getValorPedido().doubleValue(), response.getContent().get(i).getValorPedido());
			assertEquals(pedido.getDescricaoPedido(), response.getContent().get(i).getDescricaoPedido());
			assertEquals(pedido.getStatus().toString(), response.getContent().get(i).getStatus());
		}						
	}

	@Test
	@DisplayName("Deve obter um pedido através do ID com sucesso.")
	public void testObterPedidoPorIdComSucesso() {
		UUID id = UUID.randomUUID(); //gerando um ID para o pedido
		
		var request = gerarPedidoRequestModel(); //objeto que iremos enviar na requisição
		var pedido = gerarPedido(id, request); //objeto que queremos obter na resposta
		
		//configurando os comportamentos do Mockito
		when(pedidoRepository.findByIdAndAtivo(id)).thenReturn(Optional.of(pedido));
		
		//Executar a lógica do método
		var response = pedidoServiceImpl.obterPedidoPorId(id);
		
		//Verificando se o retorno não é null
		assertNotNull(response);
				
		//Verificando se os dados obtidos são iguais aos enviados
		assertEquals(id, response.getId());
		assertEquals(request.getNomeCliente(), response.getNomeCliente());
		assertEquals(request.getDataPedido(), response.getDataPedido());
		assertEquals(request.getValorPedido(), response.getValorPedido());
		assertEquals(request.getDescricaoPedido(), response.getDescricaoPedido());
		assertEquals(request.getStatus(), response.getStatus());
	}


	
	/*
	 * Método auxiliar para gerar os dados de uma requisição de pedido para os
	 * testes
	 */
	private PedidoRequestModel gerarPedidoRequestModel() {

		var request = new PedidoRequestModel();
		var faker = new Faker();

		request.setNomeCliente(faker.name().fullName());
		request.setDataPedido(LocalDate.now());
		request.setValorPedido(faker.number().randomDouble(2, 100, 1000));
		request.setDescricaoPedido(faker.commerce().department());

		var status = StatusPedido.values();
		var indice = new Random().nextInt(status.length);
		request.setStatus(status[indice].toString());

		return request;
	}

	/*
	 * Método auxiliar para gerar um objeto da entidade Pedido nos testes
	 */
	private Pedido gerarPedido(UUID id, PedidoRequestModel request) {
		var pedido = modelMapper.map(request, Pedido.class);
		pedido.setId(id);

		return pedido;
	}

}
