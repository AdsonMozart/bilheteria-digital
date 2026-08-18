package br.com.mozart.bilheteria_digital.ingresso.dto;

import br.com.mozart.bilheteria_digital.ingresso.domain.Ingresso;

import java.time.LocalDateTime;

public record IngressoResponse(
        Long id,
        Long reservaId,
        Long eventoId,
        String tituloEvento,
        String codigo,
        String assinaturaQr,
        String tokenCompartilhamento,
        String status,
        LocalDateTime validadoEm,
        LocalDateTime dataCriacao
) {
    public static IngressoResponse from(Ingresso ingresso) {
        return new IngressoResponse(
                ingresso.getId(),
                ingresso.getReserva().getId(),
                ingresso.getReserva().getEvento().getId(),
                ingresso.getReserva().getEvento().getTitulo(),
                ingresso.getCodigo(),
                ingresso.getAssinaturaQr(),
                ingresso.getTokenCompartilhamento(),
                ingresso.getStatus().name(),
                ingresso.getValidadoEm(),
                ingresso.getDataCriacao()
        );
    }
}
