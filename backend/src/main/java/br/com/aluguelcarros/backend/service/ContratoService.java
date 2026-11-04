package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.dto.ContratoAluguelRequest;
import br.com.aluguelcarros.backend.dto.ContratoCreditoRequest;
import br.com.aluguelcarros.backend.dto.ContratoAluguelResponse;
import br.com.aluguelcarros.backend.dto.ContratoCreditoResponse;

import br.com.aluguelcarros.backend.model.ContratoAluguel;
import br.com.aluguelcarros.backend.model.ContratoCredito;
import br.com.aluguelcarros.backend.model.Pedido;
import br.com.aluguelcarros.backend.model.AgenteBanco;

import br.com.aluguelcarros.backend.model.enums.StatusPedido;

import br.com.aluguelcarros.backend.repository.ContratoAluguelRepository;
import br.com.aluguelcarros.backend.repository.ContratoCreditoRepository;
import br.com.aluguelcarros.backend.repository.PedidoRepository;
import br.com.aluguelcarros.backend.repository.AgenteBancoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ContratoService {

    private final ContratoAluguelRepository contratoAluguelRepository;
    private final ContratoCreditoRepository contratoCreditoRepository;
    private final PedidoRepository pedidoRepository;
    private final AgenteBancoRepository agenteBancoRepository;

    public ContratoService(
            ContratoAluguelRepository contratoAluguelRepository,
            ContratoCreditoRepository contratoCreditoRepository,
            PedidoRepository pedidoRepository,
            AgenteBancoRepository agenteBancoRepository) {

        this.contratoAluguelRepository = contratoAluguelRepository;
        this.contratoCreditoRepository = contratoCreditoRepository;
        this.pedidoRepository = pedidoRepository;
        this.agenteBancoRepository = agenteBancoRepository;
    }

    public ContratoAluguel criarContratoAluguel(
            ContratoAluguelRequest dados, String loginAgente) {

        if (dados.getTipo() == null) {
            throw new RuntimeException("Informe o tipo de contrato (propriedade do cliente, da empresa ou do banco).");
        }

        Pedido pedido = pedidoRepository.findById(dados.getPedidoId())
                .orElseThrow(() ->
                        new RuntimeException("Pedido não encontrado"));

        if (pedido.getStatus() != StatusPedido.APROVADO) {
            throw new RuntimeException(
                    "Somente pedidos aprovados podem gerar contratos");
        }

        if (pedido.getContratoAluguel() != null) {
            throw new RuntimeException(
                    "Este pedido já possui contrato de aluguel");
        }

        ContratoAluguel contrato = new ContratoAluguel();

        contrato.setPedido(pedido);
        contrato.setDataAssinatura(LocalDate.now());
        contrato.setTipo(dados.getTipo());
        contrato.setStatus(br.com.aluguelcarros.backend.model.enums.StatusContrato.EXECUTADO);

        // O proprietário do automóvel passa a ser definido pelo tipo do contrato.
        br.com.aluguelcarros.backend.model.Usuario novoProprietario;
        switch (dados.getTipo()) {
            case PROPRIEDADE_CLIENTE -> novoProprietario = pedido.getCliente();
            case PROPRIEDADE_BANCO -> novoProprietario = agenteBancoRepository.findByLogin(loginAgente)
                    .orElseThrow(() -> new RuntimeException(
                            "Contratos de propriedade do banco exigem um agente bancário autenticado."));
            default -> novoProprietario = pedido.getAgente();
        }
        if (novoProprietario == null) {
            throw new RuntimeException("Não foi possível determinar o novo proprietário. Avalie o pedido antes de gerar o contrato.");
        }
        pedido.getAutomovel().setProprietario(novoProprietario);

        return contratoAluguelRepository.save(contrato);
    }

    public ContratoCredito criarContratoCredito(
            ContratoCreditoRequest dados, String loginAgenteBanco) {

        ContratoAluguel contratoAluguel =
                contratoAluguelRepository.findById(
                        dados.getContratoAluguelId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Contrato de aluguel não encontrado"));

        if (contratoAluguel.getContratoCredito() != null) {
            throw new RuntimeException(
                    "Este contrato já possui crédito associado");
        }

        AgenteBanco banco = agenteBancoRepository.findByLogin(loginAgenteBanco)
                .orElseThrow(() -> new RuntimeException("Somente um agente bancário pode conceder crédito."));
        if (!banco.getId().equals(dados.getAgenteBancoId())) {
            throw new RuntimeException("O contrato de crédito deve ser concedido pelo banco autenticado.");
        }
        if (dados.getValorConcedido() == null || dados.getValorConcedido().signum() <= 0
                || dados.getTaxaJuros() == null || dados.getTaxaJuros().signum() < 0
                || dados.getParcelas() <= 0) {
            throw new RuntimeException("Informe valor, taxa de juros e quantidade de parcelas válidos.");
        }

        ContratoCredito credito = new ContratoCredito();

        credito.setContratoAluguel(contratoAluguel);
        credito.setAgenteBanco(banco);
        credito.setValorConcedido(dados.getValorConcedido());
        credito.setTaxaJuros(dados.getTaxaJuros());
        credito.setParcelas(dados.getParcelas());

        return contratoCreditoRepository.save(credito);
    }

    public List<ContratoAluguelResponse> listarContratosAluguel() {
        return contratoAluguelRepository.findAll()
                .stream()
                .map(ContratoAluguelResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ContratoAluguelResponse> listarContratosAluguelDoCliente(String loginCliente) {
        return contratoAluguelRepository.findAll()
                .stream()
                .filter(c -> pertenceAoCliente(c, loginCliente))
                .map(ContratoAluguelResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ContratoCreditoResponse> listarContratosCreditoDoCliente(String loginCliente) {
        return contratoCreditoRepository.findAll()
                .stream()
                .filter(cr -> pertenceAoCliente(cr.getContratoAluguel(), loginCliente))
                .map(ContratoCreditoResponse::from)
                .toList();
    }

    private boolean pertenceAoCliente(ContratoAluguel contrato, String loginCliente) {
        return contrato != null
                && contrato.getPedido() != null
                && contrato.getPedido().getCliente() != null
                && loginCliente != null
                && loginCliente.equals(contrato.getPedido().getCliente().getLogin());
    }

    public ContratoAluguelResponse buscarContratoAluguel(Long id) {
        ContratoAluguel contrato = contratoAluguelRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Contrato de aluguel não encontrado"));

        return ContratoAluguelResponse.from(contrato);
    }

    public List<ContratoCreditoResponse> listarContratosCredito() {
        return contratoCreditoRepository.findAll()
                .stream()
                .map(ContratoCreditoResponse::from)
                .toList();
    }

    public ContratoCreditoResponse buscarContratoCredito(Long id) {
        ContratoCredito credito = contratoCreditoRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Contrato de crédito não encontrado"));

        return ContratoCreditoResponse.from(credito);
    }
}