package br.com.mozart.bilheteria_digital.catalogo.ticketmaster;

public record TicketmasterImagemResponse(
        String url,
        Integer width,
        Integer height
) {
    int area() {
        return safe(width) * safe(height);
    }

    private int safe(Integer value) {
        return value == null ? 0 : value;
    }
}
