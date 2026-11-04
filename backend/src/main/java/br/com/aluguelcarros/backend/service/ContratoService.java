
package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.dto.ContratoAluguelRequest;
import br.com.aluguelcarros.backend.dto.ContratoCreditoRequest;

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

import java.time.LocalDate;

@Service
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

    // Criar contrato de aluguel
    public ContratoAluguel criarContratoAluguel(
            ContratoAluguelRequest dados) {

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

        return contratoAluguelRepository.save(contrato);
    }

    // Criar contrato de crédito
    public ContratoCredito criarContratoCredito(
            ContratoCreditoRequest dados) {

        ContratoAluguel contratoAluguel =
                contratoAluguelRepository.findById(
                        dados.getContratoAluguelId())
                .orElseThrow(() ->
                        new RuntimeException("Contrato de aluguel não encontrado"));

        if (contratoAluguel.getContratoCredito() != null) {
            throw new RuntimeException(
                    "Este contrato já possui crédito associado");
        }

        AgenteBanco banco = agenteBancoRepository.findById(
                dados.getAgenteBancoId())
                .orElseThrow(() ->
                        new RuntimeException("Agente bancário não encontrado"));

        ContratoCredito credito = new ContratoCredito();

        credito.setContratoAluguel(contratoAluguel);
        credito.setAgenteBanco(banco);
        credito.setValorConcedido(dados.getValorConcedido());
        credito.setTaxaJuros(dados.getTaxaJuros());
        credito.setParcelas(dados.getParcelas());

        return contratoCreditoRepository.save(credito);
    }
}
