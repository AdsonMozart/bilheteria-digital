package br.com.mozart.bilheteria_digital.catalogo.tmdb;

import br.com.mozart.bilheteria_digital.catalogo.dto.DetalheCatalogoResponse;
import br.com.mozart.bilheteria_digital.catalogo.dto.ItemCatalogoResponse;
import br.com.mozart.bilheteria_digital.evento.domain.OrigemExterna;
import br.com.mozart.bilheteria_digital.evento.domain.TipoEvento;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.List;

@Component
public class TmdbCliente {

    private final TmdbPropriedades properties;
    private final RestClient restClient;

    public TmdbCliente(TmdbPropriedades properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.accessToken())
                .build();
    }

    public List<ItemCatalogoResponse> buscarFilmes(String termo) {
        validarConfiguracao();

        if (!StringUtils.hasText(termo)) {
            throw new IllegalArgumentException("Termo de busca e obrigatorio");
        }

        try {
            TmdbBuscaResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/movie")
                            .queryParam("query", termo)
                            .queryParam("language", properties.language())
                            .queryParam("include_adult", false)
                            .queryParam("page", 1)
                            .build())
                    .retrieve()
                    .body(TmdbBuscaResponse.class);

            if (response == null || response.results() == null) {
                return List.of();
            }

            return response.results()
                    .stream()
                    .map(this::mapearItem)
                    .toList();
        } catch (RestClientException ex) {
            throw new IllegalStateException("Falha ao consultar filmes no TMDb", ex);
        }
    }

    public DetalheCatalogoResponse detalharFilme(String idExterno) {
        validarConfiguracao();

        if (!StringUtils.hasText(idExterno)) {
            throw new IllegalArgumentException("Id externo e obrigatorio");
        }

        try {
            TmdbFilmeDetalheResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/movie/{id}")
                            .queryParam("language", properties.language())
                            .build(idExterno))
                    .retrieve()
                    .body(TmdbFilmeDetalheResponse.class);

            if (response == null) {
                throw new IllegalArgumentException("Filme nao encontrado no TMDb");
            }

            return mapearDetalhe(response);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Falha ao consultar detalhes do filme no TMDb", ex);
        }
    }

    private ItemCatalogoResponse mapearItem(TmdbFilmeResumoResponse movie) {
        return new ItemCatalogoResponse(
                OrigemExterna.TMDB,
                String.valueOf(movie.id()),
                movie.title(),
                TipoEvento.FILME,
                movie.overview(),
                montarUrlImagem(movie.posterPath()),
                parseDate(movie.releaseDate()),
                movie.voteAverage(),
                movie.voteCount()
        );
    }

    private DetalheCatalogoResponse mapearDetalhe(TmdbFilmeDetalheResponse movie) {
        return new DetalheCatalogoResponse(
                OrigemExterna.TMDB,
                String.valueOf(movie.id()),
                movie.title(),
                TipoEvento.FILME,
                movie.overview(),
                montarUrlImagem(movie.posterPath()),
                parseDate(movie.releaseDate()),
                movie.runtime(),
                movie.genres() == null ? List.of() : movie.genres().stream().map(TmdbGeneroResponse::name).toList(),
                movie.voteAverage(),
                movie.voteCount()
        );
    }

    private String montarUrlImagem(String posterPath) {
        if (!StringUtils.hasText(posterPath)) {
            return null;
        }

        return properties.imageBaseUrl() + posterPath;
    }

    private LocalDate parseDate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return LocalDate.parse(value);
    }

    private void validarConfiguracao() {
        if (!StringUtils.hasText(properties.accessToken())) {
            throw new IllegalStateException("TMDB_ACCESS_TOKEN nao configurado");
        }
    }
}
