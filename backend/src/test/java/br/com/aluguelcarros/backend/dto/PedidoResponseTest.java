package br.com.aluguelcarros.backend.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.com.aluguelcarros.backend.model.Automovel;
import br.com.aluguelcarros.backend.model.Cliente;
import br.com.aluguelcarros.backend.model.Pedido;
import br.com.aluguelcarros.backend.model.enums.StatusPedido;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

/** O cliente acompanha o estado do pedido pela resposta da API. */
class PedidoResponseTest {

    @Test
    void expoEstadoParecerEDadosDoAutomovel() {
        Cliente cliente = new Cliente();
        cliente.setNome("Maria Souza");

        Automovel carro = new Automovel();
        carro.setMarca("Fiat");
        carro.setModelo("Argo");
        carro.setPlaca("ABC1D23");

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setAutomovel(carro);
        pedido.setDataInicio(LocalDate.of(2026, 12, 1));
        pedido.setDataFim(LocalDate.of(2026, 12, 10));
        pedido.setValorTotal(new BigDecimal("1500.00"));
        pedido.setStatus(StatusPedido.REPROVADO);
        pedido.setParecerFinanceiro("Renda insuficiente");

        PedidoResponse resposta = new PedidoResponse(pedido);

        assertEquals(StatusPedido.REPROVADO, resposta.getStatus());
        assertEquals("Renda insuficiente", resposta.getParecerFinanceiro());
        assertEquals("Maria Souza", resposta.getClienteNome());
        assertEquals("Fiat", resposta.getMarca());
        assertEquals("Argo", resposta.getModelo());
        assertEquals("ABC1D23", resposta.getPlaca());
        assertNull(resposta.getContratoAluguelId());
    }
}
