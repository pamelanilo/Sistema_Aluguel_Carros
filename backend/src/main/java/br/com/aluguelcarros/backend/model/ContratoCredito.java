package br.com.aluguelcarros.backend.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "contratos_credito")
public class ContratoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 12, scale = 2)
    private BigDecimal valorConcedido;

    @Column(precision = 12, scale = 2)
    private BigDecimal taxaJuros;

    private int parcelas;

    @OneToOne
    @JoinColumn(name = "contrato_aluguel_id", unique = true)
    private ContratoAluguel contratoAluguel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agente_banco_id")
    private AgenteBanco agenteBanco;

    public Long getId() {
        return id;
    }

    public BigDecimal getValorConcedido() {
        return valorConcedido;
    }

    public void setValorConcedido(BigDecimal valorConcedido) {
        this.valorConcedido = valorConcedido;
    }

    public BigDecimal getTaxaJuros() {
        return taxaJuros;
    }

    public void setTaxaJuros(BigDecimal taxaJuros) {
        this.taxaJuros = taxaJuros;
    }

    public int getParcelas() {
        return parcelas;
    }

    public void setParcelas(int parcelas) {
        this.parcelas = parcelas;
    }

    public ContratoAluguel getContratoAluguel() {
        return contratoAluguel;
    }

    public void setContratoAluguel(ContratoAluguel contratoAluguel) {
        this.contratoAluguel = contratoAluguel;
    }

    public AgenteBanco getAgenteBanco() {
        return agenteBanco;
    }

    public void setAgenteBanco(AgenteBanco agenteBanco) {
        this.agenteBanco = agenteBanco;
    }
}