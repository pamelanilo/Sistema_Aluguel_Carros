package br.com.aluguelcarros.backend.repository;

import br.com.aluguelcarros.backend.model.Pedido;
import br.com.aluguelcarros.backend.model.enums.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByCliente_Login(String login);

    boolean existsByCliente_Id(Long clienteId);

    boolean existsByAutomovel_Id(Long automovelId);

    /** Pedidos do automóvel, nos estados informados, cujo período se sobrepõe a [inicio, fim]. */
    List<Pedido> findByAutomovel_IdAndStatusInAndDataInicioLessThanEqualAndDataFimGreaterThanEqual(
            Long automovelId, Collection<StatusPedido> status, LocalDate fim, LocalDate inicio);
}
