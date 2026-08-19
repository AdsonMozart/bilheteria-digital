package br.com.mozart.bilheteria_digital.catalogo.ticketmaster;

import br.com.mozart.bilheteria_digital.catalogo.dto.DetalheCatalogoResponse;
import br.com.mozart.bilheteria_digital.catalogo.dto.ItemCatalogoResponse;
import br.com.mozart.bilheteria_digital.evento.domain.OrigemExterna;
import br.com.mozart.bilheteria_digital.evento.domain.TipoEvento;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Component
public class TicketmasterCliente {

    private final TicketmasterPropriedades properties;
    private final RestClient restClient;

    public TicketmasterCliente(TicketmasterPropriedades properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }

    public List<ItemCatalogoResponse> buscarEventos(String termo) {
        validarConfiguracao();

        if (!StringUtils.hasText(termo)) {
            throw new IllegalArgumentException("Termo de busca e obrigatorio");
        }

        try {
            TicketmasterBuscaResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/events.json")
                            .queryParam("apikey", properties.apiKey())
                            .queryParam("keyword", termo)
                            .queryParam("locale", properties.locale())
                            .queryParam("countryCode", properties.countryCode())
                            .queryParam("size", 20)
                            .queryParam("page", 0)
                            .build())
                    .retrieve()
                    .body(TicketmasterBuscaResponse.class);

            if (response == null || response.embedded() == null || response.embedded().events() == null) {
                return List.of();
            }

            return response.embedded()
                    .events()
                    .stream()
                    .map(this::mapearItem)
                    .toList();
        } catch (RestClientException ex) {
            throw new IllegalStateException("Falha ao consultar eventos no Ticketmaster", ex);
        }
    }

    public DetalheCatalogoResponse detalharEvento(String idExterno) {
        validarConfiguracao();

        if (!StringUtils.hasText(idExterno)) {
            throw new IllegalArgumentException("Id externo e obrigatorio");
        }

        try {
            TicketmasterEventoResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/events/{id}.json")
                            .queryParam("apikey", properties.apiKey())
                            .queryParam("locale", properties.locale())
                            .build(idExterno))
                    .retrieve()
                    .body(TicketmasterEventoResponse.class);

            if (response == null) {
                throw new IllegalArgumentException("Evento nao encontrado no Ticketmaster");
            }

            return mapearDetalhe(response);
        } catch (RestClientException ex) {
            throw new IllegalStateException("Falha ao consultar detalhes do evento no Ticketmaster", ex);
        }
    }

    private ItemCatalogoResponse mapearItem(TicketmasterEventoResponse evento) {
        return new ItemCatalogoResponse(
                OrigemExterna.TICKETMASTER,
                evento.id(),
                evento.name(),
                TipoEvento.SHOW,
                montarDescricao(evento),
                escolherImagem(evento.images()),
                parseDate(evento.localDate()),
                null,
                null
        );
    }

    private DetalheCatalogoResponse mapearDetalhe(TicketmasterEventoResponse evento) {
        return new DetalheCatalogoResponse(
                OrigemExterna.TICKETMASTER,
                evento.id(),
                evento.name(),
                TipoEvento.SHOW,
                montarDescricao(evento),
                escolherImagem(evento.images()),
                parseDate(evento.localDate()),
                null,
                mapearGeneros(evento.classifications()),
                null,
                null
        );
    }

    private String montarDescricao(TicketmasterEventoResponse evento) {
        if (StringUtils.hasText(evento.info())) {
            return evento.info();
        }

        if (StringUtils.hasText(evento.pleaseNote())) {
            return evento.pleaseNote();
        }

        List<String> generos = mapearGeneros(evento.classifications());
        if (!generos.isEmpty()) {
            return String.join(", ", generos);
        }

        return null;
    }

    private String escolherImagem(List<TicketmasterImagemResponse> imagens) {
        if (imagens == null || imagens.isEmpty()) {
            return null;
        }

        return imagens.stream()
                .filter(imagem -> StringUtils.hasText(imagem.url()))
                .max(Comparator.comparingInt(TicketmasterImagemResponse::area))
                .map(TicketmasterImagemResponse::url)
                .orElse(null);
    }

    private List<String> mapearGeneros(List<TicketmasterClassificacaoResponse> classificacoes) {
        if (classificacoes == null) {
            return List.of();
        }

        return classificacoes.stream()
                .flatMap(classificacao -> List.of(
                        classificacao.segmentName(),
                        classificacao.genreName(),
                        classificacao.subGenreName()
                ).stream())
                .filter(StringUtils::hasText)
                .distinct()
                .toList();
    }

    private LocalDate parseDate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return LocalDate.parse(value);
    }

    private void validarConfiguracao() {
        if (!StringUtils.hasText(properties.apiKey())) {
            throw new IllegalStateException("TICKETMASTER_API_KEY nao configurada");
        }
    }
}
