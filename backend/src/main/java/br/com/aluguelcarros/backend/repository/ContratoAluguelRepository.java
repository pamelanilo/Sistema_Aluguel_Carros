package br.com.aluguelcarros.backend.repository;

import br.com.aluguelcarros.backend.model.ContratoAluguel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratoAluguelRepository extends JpaRepository<ContratoAluguel, Long> {
}