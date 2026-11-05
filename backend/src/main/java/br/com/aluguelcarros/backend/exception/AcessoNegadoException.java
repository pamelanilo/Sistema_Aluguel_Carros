package br.com.aluguelcarros.backend.exception;

/** Usuário autenticado tentando acessar um recurso que não lhe pertence (HTTP 403). */
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
