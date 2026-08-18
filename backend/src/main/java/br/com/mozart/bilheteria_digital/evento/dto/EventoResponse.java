package br.com.mozart.bilheteria_digital.evento.dto;

import br.com.mozart.bilheteria_digital.evento.domain.Evento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventoResponse (
        Long id,
        String titulo,
        String tipo,
        String status,
        String nomeLocal,
        LocalDateTime dataHora,
        String tipoCapacidade,
        BigDecimal preco,
        Integer capacidade,
        Integer capacidadeVendida
){

    public static EventoResponse from(Evento evento){
        return new EventoResponse(
                evento.getId(),
                evento.getTitulo(),
                evento.getTipoEvento().name(),
                evento.getStatus().name(),
                evento.getNomeLocal(),
                evento.getDataHora(),
                evento.getTipoCapacidade().name(),
                evento.getPreco(),
                evento.getCapacidade(),
                evento.getCapacidadeVendida()

        );
    }
}
