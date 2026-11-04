package br.com.aluguelcarros.backend.repository;

import br.com.aluguelcarros.backend.model.Rendimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RendimentoRepository extends JpaRepository<Rendimento, Long> {
}