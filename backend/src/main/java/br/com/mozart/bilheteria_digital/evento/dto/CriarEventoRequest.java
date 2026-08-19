package br.com.mozart.bilheteria_digital.evento.dto;

import br.com.mozart.bilheteria_digital.evento.domain.OrigemExterna;
import br.com.mozart.bilheteria_digital.evento.domain.TipoCapacidade;
import br.com.mozart.bilheteria_digital.evento.domain.TipoEvento;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CriarEventoRequest(
        @NotNull(message = "Origem externa e obrigatoria")
        OrigemExterna origemExterna,

        String idExterno,

        @NotBlank(message = "Titulo e obrigatorio")
        String titulo,

        @NotNull(message = "Tipo do evento e obrigatorio")
        TipoEvento tipo,

        String descricao,

        String urlImagem,

        @NotBlank(message = "Nome do local e obrigatorio")
        String nomeLocal,

        String enderecoLocal,

        @NotNull(message = "Data e hora sao obrigatorias")
        @Future(message = "Data e hora devem estar no futuro")
        LocalDateTime dataHora,

        @NotNull(message = "Tipo de capacidade e obrigatorio")
        TipoCapacidade tipoCapacidade,

        @NotNull(message = "Preco e obrigatorio")
        @Positive(message = "Preco deve ser maior que zero")
        BigDecimal preco,

        @NotNull(message = "Capacidade e obrigatoria")
        @Positive(message = "Capacidade deve ser maior que zero")
        Integer capacidade
) {
}
