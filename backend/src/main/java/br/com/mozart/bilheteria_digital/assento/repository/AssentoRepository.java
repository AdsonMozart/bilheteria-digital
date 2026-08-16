package br.com.mozart.bilheteria_digital.assento.repository;

import br.com.mozart.bilheteria_digital.assento.domain.Assento;
import br.com.mozart.bilheteria_digital.assento.domain.StatusAssento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssentoRepository extends JpaRepository<Assento, Long> {

    List<Assento> findByEventoId(Long eventoId);

    List<Assento> findByEventoIdAndStatus(Long eventoId, StatusAssento status);
}
