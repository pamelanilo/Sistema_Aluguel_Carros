package br.com.aluguelcarros.backend.service;

import br.com.aluguelcarros.backend.dto.AutomovelRequest;
import br.com.aluguelcarros.backend.exception.RecursoNaoEncontradoException;
import br.com.aluguelcarros.backend.exception.RegraNegocioException;
import br.com.aluguelcarros.backend.model.Automovel;
import br.com.aluguelcarros.backend.model.Usuario;
import br.com.aluguelcarros.backend.repository.AutomovelRepository;
import br.com.aluguelcarros.backend.repository.PedidoRepository;
import br.com.aluguelcarros.backend.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;

@Service
@Transactional
public class AutomovelService {

    private final AutomovelRepository automovelRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;

    public AutomovelService(
            AutomovelRepository automovelRepository,
            UsuarioRepository usuarioRepository,
            PedidoRepository pedidoRepository) {

        this.automovelRepository = automovelRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
    }

    // Cadastrar automóvel
    public Automovel cadastrar(AutomovelRequest dados) {
        Automovel automovel = new Automovel();
        preencher(automovel, dados);
        return automovelRepository.save(automovel);
    }

    // Listar automóveis
    @Transactional(readOnly = true)
    public List<Automovel> listarTodos() {
        return automovelRepository.findAll();
    }

    // Buscar automóvel pelo ID
    @Transactional(readOnly = true)
    public Automovel buscarPorId(Long id) {
        return automovelRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Automóvel não encontrado"));
    }

    // Atualizar automóvel
    public Automovel atualizar(Long id, AutomovelRequest dados) {
        Automovel automovel = buscarPorId(id);
        preencher(automovel, dados);
        return automovelRepository.save(automovel);
    }

    // Excluir automóvel (somente se nunca foi usado em um pedido)
    public void excluir(Long id) {
        Automovel automovel = buscarPorId(id);
        if (pedidoRepository.existsByAutomovel_Id(id)) {
            throw new RegraNegocioException(
                    "Este automóvel possui pedidos registrados e não pode ser excluído.");
        }
        automovelRepository.delete(automovel);
    }

    private void preencher(Automovel automovel, AutomovelRequest dados) {
        if (dados == null
                || vazio(dados.getMatricula()) || vazio(dados.getMarca())
                || vazio(dados.getModelo()) || vazio(dados.getPlaca())) {
            throw new RegraNegocioException("Informe matrícula, marca, modelo e placa.");
        }
        int anoMaximo = Year.now().getValue() + 1;
        if (dados.getAno() < 1900 || dados.getAno() > anoMaximo) {
            throw new RegraNegocioException("Informe um ano entre 1900 e " + anoMaximo + ".");
        }
        if (dados.getProprietarioId() == null) {
            throw new RegraNegocioException("Informe o proprietário do automóvel.");
        }

        Usuario proprietario = usuarioRepository.findById(dados.getProprietarioId())
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Proprietário não encontrado"));

        automovel.setMatricula(dados.getMatricula().trim());
        automovel.setAno(dados.getAno());
        automovel.setMarca(dados.getMarca().trim());
        automovel.setModelo(dados.getModelo().trim());
        automovel.setPlaca(dados.getPlaca().replaceAll("[^A-Za-z0-9]", "").toUpperCase());
        automovel.setProprietario(proprietario);
    }

    private static boolean vazio(String s) {
        return s == null || s.isBlank();
    }
}
