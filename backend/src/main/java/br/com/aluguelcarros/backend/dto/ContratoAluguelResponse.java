package br.com.aluguelcarros.backend.dto;

import br.com.aluguelcarros.backend.model.ContratoAluguel;
import java.time.LocalDate;

public record ContratoAluguelResponse(
        Long id,
        LocalDate dataAssinatura,
        String tipo,
        String status,
        Long pedidoId,
        String cliente,
        String automovel,
        String proprietario,
        Long contratoCreditoId
) {
    public static ContratoAluguelResponse from(ContratoAluguel c) {
        var pedido = c.getPedido();
        var auto = pedido.getAutomovel();
        return new ContratoAluguelResponse(
                c.getId(),
                c.getDataAssinatura(),
                c.getTipo() == null ? null : c.getTipo().name(),
                c.getStatus() == null ? "EXECUTADO" : c.getStatus().name(),
                pedido.getId(),
                pedido.getCliente() == null ? null : pedido.getCliente().getNome(),
                auto == null ? null : auto.getMarca() + " " + auto.getModelo() + " (" + auto.getPlaca() + ")",
                auto == null || auto.getProprietario() == null ? null : auto.getProprietario().getNome(),
                c.getContratoCredito() != null ? c.getContratoCredito().getId() : null
        );
    }
}
