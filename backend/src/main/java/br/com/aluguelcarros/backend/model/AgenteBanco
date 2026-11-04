package br.com.aluguelcarros.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "agentes_banco")
public class AgenteBanco extends Agente {

    @Column(nullable = false, length = 10)
    private String codigoBanco;

    public String getCodigoBanco() {
        return codigoBanco;
    }

    public void setCodigoBanco(String codigoBanco) {
        this.codigoBanco = codigoBanco;
    }
}