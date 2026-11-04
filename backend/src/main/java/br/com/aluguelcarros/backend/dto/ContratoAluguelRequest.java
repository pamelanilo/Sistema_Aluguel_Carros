package br.com.aluguelcarros.backend.dto;

import br.com.aluguelcarros.backend.model.enums.TipoContrato;

public class ContratoAluguelRequest {

    private Long pedidoId;
    private TipoContrato tipo;

    public Long getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }

    public TipoContrato getTipo() {
        return tipo;
    }

    public void setTipo(TipoContrato tipo) {
        this.tipo = tipo;
    }
}