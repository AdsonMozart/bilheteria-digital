package br.com.mozart.bilheteria_digital.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiErroResponse(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String path,
        Map<String, String> campos
) {

    public static ApiErroResponse semCampos(int status, String erro, String mensagem, String path) {
        return new ApiErroResponse(
                LocalDateTime.now(),
                status,
                erro,
                mensagem,
                path,
                null
        );
    }

    public static ApiErroResponse comCampos(int status, String erro, String mensagem, String path, Map<String, String> campos) {
        return new ApiErroResponse(
                LocalDateTime.now(),
                status,
                erro,
                mensagem,
                path,
                campos
        );
    }
}
