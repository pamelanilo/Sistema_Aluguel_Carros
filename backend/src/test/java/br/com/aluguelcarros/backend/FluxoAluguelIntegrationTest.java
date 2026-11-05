package br.com.aluguelcarros.backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Teste de integração (controller → service → repository → banco H2 em memória)
 * cobrindo o fluxo descrito no enunciado: cadastro, login, pedido, análise financeira,
 * contrato de aluguel (com troca de proprietário) e contrato de crédito.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
class FluxoAluguelIntegrationTest {

    private static final String TOKEN = "token-de-teste";

    @Autowired
    private MockMvc mvc;

    // ------------------------------------------------------------------ helpers

    private MvcResult enviar(MockHttpServletRequestBuilder req, MockHttpSession sessao, String json, int status)
            throws Exception {
        if (sessao != null) req.session(sessao);
        if (json != null) req.contentType(MediaType.APPLICATION_JSON).content(json);
        return mvc.perform(req).andExpect(status().is(status)).andReturn();
    }

    private static String corpo(MvcResult r) throws Exception {
        return r.getResponse().getContentAsString(java.nio.charset.StandardCharsets.UTF_8);
    }

    private static Long id(MvcResult r) throws Exception {
        return ((Number) JsonPath.read(corpo(r), "$.id")).longValue();
    }

    private MockHttpSession login(String login, String senha) throws Exception {
        MvcResult r = enviar(post("/auth/login"), null,
                "{\"login\":\"" + login + "\",\"senha\":\"" + senha + "\"}", 200);
        return (MockHttpSession) r.getRequest().getSession(false);
    }

    private Long criarAgente(String tipo, String login, String extra) throws Exception {
        String json = "{\"nome\":\"" + login + "\",\"login\":\"" + login + "\",\"senha\":\"senha123\"," + extra + "}";
        MvcResult r = mvc.perform(post("/agentes/" + tipo).header("X-Setup-Token", TOKEN)
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated()).andReturn();
        return id(r);
    }

    private static String cliente(String login, String cpf, int rendimentos) {
        StringBuilder rend = new StringBuilder();
        for (int i = 0; i < rendimentos; i++) {
            if (i > 0) rend.append(',');
            rend.append("{\"entidadeEmpregadora\":\"Empresa ").append(i).append("\",\"valor\":2500.00}");
        }
        return "{\"nome\":\"Cliente " + login + "\",\"login\":\"" + login + "\",\"senha\":\"senha123\","
                + "\"rg\":\"MG123\",\"cpf\":\"" + cpf + "\",\"endereco\":\"Rua A, 1\",\"profissao\":\"Analista\","
                + "\"rendimentos\":[" + rend + "]}";
    }

    private static String pedido(Long carro, int inicio, int fim) {
        return "{\"automovelId\":" + carro + ",\"dataInicio\":\"" + LocalDate.now().plusDays(inicio)
                + "\",\"dataFim\":\"" + LocalDate.now().plusDays(fim) + "\",\"valorTotal\":900.00}";
    }

    // -------------------------------------------------------------------- testes

    @Test
    void fluxoCompletoDoPedidoAoContratoDeCredito() throws Exception {
        // Acesso exige autenticação
        enviar(get("/auth/me"), null, null, 401);
        enviar(get("/pedidos"), null, null, 401);

        // Autocadastro de empresa e banco pela tela (perfil "it": cadastro público ligado)
        assertThat(corpo(enviar(get("/agentes/cadastro-publico"), null, null, 200))).contains("true");
        enviar(post("/agentes/empresas"), null,
                "{\"nome\":\"X\",\"login\":\"x1\",\"senha\":\"senha123\",\"cnpj\":\"11111111000111\",\"razaoSocial\":\"X\"}", 400); // CNPJ inválido
        enviar(post("/agentes/bancos"), null,
                "{\"nome\":\"X\",\"login\":\"x2\",\"senha\":\"senha123\",\"cnpj\":\"77888999000181\",\"codigoBanco\":\"1\"}", 400); // código do banco
        enviar(post("/agentes/bancos"), null,
                "{\"nome\":\"X\",\"login\":\"x3\",\"senha\":\"123\",\"cnpj\":\"77888999000181\",\"codigoBanco\":\"001\"}", 400); // senha curta
        Long empresaId = criarAgente("empresas", "empresa1", "\"cnpj\":\"00000000000191\",\"razaoSocial\":\"VRUM\"");
        Long bancoId = criarAgente("bancos", "banco1", "\"cnpj\":\"00000000000272\",\"codigoBanco\":\"001\"");
        enviar(post("/agentes/bancos"), null,
                "{\"nome\":\"X\",\"login\":\"outro\",\"senha\":\"senha123\",\"cnpj\":\"00000000000272\",\"codigoBanco\":\"001\"}", 400); // CNPJ repetido
        enviar(post("/agentes/empresas"), null,
                "{\"nome\":\"X\",\"login\":\"banco1\",\"senha\":\"senha123\",\"cnpj\":\"77888999000181\",\"razaoSocial\":\"X\"}", 400); // login repetido
        enviar(post("/clientes"), null, cliente("empresa1", "12345678909", 1), 400); // login de agente não pode ser reutilizado por cliente

        // Cadastro de cliente: máximo 3 rendimentos, CPF válido, senha nunca retornada
        enviar(post("/clientes"), null, cliente("ana", "52998224725", 4), 400);
        enviar(post("/clientes"), null, cliente("ana", "12345678900", 1), 400);
        MvcResult cad = enviar(post("/clientes"), null, cliente("ana", "529.982.247-25", 2), 201);
        assertThat(corpo(cad)).doesNotContain("senha").contains("52998224725");
        Long anaId = id(cad);
        enviar(post("/clientes"), null, cliente("ana2", "52998224725", 1), 400); // CPF repetido
        enviar(post("/clientes"), null, cliente("bruno", "11144477735", 1), 201);

        // Login
        enviar(post("/auth/login"), null, "{\"login\":\"ana\",\"senha\":\"errada\"}", 401);
        MockHttpSession ana = login("ana", "senha123");
        MockHttpSession bruno = login("bruno", "senha123");
        MockHttpSession empresa = login("empresa1", "senha123");
        MockHttpSession banco = login("banco1", "senha123");
        assertThat(corpo(enviar(get("/auth/me"), ana, null, 200))).contains("\"tipo\":\"Cliente\"");

        // Cliente consulta os próprios dados, mas não a lista de clientes
        assertThat(corpo(enviar(get("/clientes/me"), ana, null, 200))).contains("Analista");
        enviar(get("/clientes"), ana, null, 403);

        // Agente registra automóvel (proprietário inicial: empresa)
        enviar(post("/automoveis"), ana, "{}", 403);
        Long carro = id(enviar(post("/automoveis"), empresa,
                "{\"matricula\":\"MAT-1\",\"ano\":2024,\"marca\":\"Fiat\",\"modelo\":\"Argo\",\"placa\":\"ABC-1D23\",\"proprietarioId\":" + empresaId + "}", 200));

        // Cliente introduz, modifica e consulta pedido
        enviar(post("/pedidos"), empresa, pedido(carro, 1, 5), 403); // agente não cria pedido
        enviar(post("/pedidos"), ana, pedido(carro, 5, 1), 400);     // datas inválidas
        MvcResult criado = enviar(post("/pedidos"), ana, pedido(carro, 1, 5), 201);
        assertThat(corpo(criado)).contains("\"status\":\"PENDENTE\"");
        Long pedidoAna = id(criado);
        enviar(put("/pedidos/" + pedidoAna), ana, pedido(carro, 2, 6), 200);
        enviar(get("/pedidos/" + pedidoAna), bruno, null, 403);       // pedido de outro cliente
        assertThat((Integer) JsonPath.read(corpo(enviar(get("/pedidos"), bruno, null, 200)), "$.length()")).isZero();

        // Bruno pede o mesmo carro em período sobreposto
        Long pedidoBruno = id(enviar(post("/pedidos"), bruno, pedido(carro, 3, 4), 201));

        // Análise financeira pelo agente
        enviar(patch("/pedidos/" + pedidoAna + "/avaliar"), empresa, "{\"status\":\"APROVADO\",\"parecer\":\"ok\"}", 400); // ainda pendente
        enviar(patch("/pedidos/" + pedidoAna + "/analisar"), ana, null, 403);
        MvcResult analise = enviar(patch("/pedidos/" + pedidoAna + "/analisar"), empresa, null, 200);
        assertThat(corpo(analise)).contains("\"status\":\"EM_ANALISE\"").contains("\"clienteRendaTotal\":5000");
        enviar(patch("/pedidos/" + pedidoAna + "/avaliar"), empresa, "{\"status\":\"REPROVADO\",\"parecer\":\" \"}", 400);
        assertThat(corpo(enviar(patch("/pedidos/" + pedidoAna + "/avaliar"), empresa,
                "{\"status\":\"APROVADO\",\"parecer\":\"Renda compatível\"}", 200))).contains("\"status\":\"APROVADO\"");

        // Pedido aprovado não pode mais ser cancelado; o carro fica reservado no período
        enviar(patch("/pedidos/" + pedidoAna + "/cancelar"), ana, null, 400);
        enviar(patch("/pedidos/" + pedidoBruno + "/analisar"), empresa, null, 200);
        enviar(patch("/pedidos/" + pedidoBruno + "/avaliar"), empresa, "{\"status\":\"APROVADO\",\"parecer\":\"ok\"}", 400);
        assertThat(corpo(enviar(patch("/pedidos/" + pedidoBruno + "/avaliar"), empresa,
                "{\"status\":\"REPROVADO\",\"parecer\":\"Veículo indisponível\"}", 200))).contains("REPROVADO");

        // Contrato de aluguel: só agente; propriedade do banco exige agente bancário
        enviar(post("/contratos/aluguel"), ana, "{\"pedidoId\":" + pedidoAna + ",\"tipo\":\"PROPRIEDADE_CLIENTE\"}", 403);
        enviar(post("/contratos/aluguel"), empresa, "{\"pedidoId\":" + pedidoBruno + ",\"tipo\":\"PROPRIEDADE_CLIENTE\"}", 400);
        enviar(post("/contratos/aluguel"), empresa, "{\"pedidoId\":" + pedidoAna + ",\"tipo\":\"PROPRIEDADE_BANCO\"}", 400);
        Long contrato = id(enviar(post("/contratos/aluguel"), empresa,
                "{\"pedidoId\":" + pedidoAna + ",\"tipo\":\"PROPRIEDADE_CLIENTE\"}", 200));
        enviar(post("/contratos/aluguel"), empresa, "{\"pedidoId\":" + pedidoAna + ",\"tipo\":\"PROPRIEDADE_CLIENTE\"}", 400); // duplicado

        // O automóvel passa a ser propriedade do cliente
        Object dono = JsonPath.read(corpo(enviar(get("/automoveis/" + carro), ana, null, 200)), "$.proprietarioId");
        assertThat(((Number) dono).longValue()).isEqualTo(anaId);

        // Contrato de crédito: somente o banco autenticado
        String credito = "{\"contratoAluguelId\":" + contrato + ",\"agenteBancoId\":" + bancoId
                + ",\"valorConcedido\":800.00,\"taxaJuros\":1.5,\"parcelas\":10}";
        enviar(post("/contratos/credito"), empresa, credito, 400);
        enviar(post("/contratos/credito"), banco, credito, 200);
        enviar(post("/contratos/credito"), banco, credito, 400); // já possui crédito

        // Cliente vê apenas os próprios contratos
        String contratosAna = corpo(enviar(get("/contratos/aluguel"), ana, null, 200));
        assertThat((Integer) JsonPath.read(contratosAna, "$.length()")).isEqualTo(1);
        assertThat(contratosAna).contains("EXECUTADO");
        assertThat((Integer) JsonPath.read(corpo(enviar(get("/contratos/aluguel"), bruno, null, 200)), "$.length()")).isZero();
        assertThat((Integer) JsonPath.read(corpo(enviar(get("/contratos/credito"), ana, null, 200)), "$.length()")).isEqualTo(1);

        // Registros em uso não podem ser excluídos
        enviar(delete("/automoveis/" + carro), empresa, null, 400);
        enviar(delete("/clientes/" + anaId), empresa, null, 400);

        // Logout encerra a sessão
        enviar(post("/auth/logout"), ana, null, 200);
        enviar(get("/auth/me"), ana, null, 401);
    }

    @Test
    void crudDeClientePeloAgenteEAlteracaoDosPropriosDados() throws Exception {
        criarAgente("empresas", "empresa-crud", "\"cnpj\":\"44555666000181\",\"razaoSocial\":\"CRUD\"");
        MockHttpSession agente = login("empresa-crud", "senha123");

        // Create
        Long id = id(enviar(post("/clientes"), agente, cliente("carla", "12345678909", 1), 201));
        // Read
        assertThat(corpo(enviar(get("/clientes/" + id), agente, null, 200))).contains("carla");
        assertThat(corpo(enviar(get("/clientes"), agente, null, 200))).contains("carla");
        // Update (com 3 rendimentos; 4 é rejeitado)
        enviar(put("/clientes/" + id), agente, cliente("carla", "12345678909", 4), 400);
        String atualizado = corpo(enviar(put("/clientes/" + id), agente,
                cliente("carla", "12345678909", 3).replace("Rua A, 1", "Rua B, 2"), 200));
        assertThat(atualizado).contains("Rua B, 2");
        assertThat((Integer) JsonPath.read(atualizado, "$.rendimentos.length()")).isEqualTo(3);

        // O próprio cliente altera seus dados (sem trocar a senha)
        MockHttpSession carla = login("carla", "senha123");
        String json = cliente("carla", "12345678909", 1).replace("\"senha\":\"senha123\",", "").replace("Analista", "Engenheira");
        assertThat(corpo(enviar(put("/clientes/me"), carla, json, 200))).contains("Engenheira");
        login("carla", "senha123"); // senha mantida

        // Delete
        enviar(delete("/clientes/" + id), agente, null, 204);
        enviar(get("/clientes/" + id), agente, null, 404);
    }
}
