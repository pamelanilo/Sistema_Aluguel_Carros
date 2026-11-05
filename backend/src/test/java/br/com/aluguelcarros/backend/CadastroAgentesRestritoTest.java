package br.com.aluguelcarros.backend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

/** Com APP_CADASTRO_AGENTES_PUBLICO=false, empresas e bancos só são criados com o token de instalação. */
@SpringBootTest(properties = "app.cadastro-agentes-publico=false")
@AutoConfigureMockMvc
@ActiveProfiles("it")
class CadastroAgentesRestritoTest {

    @Autowired
    private MockMvc mvc;

    private static final String EMPRESA =
            "{\"nome\":\"Restrita\",\"login\":\"restrita\",\"senha\":\"senha123\",\"cnpj\":\"12121212000106\",\"razaoSocial\":\"Restrita LTDA\"}";

    @Test
    void cadastroDeAgenteExigeTokenQuandoRestrito() throws Exception {
        String cfg = mvc.perform(get("/agentes/cadastro-publico")).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(cfg).contains("false");

        mvc.perform(post("/agentes/empresas").contentType(MediaType.APPLICATION_JSON).content(EMPRESA))
                .andExpect(status().isForbidden());
        mvc.perform(post("/agentes/empresas").header("X-Setup-Token", "token-errado")
                .contentType(MediaType.APPLICATION_JSON).content(EMPRESA))
                .andExpect(status().isForbidden());
        mvc.perform(post("/agentes/empresas").header("X-Setup-Token", "token-de-teste")
                .contentType(MediaType.APPLICATION_JSON).content(EMPRESA))
                .andExpect(status().isCreated());
    }
}
