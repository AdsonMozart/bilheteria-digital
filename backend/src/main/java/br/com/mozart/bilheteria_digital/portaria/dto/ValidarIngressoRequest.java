package br.com.mozart.bilheteria_digital.portaria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ValidarIngressoRequest(
        @NotNull(message = "Evento e obrigatorio")
        Long eventoId,

        @NotBlank(message = "Codigo do ingresso e obrigatorio")
        String codigo
) {
}
