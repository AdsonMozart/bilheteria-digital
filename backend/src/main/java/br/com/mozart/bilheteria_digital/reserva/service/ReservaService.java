package br.com.mozart.bilheteria_digital.reserva.service;

import br.com.mozart.bilheteria_digital.evento.domain.Evento;
import br.com.mozart.bilheteria_digital.evento.repository.EventoRepository;
import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import br.com.mozart.bilheteria_digital.reserva.dto.CriarReservaRequest;
import br.com.mozart.bilheteria_digital.reserva.dto.ReservaResponse;
import br.com.mozart.bilheteria_digital.reserva.repository.ReservaRepository;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final EventoRepository eventoRepository;

    public ReservaService(
            ReservaRepository reservaRepository,
            EventoRepository eventoRepository
    ) {
        this.reservaRepository = reservaRepository;
        this.eventoRepository = eventoRepository;
    }

    @Transactional
    public ReservaResponse criarReservaGeral(Usuario cliente, CriarReservaRequest request) {
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
                evento.getPreco().multiply(java.math.BigDecimal.valueOf(request.quantidade())),
                LocalDateTime.now().plusMinutes(15)
        );

        Reserva reservaSalva = reservaRepository.save(reserva);
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
        return reservaRepository.findByClienteId(cliente.getId())
                .stream()
                .map(ReservaResponse::from)
                .toList();
    }
}
