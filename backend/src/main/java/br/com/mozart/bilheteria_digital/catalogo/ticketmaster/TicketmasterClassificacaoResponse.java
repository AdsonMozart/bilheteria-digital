package br.com.mozart.bilheteria_digital.catalogo.ticketmaster;

public record TicketmasterClassificacaoResponse(
        TicketmasterValorNomeadoResponse segment,
        TicketmasterValorNomeadoResponse genre,
        TicketmasterValorNomeadoResponse subGenre
) {
    String segmentName() {
        return segment == null ? null : segment.name();
    }

    String genreName() {
        return genre == null ? null : genre.name();
    }

    String subGenreName() {
        return subGenre == null ? null : subGenre.name();
    }
}
