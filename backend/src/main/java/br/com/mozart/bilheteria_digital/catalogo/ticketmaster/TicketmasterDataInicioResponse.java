package br.com.mozart.bilheteria_digital.catalogo.ticketmaster;

public record TicketmasterDataInicioResponse(
        String localDate,
        String localTime,
        String dateTime
) {
}
