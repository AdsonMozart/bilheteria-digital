package br.com.mozart.bilheteria_digital.usuario.dto;

public record UsuarioLogadoResponse(
        Long id,
        String nome,
        String email,
        String nivelAcesso
) {
}
