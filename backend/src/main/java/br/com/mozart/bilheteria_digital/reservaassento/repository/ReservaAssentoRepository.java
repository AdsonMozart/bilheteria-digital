package br.com.mozart.bilheteria_digital.reservaassento.repository;

import br.com.mozart.bilheteria_digital.reservaassento.domain.ReservaAssento;
import br.com.mozart.bilheteria_digital.reservaassento.domain.ReservaAssentoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaAssentoRepository extends JpaRepository<ReservaAssento, ReservaAssentoId> {

    List<ReservaAssento> findByReservaId(Long reservaId);
    List<ReservaAssento> findByAssentoId(Long assentoId);
}
