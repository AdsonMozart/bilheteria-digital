package br.com.mozart.bilheteria_digital.evento.service;

import br.com.mozart.bilheteria_digital.evento.domain.Evento;
import br.com.mozart.bilheteria_digital.evento.domain.StatusEvento;
import br.com.mozart.bilheteria_digital.evento.dto.CriarEventoRequest;
import br.com.mozart.bilheteria_digital.evento.dto.EventoResponse;
import br.com.mozart.bilheteria_digital.evento.repository.EventoRepository;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;

    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public EventoResponse criarEvento(Usuario organizador, CriarEventoRequest request) {
        Evento evento = new Evento(
                organizador,
                request.origemExterna(),
                request.idExterno(),
                request.titulo(),
                request.tipo(),
                request.descricao(),
                request.urlImagem(),
                request.nomeLocal(),
                request.enderecoLocal(),
                request.dataHora(),
                request.tipoCapacidade(),
                request.preco(),
                request.capacidade()
        );

        Evento eventoSalvo = eventoRepository.save(evento);
        return EventoResponse.from(eventoSalvo);
    }

    public List<EventoResponse> listarEventosDoOrganizador(Usuario organizador) {
        return eventoRepository.findByOrganizadorId(organizador.getId())
                .stream()
                .map(EventoResponse::from)
                .toList();
    }

    public EventoResponse publicarEvento(Usuario organizador, Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId).orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado"));

        if (!evento.getOrganizador().getId().equals(organizador.getId())) {
            throw new IllegalArgumentException("Evento nao pertence ao organizador logado");
        }

        evento.publicar();

        Evento eventoSalvo = eventoRepository.save(evento);
        return EventoResponse.from(eventoSalvo);
    }

    public EventoResponse cancelarEvento(Usuario organizador, Long eventoId) {
        Evento evento = buscarEventoDoOrganizador(organizador, eventoId);

        evento.cancelar();

        return EventoResponse.from(eventoRepository.save(evento));
    }

    public List<EventoResponse> listarEventosPublicados() {
        return eventoRepository.findByStatus(StatusEvento.PUBLICADO)
                .stream()
                .map(EventoResponse::from)
                .toList();
    }

    public EventoResponse detalharEventoPublicado(Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado"));

        if (!evento.estaPublicado()) {
            throw new IllegalArgumentException("Evento nao encontrado");
        }

        return EventoResponse.from(evento);
    }

    private Evento buscarEventoDoOrganizador(Usuario organizador, Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado"));

        if (!evento.pertenceAoOrganizador(organizador.getId())) {
            throw new IllegalArgumentException("Evento nao pertence ao organizador logado");
        }

        return evento;
    }
}
