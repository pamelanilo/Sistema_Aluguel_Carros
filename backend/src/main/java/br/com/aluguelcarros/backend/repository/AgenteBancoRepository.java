
package br.com.aluguelcarros.backend.repository;

import br.com.aluguelcarros.backend.model.AgenteBanco;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgenteBancoRepository
        extends JpaRepository<AgenteBanco, Long> {
    java.util.Optional<AgenteBanco> findByLogin(String login);
}
