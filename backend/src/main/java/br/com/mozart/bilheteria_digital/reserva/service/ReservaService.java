package br.com.mozart.bilheteria_digital.reserva.service;

import br.com.mozart.bilheteria_digital.assento.domain.Assento;
import br.com.mozart.bilheteria_digital.assento.domain.StatusAssento;
import br.com.mozart.bilheteria_digital.assento.repository.AssentoRepository;
import br.com.mozart.bilheteria_digital.evento.domain.Evento;
import br.com.mozart.bilheteria_digital.evento.repository.EventoRepository;
import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import br.com.mozart.bilheteria_digital.reserva.dto.CriarReservaRequest;
import br.com.mozart.bilheteria_digital.reserva.dto.ReservaResponse;
import br.com.mozart.bilheteria_digital.reserva.repository.ReservaRepository;
import br.com.mozart.bilheteria_digital.reservaassento.domain.ReservaAssento;
import br.com.mozart.bilheteria_digital.reservaassento.repository.ReservaAssentoRepository;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final EventoRepository eventoRepository;
    private final AssentoRepository assentoRepository;
    private final ReservaAssentoRepository reservaAssentoRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            EventoRepository eventoRepository,
            AssentoRepository assentoRepository,
            ReservaAssentoRepository reservaAssentoRepository
    ) {
        this.reservaRepository = reservaRepository;
        this.eventoRepository = eventoRepository;
        this.assentoRepository = assentoRepository;
        this.reservaAssentoRepository = reservaAssentoRepository;
    }

    @Transactional
    public ReservaResponse criarReserva(Usuario cliente, CriarReservaRequest request) {
        if (request.assentoIds() != null && !request.assentoIds().isEmpty()) {
            return criarReservaComAssentos(cliente, request);
        }

        return criarReservaGeral(cliente, request);
    }

    private ReservaResponse criarReservaGeral(Usuario cliente, CriarReservaRequest request) {
        if (request.quantidade() == null || request.quantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }

        int linhasAfetadas = eventoRepository.reservarCapacidadeGeral(
                request.eventoId(),
                request.quantidade()
        );

        if (linhasAfetadas == 0) {
            throw new IllegalArgumentException("Evento indisponivel para essa quantidade");
        }

        Evento evento = eventoRepository.findById(request.eventoId())
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado"));

        Reserva reserva = new Reserva(
                evento,
                cliente,
                request.quantidade(),
                evento.getPreco().multiply(BigDecimal.valueOf(request.quantidade())),
                LocalDateTime.now().plusMinutes(15)
        );

        Reserva reservaSalva = reservaRepository.save(reserva);
        return ReservaResponse.from(reservaSalva);
    }

    private ReservaResponse criarReservaComAssentos(Usuario cliente, CriarReservaRequest request) {
        Evento evento = eventoRepository.findById(request.eventoId())
                .orElseThrow(() -> new IllegalArgumentException("Evento nao encontrado"));

        if (!evento.estaPublicado() || !evento.possuiAssentos()) {
            throw new IllegalArgumentException("Evento indisponivel para reserva de assentos");
        }

        long totalAssentosUnicos = request.assentoIds()
                .stream()
                .distinct()
                .count();

        if (totalAssentosUnicos != request.assentoIds().size()) {
            throw new IllegalArgumentException("Existem assentos duplicados na reserva");
        }

        Integer quantidade = request.assentoIds().size();

        Reserva reserva = new Reserva(
                evento,
                cliente,
                quantidade,
                evento.getPreco().multiply(BigDecimal.valueOf(quantidade)),
                LocalDateTime.now().plusMinutes(15)
        );

        Reserva reservaSalva = reservaRepository.save(reserva);

        for (Long assentoId : request.assentoIds()) {
            Assento assento = assentoRepository.findById(assentoId)
                    .orElseThrow(() -> new IllegalArgumentException("Assento nao encontrado"));

            if (!assento.getEvento().getId().equals(evento.getId())) {
                throw new IllegalArgumentException("Assento nao pertence ao evento informado");
            }

            int linhasAfetadas = assentoRepository.reservarAssento(
                    assentoId,
                    reservaSalva.getId(),
                    StatusAssento.DISPONIVEL,
                    StatusAssento.RESERVADO
            );

            if (linhasAfetadas == 0) {
                throw new IllegalArgumentException("Assento indisponivel");
            }

            reservaAssentoRepository.save(new ReservaAssento(reservaSalva, assento));
        }

        return ReservaResponse.from(reservaSalva);
    }

    public ReservaResponse buscarReservaDoCliente(Usuario cliente, Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva nao encontrada"));

        if (!reserva.getCliente().getId().equals(cliente.getId())) {
            throw new IllegalArgumentException("Reserva nao pertence ao cliente logado");
        }

        return ReservaResponse.from(reserva);
    }

    public List<ReservaResponse> listarReservasDoCliente(Usuario cliente) {
        return reservaRepository.findByCliente_Id(cliente.getId())
                .stream()
                .map(ReservaResponse::from)
                .toList();
    }
}
