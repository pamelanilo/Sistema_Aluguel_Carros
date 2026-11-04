package br.com.aluguelcarros.backend.repository;

import br.com.aluguelcarros.backend.model.AgenteEmpresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgenteEmpresaRepository extends JpaRepository<AgenteEmpresa, Long> {
}