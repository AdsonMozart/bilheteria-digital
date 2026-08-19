package br.com.mozart.bilheteria_digital.reserva.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record CriarReservaRequest(
        @NotNull(message = "Evento e obrigatorio")
        Long eventoId,

        @Positive(message = "Quantidade deve ser maior que zero")
        Integer quantidade,

        List<Long> assentoIds
) {
}
