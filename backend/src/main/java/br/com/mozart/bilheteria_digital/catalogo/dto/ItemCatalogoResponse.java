package br.com.mozart.bilheteria_digital.catalogo.dto;

import br.com.mozart.bilheteria_digital.evento.domain.OrigemExterna;
import br.com.mozart.bilheteria_digital.evento.domain.TipoEvento;

import java.time.LocalDate;

public record ItemCatalogoResponse(
        OrigemExterna origem,
        String idExterno,
        String titulo,
        TipoEvento tipo,
        String descricao,
        String urlImagem,
        LocalDate dataLancamento,
        Double avaliacao,
        Integer totalVotos
) {
}
