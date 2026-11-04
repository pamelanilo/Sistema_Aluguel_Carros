package br.com.aluguelcarros.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "agentes_empresa")
public class AgenteEmpresa extends Agente {

    @Column(nullable = false)
    private String razaoSocial;

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }
}