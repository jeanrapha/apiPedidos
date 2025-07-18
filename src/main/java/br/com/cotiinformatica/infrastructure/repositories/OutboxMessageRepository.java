package br.com.cotiinformatica.infrastructure.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.cotiinformatica.infrastructure.outbox.OutboxMessage;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessage, Long> {

	/*
	 * Método de consulta com JPQL que retorne todos os registros
	 * de mensagens que não foram transmitidos de um determinado evento
	 */
	@Query("""
			SELECT o FROM OutboxMessage o
			WHERE o.published = false
			  AND o.type = :type
			ORDER BY o.createdAt ASC
			""")
	List<OutboxMessage> find(@Param("type") String type, Pageable pageable);
	
	
	// Método para buscar mensagens não publicadas
	List<OutboxMessage> findByPublishedFalse();

	// Método para atualizar o status de publicação de uma mensagem
	@Modifying
	@Query("UPDATE OutboxMessage om SET om.published = true, om.transmittedAt = CURRENT_TIMESTAMP WHERE om.id = :id")
	void markAsPublished(@Param("id") Long id);

}
