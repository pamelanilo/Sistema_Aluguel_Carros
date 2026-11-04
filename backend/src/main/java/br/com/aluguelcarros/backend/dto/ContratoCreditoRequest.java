
package br.com.aluguelcarros.backend.dto;

import java.math.BigDecimal;

public class ContratoCreditoRequest {

    private Long contratoAluguelId;
    private Long agenteBancoId;
    private BigDecimal valorConcedido;
    private BigDecimal taxaJuros;
    private int parcelas;

    public Long getContratoAluguelId() {
        return contratoAluguelId;
    }

    public void setContratoAluguelId(Long contratoAluguelId) {
        this.contratoAluguelId = contratoAluguelId;
    }

    public Long getAgenteBancoId() {
        return agenteBancoId;
    }

    public void setAgenteBancoId(Long agenteBancoId) {
        this.agenteBancoId = agenteBancoId;
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
}
