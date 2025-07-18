package br.com.cotiinformatica.infrastructure.components;


import java.time.LocalDateTime;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import br.com.cotiinformatica.infrastructure.repositories.OutboxMessageRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PedidoCriadoProducer {
	private final RabbitTemplate rabbitTemplate;
	private final OutboxMessageRepository outboxMessageRepository;
	private final Queue queue;
	
	@Scheduled(fixedDelay = 60000)
	public void enviarPedidosCriados() {
		// Busca mensagens pendentes na tabela OutboxMessage
		Pageable pageable = PageRequest.of(0, 20); // Pega as primeiras 20 mensagens
		
		var messages = outboxMessageRepository.find("PedidoCriado", pageable);

		try { 
			for (var message : messages) {
				// Envia a mensagem para a fila RabbitMQ
				rabbitTemplate.convertAndSend(queue.getName(), message.getPayload());
	
				// Marca a mensagem como publicada
				message.setPublished(true);
				message.setTransmittedAt(LocalDateTime.now());
				outboxMessageRepository.save(message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
