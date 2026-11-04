package br.com.aluguelcarros.backend.dto;

import br.com.aluguelcarros.backend.model.ContratoAluguel;
import java.time.LocalDate;

public record ContratoAluguelResponse(
        Long id,
        LocalDate dataAssinatura,
        String tipo,
        Long pedidoId,
        Long contratoCreditoId
) {
    public static ContratoAluguelResponse from(ContratoAluguel contrato) {
        return new ContratoAluguelResponse(
                contrato.getId(),
                contrato.getDataAssinatura(),
                contrato.getTipo().name(),
                contrato.getPedido().getId(),
                contrato.getContratoCredito() != null
                        ? contrato.getContratoCredito().getId()
                        : null
        );
    }
}