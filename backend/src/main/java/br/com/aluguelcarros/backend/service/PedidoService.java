
package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.dto.PedidoRequest;
import br.com.aluguelcarros.backend.model.Pedido;
import br.com.aluguelcarros.backend.model.Cliente;
import br.com.aluguelcarros.backend.model.Automovel;
import br.com.aluguelcarros.backend.model.enums.StatusPedido;
import br.com.aluguelcarros.backend.repository.PedidoRepository;
import br.com.aluguelcarros.backend.repository.ClienteRepository;
import br.com.aluguelcarros.backend.repository.AutomovelRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final AutomovelRepository automovelRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            AutomovelRepository automovelRepository) {

        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.automovelRepository = automovelRepository;
    }

    // Criar um novo pedido
    public Pedido criar(PedidoRequest dados) {

        Cliente cliente = clienteRepository.findById(dados.getClienteId())
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado"));

        Automovel automovel = automovelRepository.findById(dados.getAutomovelId())
                .orElseThrow(() ->
                        new RuntimeException("Automóvel não encontrado"));

        Pedido pedido = new Pedido();

        pedido.setCliente(cliente);
        pedido.setAutomovel(automovel);
        pedido.setDataCriacao(LocalDate.now());
        pedido.setDataInicio(dados.getDataInicio());
        pedido.setDataFim(dados.getDataFim());
        pedido.setValorTotal(dados.getValorTotal());
        pedido.setStatus(StatusPedido.PENDENTE);

        return pedidoRepository.save(pedido);
    }

    // Listar todos os pedidos
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    // Buscar um pedido pelo ID
    public Pedido buscarPorId(Long id) {

        return pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado"));
    }

    // Atualizar um pedido
    public Pedido atualizar(Long id, PedidoRequest dados) {

        Pedido pedido = buscarPorId(id);

        Cliente cliente = clienteRepository.findById(dados.getClienteId())
                .orElseThrow(() ->
                        new RuntimeException("Cliente não encontrado"));

        Automovel automovel = automovelRepository.findById(dados.getAutomovelId())
                .orElseThrow(() ->
                        new RuntimeException("Automóvel não encontrado"));

        pedido.setCliente(cliente);
        pedido.setAutomovel(automovel);
        pedido.setDataInicio(dados.getDataInicio());
        pedido.setDataFim(dados.getDataFim());
        pedido.setValorTotal(dados.getValorTotal());

        return pedidoRepository.save(pedido);
    }

    // Cancelar um pedido
    public Pedido cancelar(Long id) {

        Pedido pedido = buscarPorId(id);

        pedido.setStatus(StatusPedido.CANCELADO);

        return pedidoRepository.save(pedido);
    }

    // Avaliar um pedido
    public Pedido avaliar(Long id, String parecer, StatusPedido status) {

        Pedido pedido = buscarPorId(id);

        if (status != StatusPedido.APROVADO
                && status != StatusPedido.REPROVADO) {

            throw new RuntimeException("Status de avaliação inválido");
        }

        pedido.setParecerFinanceiro(parecer);
        pedido.setStatus(status);

        return pedidoRepository.save(pedido);
    }
}
