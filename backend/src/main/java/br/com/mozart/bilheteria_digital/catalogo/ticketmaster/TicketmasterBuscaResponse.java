package br.com.mozart.bilheteria_digital.catalogo.ticketmaster;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TicketmasterBuscaResponse(
        @JsonProperty("_embedded")
        TicketmasterConteudoResponse embedded
) {
}
