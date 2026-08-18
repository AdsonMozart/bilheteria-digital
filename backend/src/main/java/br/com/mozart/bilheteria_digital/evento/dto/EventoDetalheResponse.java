package br.com.mozart.bilheteria_digital.evento.dto;

import br.com.mozart.bilheteria_digital.assento.dto.AssentoResponse;
import br.com.mozart.bilheteria_digital.evento.domain.Evento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EventoDetalheResponse(
        Long id,
        String titulo,
        String tipo,
        String status,
        String descricao,
        String urlImagem,
        String nomeLocal,
        String enderecoLocal,
        LocalDateTime dataHora,
        String tipoCapacidade,
        BigDecimal preco,
        Integer capacidade,
        Integer capacidadeVendida,
        List<AssentoResponse> assentos
) {
    public static EventoDetalheResponse from(Evento evento, List<AssentoResponse> assentos) {
        return new EventoDetalheResponse(
                evento.getId(),
                evento.getTitulo(),
                evento.getTipoEvento().name(),
                evento.getStatus().name(),
                evento.getDescricao(),
                evento.getUrlImagem(),
                evento.getNomeLocal(),
                evento.getEnderecoLocal(),
                evento.getDataHora(),
                evento.getTipoCapacidade().name(),
                evento.getPreco(),
                evento.getCapacidade(),
                evento.getCapacidadeVendida(),
                assentos
        );
    }
}
