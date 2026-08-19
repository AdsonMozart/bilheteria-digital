package br.com.mozart.bilheteria_digital.catalogo.service;

import br.com.mozart.bilheteria_digital.catalogo.dto.DetalheCatalogoResponse;
import br.com.mozart.bilheteria_digital.catalogo.dto.ItemCatalogoResponse;
import br.com.mozart.bilheteria_digital.catalogo.ticketmaster.TicketmasterCliente;
import br.com.mozart.bilheteria_digital.catalogo.tmdb.TmdbCliente;
import br.com.mozart.bilheteria_digital.evento.domain.OrigemExterna;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogoService {

    private final TmdbCliente tmdbCliente;
    private final TicketmasterCliente ticketmasterCliente;

    public CatalogoService(TmdbCliente tmdbCliente, TicketmasterCliente ticketmasterCliente) {
        this.tmdbCliente = tmdbCliente;
        this.ticketmasterCliente = ticketmasterCliente;
    }

    public List<ItemCatalogoResponse> buscar(OrigemExterna origem, String termo) {
        if (origem == OrigemExterna.TMDB) {
            return tmdbCliente.buscarFilmes(termo);
        }

        if (origem == OrigemExterna.TICKETMASTER) {
            return ticketmasterCliente.buscarEventos(termo);
        }

        throw new IllegalArgumentException("Origem de catalogo ainda nao suportada: " + origem);
    }

    public DetalheCatalogoResponse detalhar(OrigemExterna origem, String idExterno) {
        if (origem == OrigemExterna.TMDB) {
            return tmdbCliente.detalharFilme(idExterno);
        }

        if (origem == OrigemExterna.TICKETMASTER) {
            return ticketmasterCliente.detalharEvento(idExterno);
        }

        throw new IllegalArgumentException("Origem de catalogo ainda nao suportada: " + origem);
    }
}
