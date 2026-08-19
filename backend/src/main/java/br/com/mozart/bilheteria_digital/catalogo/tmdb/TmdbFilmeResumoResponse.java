package br.com.mozart.bilheteria_digital.catalogo.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TmdbFilmeResumoResponse(
        Long id,
        String title,
        String overview,
        @JsonProperty("poster_path")
        String posterPath,
        @JsonProperty("release_date")
        String releaseDate,
        @JsonProperty("vote_average")
        Double voteAverage,
        @JsonProperty("vote_count")
        Integer voteCount
) {
}
