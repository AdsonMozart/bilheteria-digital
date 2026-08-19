package br.com.mozart.bilheteria_digital.catalogo.ticketmaster;

import java.util.List;

public record TicketmasterConteudoResponse(
        List<TicketmasterEventoResponse> events
) {
}
