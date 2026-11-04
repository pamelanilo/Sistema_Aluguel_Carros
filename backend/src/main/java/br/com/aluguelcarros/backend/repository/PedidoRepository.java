package br.com.aluguelcarros.backend.repository;

import br.com.aluguelcarros.backend.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    java.util.List<Pedido> findByCliente_Login(String login);
}