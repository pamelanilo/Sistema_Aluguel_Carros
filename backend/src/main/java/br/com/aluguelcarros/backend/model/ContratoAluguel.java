package br.com.aluguelcarros.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import br.com.aluguelcarros.backend.model.enums.TipoContrato;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "contratos_aluguel")
public class ContratoAluguel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataAssinatura;

    @Enumerated(EnumType.STRING)
    private TipoContrato tipo;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;

    @OneToOne(mappedBy = "contratoAluguel", cascade = CascadeType.ALL)
    private ContratoCredito contratoCredito;

    public Long getId() {
        return id;
    }

    public LocalDate getDataAssinatura() {
        return dataAssinatura;
    }

    public void setDataAssinatura(LocalDate dataAssinatura) {
        this.dataAssinatura = dataAssinatura;
    }

    public TipoContrato getTipo() {
        return tipo;
    }

    public void setTipo(TipoContrato tipo) {
        this.tipo = tipo;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public ContratoCredito getContratoCredito() {
        return contratoCredito;
    }

    public void setContratoCredito(ContratoCredito contratoCredito) {
        this.contratoCredito = contratoCredito;
    }
}