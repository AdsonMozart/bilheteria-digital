package br.com.mozart.bilheteria_digital.reserva.repository;

import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import br.com.mozart.bilheteria_digital.reserva.domain.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByClienteId(Long clienteId);

    List<Reserva> findByEventoId(Long eventoId);

    List<Reserva> findByStatus(StatusReserva status);

}
