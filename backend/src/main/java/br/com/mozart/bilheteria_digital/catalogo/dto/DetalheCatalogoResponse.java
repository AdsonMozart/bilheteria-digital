package br.com.mozart.bilheteria_digital.catalogo.dto;

import br.com.mozart.bilheteria_digital.evento.domain.OrigemExterna;
import br.com.mozart.bilheteria_digital.evento.domain.TipoEvento;

import java.time.LocalDate;
import java.util.List;

public record DetalheCatalogoResponse(
        OrigemExterna origem,
        String idExterno,
        String titulo,
        TipoEvento tipo,
        String descricao,
        String urlImagem,
        LocalDate dataLancamento,
        Integer duracaoMinutos,
        List<String> generos,
        Double avaliacao,
        Integer totalVotos
) {
}
