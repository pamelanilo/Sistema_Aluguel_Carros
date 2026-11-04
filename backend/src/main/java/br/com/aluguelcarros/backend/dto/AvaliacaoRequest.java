package br.com.aluguelcarros.backend.dto;

import br.com.aluguelcarros.backend.model.enums.StatusPedido;

public class AvaliacaoRequest {

    private String parecer;
    private StatusPedido status;

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }
}