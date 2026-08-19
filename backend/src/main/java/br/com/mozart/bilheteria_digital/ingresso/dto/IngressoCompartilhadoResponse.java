package br.com.mozart.bilheteria_digital.ingresso.dto;

import br.com.mozart.bilheteria_digital.ingresso.domain.Ingresso;

import java.time.LocalDateTime;

public record IngressoCompartilhadoResponse(
        String codigo,
        String status,
        String tituloEvento,
        String nomeLocal,
        String enderecoLocal,
        LocalDateTime dataHora
) {
    public static IngressoCompartilhadoResponse from(Ingresso ingresso) {
        return new IngressoCompartilhadoResponse(
                ingresso.getCodigo(),
                ingresso.getStatus().name(),
                ingresso.getReserva().getEvento().getTitulo(),
                ingresso.getReserva().getEvento().getNomeLocal(),
                ingresso.getReserva().getEvento().getEnderecoLocal(),
                ingresso.getReserva().getEvento().getDataHora()
        );
    }
}
