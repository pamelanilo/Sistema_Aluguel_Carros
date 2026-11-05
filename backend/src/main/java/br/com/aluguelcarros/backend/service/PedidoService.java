package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.exception.RecursoNaoEncontradoException;
import br.com.aluguelcarros.backend.exception.RegraNegocioException;
import br.com.aluguelcarros.backend.exception.AcessoNegadoException;

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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente autenticado não encontrado"));

        if (dados.getDataInicio() == null || dados.getDataFim() == null
                || !dados.getDataInicio().isAfter(LocalDate.now().minusDays(1))
                || !dados.getDataFim().isAfter(dados.getDataInicio())) {
            throw new RegraNegocioException("Informe datas válidas: retirada a partir de hoje e devolução posterior.");
        }

        validarValor(dados);
        Automovel automovel = automovelRepository.findById(dados.getAutomovelId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Automóvel não encontrado"));
        validarDisponibilidade(automovel.getId(), dados.getDataInicio(), dados.getDataFim(), null);


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
            throw new AcessoNegadoException("Você não tem permissão para consultar este pedido.");
        }
        return new PedidoResponse(pedido);
    }

    // Cliente pode alterar o próprio pedido enquanto pendente; agente pode atualizar durante a análise.
    public PedidoResponse atualizar(Long id, PedidoRequest dados, String loginAutenticado, boolean agente) {
        Pedido pedido = buscarEntidadePorId(id);

        if (!agente && (pedido.getCliente() == null
                || !pedido.getCliente().getLogin().equals(loginAutenticado))) {
            throw new AcessoNegadoException("Você não tem permissão para alterar este pedido.");
        }
        if (pedido.getStatus() != StatusPedido.PENDENTE
                && pedido.getStatus() != StatusPedido.EM_ANALISE) {
            throw new RegraNegocioException("Somente pedidos pendentes ou em análise podem ser alterados.");
        }
        if (dados.getDataInicio() == null || dados.getDataFim() == null
                || dados.getDataInicio().isBefore(LocalDate.now())
                || !dados.getDataFim().isAfter(dados.getDataInicio())) {
            throw new RegraNegocioException("Informe datas válidas: retirada a partir de hoje e devolução posterior.");
        }

        validarValor(dados);
        Automovel automovel = automovelRepository.findById(dados.getAutomovelId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Automóvel não encontrado"));
        validarDisponibilidade(automovel.getId(), dados.getDataInicio(), dados.getDataFim(), pedido.getId());


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
            throw new AcessoNegadoException("Você não tem permissão para cancelar este pedido.");
        }
        if (pedido.getStatus() != StatusPedido.PENDENTE && pedido.getStatus() != StatusPedido.EM_ANALISE) {
            throw new RegraNegocioException("Somente pedidos pendentes ou em análise podem ser cancelados.");
        }
        pedido.setStatus(StatusPedido.CANCELADO);

        return new PedidoResponse(pedidoRepository.save(pedido));
    }

    // Iniciar a análise financeira (PENDENTE -> EM_ANALISE)
    public PedidoResponse iniciarAnalise(Long id, String loginAgente) {
        Pedido pedido = buscarEntidadePorId(id);
        if (pedido.getStatus() != StatusPedido.PENDENTE) {
            throw new RegraNegocioException("Somente pedidos pendentes podem entrar em análise.");
        }
        var agente = agenteRepository.findByLogin(loginAgente)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agente autenticado não encontrado"));
        pedido.setAgente(agente);
        pedido.setStatus(StatusPedido.EM_ANALISE);
        return new PedidoResponse(pedidoRepository.save(pedido));
    }

    // Avaliar um pedido
    public PedidoResponse avaliar(Long id, String parecer, StatusPedido status, String loginAgente) {

        Pedido pedido = buscarEntidadePorId(id);

        if (pedido.getStatus() != StatusPedido.EM_ANALISE) {
            throw new RegraNegocioException("Inicie a análise financeira antes de aprovar ou recusar o pedido.");
        }
        if (status != StatusPedido.APROVADO
                && status != StatusPedido.REPROVADO) {

            throw new RegraNegocioException("Status de avaliação inválido");
        }

        var agente = agenteRepository.findByLogin(loginAgente)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Agente autenticado não encontrado"));
        if (status == StatusPedido.APROVADO) {
            validarDisponibilidade(pedido.getAutomovel().getId(),
                    pedido.getDataInicio(), pedido.getDataFim(), pedido.getId());
        }
        pedido.setAgente(agente);
        pedido.setParecerFinanceiro(parecer);
        pedido.setStatus(status);

        return new PedidoResponse(pedidoRepository.save(pedido));
    }

    private void validarValor(PedidoRequest dados) {
        if (dados.getAutomovelId() == null) {
            throw new RegraNegocioException("Selecione o automóvel do pedido.");
        }
        if (dados.getValorTotal() != null && dados.getValorTotal().signum() < 0) {
            throw new RegraNegocioException("O valor do pedido não pode ser negativo.");
        }
    }

    // O automóvel não pode ter outro pedido aprovado no mesmo período.
    private void validarDisponibilidade(Long automovelId, LocalDate inicio, LocalDate fim, Long ignorarPedidoId) {
        boolean ocupado = pedidoRepository
                .findByAutomovel_IdAndStatusInAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
                        automovelId, List.of(StatusPedido.APROVADO), fim, inicio)
                .stream()
                .anyMatch(p -> !p.getId().equals(ignorarPedidoId));
        if (ocupado) {
            throw new RegraNegocioException(
                    "O automóvel já está reservado por outro pedido aprovado neste período.");
        }
    }

    // Buscar a entidade para operações internas
    private Pedido buscarEntidadePorId(Long id) {

        return pedidoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Pedido não encontrado"));
    }
}