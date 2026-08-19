package br.com.mozart.bilheteria_digital.catalogo.ticketmaster;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.ticketmaster")
public record TicketmasterPropriedades(
        String baseUrl,
        String apiKey,
        String locale,
        String countryCode
) {
}
