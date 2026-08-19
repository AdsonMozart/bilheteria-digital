package br.com.mozart.bilheteria_digital.catalogo.tmdb;

import java.util.List;

public record TmdbBuscaResponse(
        Integer page,
        List<TmdbFilmeResumoResponse> results
) {
}
