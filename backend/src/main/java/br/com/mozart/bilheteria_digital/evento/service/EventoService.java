package br.com.mozart.bilheteria_digital.evento.service;

import br.com.mozart.bilheteria_digital.evento.domain.Evento;
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
}
