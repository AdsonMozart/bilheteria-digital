package br.com.mozart.bilheteria_digital.catalogo.ticketmaster;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TicketmasterEventoResponse(
        String id,
        String name,
        String info,
        @JsonProperty("pleaseNote")
        String pleaseNote,
        List<TicketmasterImagemResponse> images,
        TicketmasterDatasResponse dates,
        List<TicketmasterClassificacaoResponse> classifications
) {
    String localDate() {
        if (dates == null || dates.start() == null) {
            return null;
        }

        return dates.start().localDate();
    }
}
