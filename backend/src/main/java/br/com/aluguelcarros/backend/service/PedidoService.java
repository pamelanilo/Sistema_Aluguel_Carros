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
import br.com.aluguelcarros.backend.repository.AgenteRepository;

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
    private final AgenteRepository agenteRepository;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            AutomovelRepository automovelRepository,
            AgenteRepository agenteRepository) {

        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.automovelRepository = automovelRepository;
        this.agenteRepository = agenteRepository;
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

    // Cliente pode alterar o próprio pedido enquanto pendente; agente pode atualizar durante a análise.
    public PedidoResponse atualizar(Long id, PedidoRequest dados, String loginAutenticado, boolean agente) {
        Pedido pedido = buscarEntidadePorId(id);

        if (!agente && (pedido.getCliente() == null
                || !pedido.getCliente().getLogin().equals(loginAutenticado))) {
            throw new RuntimeException("Você não tem permissão para alterar este pedido.");
        }
        if (pedido.getStatus() != StatusPedido.PENDENTE
                && pedido.getStatus() != StatusPedido.EM_ANALISE) {
            throw new RuntimeException("Somente pedidos pendentes ou em análise podem ser alterados.");
        }
        if (dados.getDataInicio() == null || dados.getDataFim() == null
                || dados.getDataInicio().isBefore(LocalDate.now())
                || !dados.getDataFim().isAfter(dados.getDataInicio())) {
            throw new RuntimeException("Informe datas válidas: retirada a partir de hoje e devolução posterior.");
        }

        Automovel automovel = automovelRepository.findById(dados.getAutomovelId())
                .orElseThrow(() -> new RuntimeException("Automóvel não encontrado"));

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

    // Iniciar a análise financeira (PENDENTE -> EM_ANALISE)
    public PedidoResponse iniciarAnalise(Long id, String loginAgente) {
        Pedido pedido = buscarEntidadePorId(id);
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new RuntimeException("Somente pedidos pendentes podem entrar em análise.");
        }
        var agente = agenteRepository.findByLogin(loginAgente)
                .orElseThrow(() -> new RuntimeException("Agente autenticado não encontrado"));
        pedido.setAgente(agente);
        pedido.setStatus(StatusPedido.EM_ANALISE);
        return new PedidoResponse(pedidoRepository.save(pedido));
    }

    // Avaliar um pedido
    public PedidoResponse avaliar(Long id, String parecer, StatusPedido status, String loginAgente) {

        Pedido pedido = buscarEntidadePorId(id);

        if (pedido.getStatus() != StatusPedido.EM_ANALISE) {
            throw new RuntimeException("Inicie a análise financeira antes de aprovar ou recusar o pedido.");
        }
        if (status != StatusPedido.APROVADO
                && status != StatusPedido.REPROVADO) {

            throw new RuntimeException("Status de avaliação inválido");
        }

        var agente = agenteRepository.findByLogin(loginAgente)
                .orElseThrow(() -> new RuntimeException("Agente autenticado não encontrado"));
        pedido.setAgente(agente);
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