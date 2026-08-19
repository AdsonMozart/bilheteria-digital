package br.com.mozart.bilheteria_digital.catalogo.tmdb;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.tmdb")
public record TmdbPropriedades(
        String baseUrl,
        String imageBaseUrl,
        String accessToken,
        String language
) {
}
