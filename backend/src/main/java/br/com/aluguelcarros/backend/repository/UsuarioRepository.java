package br.com.aluguelcarros.backend.repository;

import br.com.aluguelcarros.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

    /** Login é único entre TODOS os usuários (clientes, empresas e bancos). */
    boolean existsByLogin(String login);

    boolean existsByLoginAndIdNot(String login, Long id);
}
