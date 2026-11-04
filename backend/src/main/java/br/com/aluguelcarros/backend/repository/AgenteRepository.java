package br.com.aluguelcarros.backend.repository;

import br.com.aluguelcarros.backend.model.Agente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AgenteRepository extends JpaRepository<Agente, Long> {
    Optional<Agente> findByLogin(String login);
}
