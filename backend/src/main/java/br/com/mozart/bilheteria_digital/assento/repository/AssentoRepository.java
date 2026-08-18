package br.com.mozart.bilheteria_digital.assento.repository;

import br.com.mozart.bilheteria_digital.assento.domain.Assento;
import br.com.mozart.bilheteria_digital.assento.domain.StatusAssento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssentoRepository extends JpaRepository<Assento, Long> {

    List<Assento> findByEvento_Id(Long eventoId);

    List<Assento> findByEvento_IdAndStatus(Long eventoId, StatusAssento status);
}
