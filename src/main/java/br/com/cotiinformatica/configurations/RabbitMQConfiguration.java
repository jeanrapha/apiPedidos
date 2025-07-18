package br.com.cotiinformatica.configurations;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfiguration {
	
	/*
	 * Mapeamento de uma fila para gravar / ler
	 * eventos de pedidos criados
	 */
	@Bean
	Queue filaPedidosCriados() {
		/*
		 * 'fila.pedidos_criados' : Nome da fila
		 *  'true' : Durável (a fila será mantida mesmo se o servidor RabbitMQ reiniciar)
		 *  'false' : Exclusiva (a fila não será exclusiva para uma conexão)
		 *  'false' : Auto-deletável (a fila não será deletada automaticamente quando não houver mais consumidores)
		 */
		return new Queue("fila.pedidos_criados", true, false, false);
	}
	

}
