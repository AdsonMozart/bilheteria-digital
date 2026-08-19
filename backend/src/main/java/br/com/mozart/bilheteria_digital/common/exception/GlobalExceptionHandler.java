package br.com.mozart.bilheteria_digital.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErroResponse> tratarErroDeValidacao(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> campos = new LinkedHashMap<>();

        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            campos.put(erro.getField(), erro.getDefaultMessage());
        }

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Dados invalidos",
                "Confira os campos enviados",
                request.getRequestURI(),
                campos
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErroResponse> tratarRegraDeNegocio(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        HttpStatus status = statusParaIllegalArgument(ex.getMessage());

        return criarResposta(
                status,
                tituloParaStatus(status),
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErroResponse> tratarEstadoInvalido(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.CONFLICT,
                "Operacao nao permitida",
                ex.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErroResponse> tratarNaoAutenticado(
            AuthenticationException ex,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.UNAUTHORIZED,
                "Nao autenticado",
                "Informe um token valido para acessar este recurso",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErroResponse> tratarAcessoNegado(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.FORBIDDEN,
                "Acesso negado",
                "Voce nao tem permissao para acessar este recurso",
                request.getRequestURI()
        );
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingRequestHeaderException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiErroResponse> tratarRequisicaoMalFormatada(
            Exception ex,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Requisicao invalida",
                "Confira os dados enviados",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErroResponse> tratarErroDeParametro(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        Map<String, String> campos = new LinkedHashMap<>();

        ex.getConstraintViolations().forEach(violacao ->
                campos.put(violacao.getPropertyPath().toString(), violacao.getMessage())
        );

        return criarResposta(
                HttpStatus.BAD_REQUEST,
                "Dados invalidos",
                "Confira os parametros enviados",
                request.getRequestURI(),
                campos
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErroResponse> tratarErroDeIntegridade(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.CONFLICT,
                "Conflito de dados",
                "Nao foi possivel concluir a operacao por conflito com os dados existentes",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErroResponse> tratarErroInesperado(
            Exception ex,
            HttpServletRequest request
    ) {
        return criarResposta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                "Ocorreu um erro inesperado no servidor",
                request.getRequestURI()
        );
    }

    private ResponseEntity<ApiErroResponse> criarResposta(
            HttpStatus status,
            String erro,
            String mensagem,
            String path
    ) {
        return ResponseEntity
                .status(status)
                .body(ApiErroResponse.semCampos(status.value(), erro, mensagem, path));
    }

    private ResponseEntity<ApiErroResponse> criarResposta(
            HttpStatus status,
            String erro,
            String mensagem,
            String path,
            Map<String, String> campos
    ) {
        return ResponseEntity
                .status(status)
                .body(ApiErroResponse.comCampos(status.value(), erro, mensagem, path, campos));
    }

    private HttpStatus statusParaIllegalArgument(String mensagem) {
        String texto = mensagem == null ? "" : mensagem.toLowerCase();

        if (texto.contains("email ou senha invalidos")) {
            return HttpStatus.UNAUTHORIZED;
        }

        if (texto.contains("nao pertence")) {
            return HttpStatus.FORBIDDEN;
        }

        if (texto.contains("nao encontrado") || texto.contains("nao encontrada")) {
            return HttpStatus.NOT_FOUND;
        }

        return HttpStatus.BAD_REQUEST;
    }

    private String tituloParaStatus(HttpStatus status) {
        return switch (status) {
            case UNAUTHORIZED -> "Nao autenticado";
            case FORBIDDEN -> "Acesso negado";
            case NOT_FOUND -> "Recurso nao encontrado";
            default -> "Requisicao invalida";
        };
    }
}
