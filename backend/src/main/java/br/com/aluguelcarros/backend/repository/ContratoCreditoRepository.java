
package br.com.aluguelcarros.backend.repository;

import br.com.aluguelcarros.backend.model.ContratoCredito;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContratoCreditoRepository
        extends JpaRepository<ContratoCredito, Long> {
}
