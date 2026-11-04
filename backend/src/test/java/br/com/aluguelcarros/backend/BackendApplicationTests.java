package br.com.aluguelcarros.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Teste de integração: sobe o contexto completo e, por isso, precisa de um banco PostgreSQL.
 * Só é executado quando SUPABASE_DB_URL estiver definida; sem ela, é ignorado
 * (os demais testes unitários continuam rodando sem banco).
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "SUPABASE_DB_URL", matches = ".+")
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
