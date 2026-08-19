package br.com.mozart.bilheteria_digital.portaria.dto;

public record ValidarIngressoRequest(
        Long eventoId,
        String codigo
) {
}
