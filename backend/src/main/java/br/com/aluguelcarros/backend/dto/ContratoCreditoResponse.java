package br.com.aluguelcarros.backend.dto;

import br.com.aluguelcarros.backend.model.ContratoCredito;
import java.math.BigDecimal;

public record ContratoCreditoResponse(
        Long id,
        BigDecimal valorConcedido,
        BigDecimal taxaJuros,
        int parcelas,
        Long contratoAluguelId,
        Long agenteBancoId
) {
    public static ContratoCreditoResponse from(ContratoCredito credito) {
        return new ContratoCreditoResponse(
                credito.getId(),
                credito.getValorConcedido(),
                credito.getTaxaJuros(),
                credito.getParcelas(),
                credito.getContratoAluguel().getId(),
                credito.getAgenteBanco().getId()
        );
    }
}