package br.com.aluguelcarros.backend.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

/** Regra de negócio do enunciado: um contratante possui no máximo três rendimentos. */
class ClienteTest {

    private Rendimento rendimento(String empregador, String valor) {
        Rendimento r = new Rendimento();
        r.setEntidadeEmpregadora(empregador);
        r.setValor(new BigDecimal(valor));
        return r;
    }

    @Test
    void aceitaAteTresRendimentosEVinculaOCliente() {
        Cliente cliente = new Cliente();
        for (int i = 1; i <= 3; i++) {
            cliente.adicionarRendimento(rendimento("Empresa " + i, "1000.00"));
        }

        assertEquals(3, cliente.getRendimentos().size());
        cliente.getRendimentos().forEach(r -> assertSame(cliente, r.getCliente()));
    }

    @Test
    void rejeitaOQuartoRendimento() {
        Cliente cliente = new Cliente();
        for (int i = 1; i <= 3; i++) {
            cliente.adicionarRendimento(rendimento("Empresa " + i, "1000.00"));
        }

        assertThrows(IllegalArgumentException.class,
                () -> cliente.adicionarRendimento(rendimento("Empresa 4", "500.00")));
        assertEquals(3, cliente.getRendimentos().size());
    }

    @Test
    void rejeitaRendimentoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Cliente().adicionarRendimento(null));
    }
}
