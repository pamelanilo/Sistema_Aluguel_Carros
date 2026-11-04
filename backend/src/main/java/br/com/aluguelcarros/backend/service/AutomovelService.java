
package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.dto.AutomovelRequest;
import br.com.aluguelcarros.backend.model.Automovel;
import br.com.aluguelcarros.backend.model.Usuario;
import br.com.aluguelcarros.backend.repository.AutomovelRepository;
import br.com.aluguelcarros.backend.repository.UsuarioRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutomovelService {

    private final AutomovelRepository automovelRepository;
    private final UsuarioRepository usuarioRepository;

    public AutomovelService(
            AutomovelRepository automovelRepository,
            UsuarioRepository usuarioRepository) {

        this.automovelRepository = automovelRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Cadastrar automóvel
    public Automovel cadastrar(AutomovelRequest dados) {

        Usuario proprietario = usuarioRepository.findById(dados.getProprietarioId())
                .orElseThrow(() ->
                        new RuntimeException("Proprietário não encontrado"));

        Automovel automovel = new Automovel();

        automovel.setMatricula(dados.getMatricula());
        automovel.setAno(dados.getAno());
        automovel.setMarca(dados.getMarca());
        automovel.setModelo(dados.getModelo());
        automovel.setPlaca(dados.getPlaca());
        automovel.setProprietario(proprietario);

        return automovelRepository.save(automovel);
    }

    // Listar automóveis
    public List<Automovel> listarTodos() {
        return automovelRepository.findAll();
    }

    // Buscar automóvel pelo ID
    public Automovel buscarPorId(Long id) {

        return automovelRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Automóvel não encontrado"));
    }

    // Atualizar automóvel
    public Automovel atualizar(Long id, AutomovelRequest dados) {

        Automovel automovel = buscarPorId(id);

        Usuario proprietario = usuarioRepository.findById(dados.getProprietarioId())
                .orElseThrow(() ->
                        new RuntimeException("Proprietário não encontrado"));

        automovel.setMatricula(dados.getMatricula());
        automovel.setAno(dados.getAno());
        automovel.setMarca(dados.getMarca());
        automovel.setModelo(dados.getModelo());
        automovel.setPlaca(dados.getPlaca());
        automovel.setProprietario(proprietario);

        return automovelRepository.save(automovel);
    }

    // Excluir automóvel
    public void excluir(Long id) {

        Automovel automovel = buscarPorId(id);

        automovelRepository.delete(automovel);
    }
}
