package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.dto.PedidoRequest;
import br.com.aluguelcarros.backend.dto.PedidoResponse;
import br.com.aluguelcarros.backend.model.Pedido;
import br.com.aluguelcarros.backend.model.Cliente;
import br.com.aluguelcarros.backend.model.Automovel;
import br.com.aluguelcarros.backend.model.enums.StatusPedido;
import br.com.aluguelcarros.backend.repository.PedidoRepository;
import br.com.aluguelcarros.backend.repository.ClienteRepository;
import br.com.aluguelcarros.backend.repository.AutomovelRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
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
    public PedidoResponse criar(PedidoRequest dados, String loginAutenticado) {

        Cliente cliente = clienteRepository.findByLogin(loginAutenticado)
                .orElseThrow(() -> new RuntimeException("Cliente autenticado não encontrado"));

        if (dados.getDataInicio() == null || dados.getDataFim() == null
                || !dados.getDataInicio().isAfter(LocalDate.now().minusDays(1))
                || !dados.getDataFim().isAfter(dados.getDataInicio())) {
            throw new RuntimeException("Informe datas válidas: retirada a partir de hoje e devolução posterior.");
        }

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

        return new PedidoResponse(pedidoRepository.save(pedido));
    }

    // Listar todos os pedidos
    public List<PedidoResponse> listarTodos() {
        return pedidoRepository.findAll().stream().map(PedidoResponse::new).toList();
    }

    public List<PedidoResponse> listarDoCliente(String login) {
        return pedidoRepository.findByCliente_Login(login).stream()
                .map(PedidoResponse::new).toList();
    }

    // Buscar um pedido pelo ID
    public PedidoResponse buscarPorId(Long id, String loginAutenticado, boolean agente) {
        Pedido pedido = buscarEntidadePorId(id);
        if (!agente && (pedido.getCliente() == null
                || !pedido.getCliente().getLogin().equals(loginAutenticado))) {
            throw new RuntimeException("Você não tem permissão para consultar este pedido.");
        }
        return new PedidoResponse(pedido);
    }

    // Atualizar um pedido
    public PedidoResponse atualizar(Long id, PedidoRequest dados) {

        Pedido pedido = buscarEntidadePorId(id);

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

        return new PedidoResponse(pedidoRepository.save(pedido));
    }

    // Cancelar um pedido
    public PedidoResponse cancelar(Long id, String loginAutenticado) {

        Pedido pedido = buscarEntidadePorId(id);
        if (pedido.getCliente() == null || !pedido.getCliente().getLogin().equals(loginAutenticado)) {
            throw new RuntimeException("Você não tem permissão para cancelar este pedido.");
        }
        if (pedido.getStatus() != StatusPedido.PENDENTE && pedido.getStatus() != StatusPedido.EM_ANALISE) {
            throw new RuntimeException("Somente pedidos pendentes ou em análise podem ser cancelados.");
        }
        pedido.setStatus(StatusPedido.CANCELADO);

        return new PedidoResponse(pedidoRepository.save(pedido));
    }

    // Avaliar um pedido
    public PedidoResponse avaliar(Long id, String parecer, StatusPedido status) {

        Pedido pedido = buscarEntidadePorId(id);

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RuntimeException("Não é possível avaliar um pedido cancelado.");
        }
        if (status != StatusPedido.APROVADO
                && status != StatusPedido.REPROVADO) {

            throw new RuntimeException("Status de avaliação inválido");
        }

        pedido.setParecerFinanceiro(parecer);
        pedido.setStatus(status);

        return new PedidoResponse(pedidoRepository.save(pedido));
    }

    // Buscar a entidade para operações internas
    private Pedido buscarEntidadePorId(Long id) {

        return pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado"));
    }
}