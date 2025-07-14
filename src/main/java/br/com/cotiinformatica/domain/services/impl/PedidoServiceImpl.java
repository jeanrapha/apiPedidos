package br.com.cotiinformatica.domain.services.impl;
import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import br.com.cotiinformatica.domain.entities.Pedido;
import br.com.cotiinformatica.domain.models.PedidoRequestModel;
import br.com.cotiinformatica.domain.models.PedidoResponseModel;
import br.com.cotiinformatica.domain.services.interfaces.PedidoService;
import br.com.cotiinformatica.infrastructure.repositories.PedidoRepository;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
		
	private final PedidoRepository pedidoRepository;
	private final ModelMapper mapper;
	
	@Override
	public PedidoResponseModel criarPedido(PedidoRequestModel model) {
	
		var pedido = mapper.map(model, Pedido.class);
		pedido.setAtivo(true);
		
		pedidoRepository.save(pedido);
		
		return mapper.map(pedido, PedidoResponseModel.class);
	}
	@Override
	public PedidoResponseModel alterarPedido(UUID id, PedidoRequestModel model) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PedidoResponseModel inativarPedido(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Page<PedidoResponseModel> consultarPedidos(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public PedidoResponseModel obterPedidoPorId(UUID id) {
		// TODO Auto-generated method stub
		return null;
	}
}
