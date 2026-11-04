package br.com.aluguelcarros.backend.dto;

import br.com.aluguelcarros.backend.model.Pedido;
import br.com.aluguelcarros.backend.model.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PedidoResponse {

    private Long id;
    private LocalDate dataCriacao;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private BigDecimal valorTotal;
    private StatusPedido status;
    private String parecerFinanceiro;

    private Long clienteId;
    private String clienteNome;

    private Long automovelId;
    private String marca;
    private String modelo;
    private String placa;

    private Long contratoAluguelId;

    public PedidoResponse(Pedido pedido) {
        this.id = pedido.getId();
        this.dataCriacao = pedido.getDataCriacao();
        this.dataInicio = pedido.getDataInicio();
        this.dataFim = pedido.getDataFim();
        this.valorTotal = pedido.getValorTotal();
        this.status = pedido.getStatus();
        this.parecerFinanceiro = pedido.getParecerFinanceiro();

        if (pedido.getCliente() != null) {
            this.clienteId = pedido.getCliente().getId();
            this.clienteNome = pedido.getCliente().getNome();
        }

        if (pedido.getAutomovel() != null) {
            this.automovelId = pedido.getAutomovel().getId();
            this.marca = pedido.getAutomovel().getMarca();
            this.modelo = pedido.getAutomovel().getModelo();
            this.placa = pedido.getAutomovel().getPlaca();
        }

        if (pedido.getContratoAluguel() != null) {
            this.contratoAluguelId = pedido.getContratoAluguel().getId();
        }
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public String getParecerFinanceiro() {
        return parecerFinanceiro;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public String getClienteNome() {
        return clienteNome;
    }

    public Long getAutomovelId() {
        return automovelId;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public String getPlaca() {
        return placa;
    }

    public Long getContratoAluguelId() {
        return contratoAluguelId;
    }
}