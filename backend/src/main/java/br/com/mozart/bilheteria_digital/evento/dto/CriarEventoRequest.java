package br.com.mozart.bilheteria_digital.evento.dto;

import br.com.mozart.bilheteria_digital.evento.domain.OrigemExterna;
import br.com.mozart.bilheteria_digital.evento.domain.TipoCapacidade;
import br.com.mozart.bilheteria_digital.evento.domain.TipoEvento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CriarEventoRequest(
        OrigemExterna origemExterna,
        String idExterno,
        String titulo,
        TipoEvento tipo,
        String descricao,
        String urlImagem,
        String nomeLocal,
        String enderecoLocal,
        LocalDateTime dataHora,
        TipoCapacidade tipoCapacidade,
        BigDecimal preco,
        Integer capacidade
) {
}
