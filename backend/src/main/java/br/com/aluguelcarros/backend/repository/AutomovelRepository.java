package br.com.aluguelcarros.backend.repository;

import br.com.aluguelcarros.backend.model.Automovel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutomovelRepository extends JpaRepository<Automovel, Long> {
}