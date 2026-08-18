package br.com.mozart.bilheteria_digital.reserva.dto;

import java.util.List;

public record CriarReservaRequest(
        Long eventoId,
        Integer quantidade,
        List<Long> assentoIds
) {
}
