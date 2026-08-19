package br.com.mozart.bilheteria_digital.evento.service;

import br.com.mozart.bilheteria_digital.assento.domain.StatusAssento;
import br.com.mozart.bilheteria_digital.assento.repository.AssentoRepository;
import br.com.mozart.bilheteria_digital.assento.service.AssentoService;
import br.com.mozart.bilheteria_digital.evento.domain.Evento;
import br.com.mozart.bilheteria_digital.evento.domain.StatusEvento;
import br.com.mozart.bilheteria_digital.evento.domain.TipoEvento;
import br.com.mozart.bilheteria_digital.evento.dto.CriarEventoRequest;
import br.com.mozart.bilheteria_digital.evento.dto.EventoDetalheResponse;
import br.com.mozart.bilheteria_digital.evento.dto.EventoResponse;
import br.com.mozart.bilheteria_digital.evento.repository.EventoRepository;
import br.com.mozart.bilheteria_digital.ingresso.domain.Ingresso;
import br.com.mozart.bilheteria_digital.ingresso.repository.IngressoRepository;
import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import br.com.mozart.bilheteria_digital.reserva.domain.StatusReserva;
import br.com.mozart.bilheteria_digital.reserva.repository.ReservaRepository;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EventoService {

    private final EventoRepository eventoRepository;
    private final AssentoService assentoService;
    private final ReservaRepository reservaRepository;
    private final AssentoRepository assentoRepository;
    private final IngressoRepository ingressoRepository;

    public EventoService(
            EventoRepository eventoRepository,
            AssentoService assentoService,
            ReservaRepository reservaRepository,
            AssentoRepository assentoRepository,
            IngressoRepository ingressoRepository
    ) {
        this.eventoRepository = eventoRepository;
        this.assentoService = assentoService;
        this.reservaRepository = reservaRepository;
        this.assentoRepository = assentoRepository;
        this.ingressoRepository = ingressoRepository;
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
        return eventoRepository.findByOrganizador_Id(organizador.getId())
                .stream()
                .map(EventoResponse::from)
                .toList();
    }

    @Transactional
    public EventoResponse publicarEvento(Usuario organizador, Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId).orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado"));

        if (!evento.getOrganizador().getId().equals(organizador.getId())) {
            throw new IllegalArgumentException("Evento nao pertence ao organizador logado");
        }

        evento.publicar();
        assentoService.gerarAssentosParaEvento(evento);

        Evento eventoSalvo = eventoRepository.save(evento);
        return EventoResponse.from(eventoSalvo);
    }

    @Transactional
    public EventoResponse cancelarEvento(Usuario organizador, Long eventoId) {
        Evento evento = buscarEventoDoOrganizador(organizador, eventoId);

        evento.cancelar();
        cancelarReservasDoEvento(evento);
        cancelarIngressosDoEvento(evento);

        return EventoResponse.from(eventoRepository.save(evento));
    }

    public List<EventoResponse> listarEventosPublicados() {
        return eventoRepository.findByStatus(StatusEvento.PUBLICADO)
                .stream()
                .map(EventoResponse::from)
                .toList();
    }

    public Page<EventoResponse> buscarEventosPublicados(
            String titulo,
            TipoEvento tipo,
            String local,
            LocalDate dataInicio,
            LocalDate dataFim,
            Long organizadorId,
            Pageable pageable
    ) {
        return eventoRepository.findAll(
                        montarFiltroEventosPublicados(titulo, tipo, local, dataInicio, dataFim, organizadorId),
                        pageable
                )
                .map(EventoResponse::from);
    }

    public EventoDetalheResponse detalharEventoPublicado(Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado"));

        if (!evento.estaPublicado()) {
            throw new IllegalArgumentException("Evento nao encontrado");
        }

        return EventoDetalheResponse.from(evento, assentoService.listarAssentosDoEvento(evento.getId()));
    }

    private Evento buscarEventoDoOrganizador(Usuario organizador, Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado"));

        if (!evento.pertenceAoOrganizador(organizador.getId())) {
            throw new IllegalArgumentException("Evento nao pertence ao organizador logado");
        }

        return evento;
    }

    private Specification<Evento> montarFiltroEventosPublicados(
            String titulo,
            TipoEvento tipo,
            String local,
            LocalDate dataInicio,
            LocalDate dataFim,
            Long organizadorId
    ) {
        return (root, query, builder) -> {
            List<Predicate> filtros = new ArrayList<>();

            filtros.add(builder.equal(root.get("status"), StatusEvento.PUBLICADO));

            if (titulo != null && !titulo.isBlank()) {
                filtros.add(builder.like(
                        builder.lower(root.get("titulo")),
                        "%" + titulo.toLowerCase() + "%"
                ));
            }

            if (tipo != null) {
                filtros.add(builder.equal(root.get("tipoEvento"), tipo));
            }

            if (local != null && !local.isBlank()) {
                String termoLocal = "%" + local.toLowerCase() + "%";
                filtros.add(builder.or(
                        builder.like(builder.lower(root.get("nomeLocal")), termoLocal),
                        builder.like(builder.lower(root.get("enderecoLocal")), termoLocal)
                ));
            }

            if (dataInicio != null) {
                filtros.add(builder.greaterThanOrEqualTo(
                        root.get("dataHora"),
                        LocalDateTime.of(dataInicio, LocalTime.MIN)
                ));
            }

            if (dataFim != null) {
                filtros.add(builder.lessThanOrEqualTo(
                        root.get("dataHora"),
                        LocalDateTime.of(dataFim, LocalTime.MAX)
                ));
            }

            if (organizadorId != null) {
                filtros.add(builder.equal(root.get("organizador").get("id"), organizadorId));
            }

            return builder.and(filtros.toArray(new Predicate[0]));
        };
    }

    private void cancelarReservasDoEvento(Evento evento) {
        List<Reserva> reservas = reservaRepository.findByEvento_Id(evento.getId());

        for (Reserva reserva : reservas) {
            if (reserva.getStatus() == StatusReserva.CANCELADA || reserva.getStatus() == StatusReserva.RECUSADA || reserva.getStatus() == StatusReserva.EXPIRADA) {
                continue;
            }

            reserva.cancelar();
            liberarEstoqueDaReserva(reserva);
        }
    }

    private void cancelarIngressosDoEvento(Evento evento) {
        List<Ingresso> ingressos = ingressoRepository.findByReserva_Evento_Id(evento.getId());

        for (Ingresso ingresso : ingressos) {
            ingresso.cancelar();
        }
    }

    private void liberarEstoqueDaReserva(Reserva reserva) {
        if (reserva.getEvento().possuiAssentos()) {
            assentoRepository.liberarAssentosDaReserva(
                    reserva.getId(),
                    StatusAssento.DISPONIVEL
            );
            return;
        }

        eventoRepository.liberarCapacidadeGeral(
                reserva.getEvento().getId(),
                reserva.getQuantidade()
        );
    }
}
