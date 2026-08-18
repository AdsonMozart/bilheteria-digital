package br.com.mozart.bilheteria_digital.reserva.repository;

import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import br.com.mozart.bilheteria_digital.reserva.domain.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByCliente_Id(Long clienteId);

    List<Reserva> findByEvento_Id(Long eventoId);

    List<Reserva> findByStatus(StatusReserva status);

}
