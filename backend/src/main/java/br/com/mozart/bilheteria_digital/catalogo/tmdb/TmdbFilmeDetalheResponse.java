package br.com.mozart.bilheteria_digital.catalogo.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TmdbFilmeDetalheResponse(
        Long id,
        String title,
        String overview,
        @JsonProperty("poster_path")
        String posterPath,
        @JsonProperty("release_date")
        String releaseDate,
        Integer runtime,
        List<TmdbGeneroResponse> genres,
        @JsonProperty("vote_average")
        Double voteAverage,
        @JsonProperty("vote_count")
        Integer voteCount
) {
}
