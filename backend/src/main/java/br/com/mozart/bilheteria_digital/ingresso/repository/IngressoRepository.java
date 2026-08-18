package br.com.mozart.bilheteria_digital.ingresso.repository;

import br.com.mozart.bilheteria_digital.ingresso.domain.Ingresso;
import br.com.mozart.bilheteria_digital.ingresso.domain.StatusIngresso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IngressoRepository extends JpaRepository<Ingresso, Long> {

    List<Ingresso> findByReserva_Id(Long reservaId);

    List<Ingresso> findByReserva_Cliente_Id(Long clientId);

    Optional<Ingresso> findByCodigo(String codigo);

    Optional<Ingresso> findByTokenCompartilhamento(String tokenCompartilamento);

    List<Ingresso> findByStatus(StatusIngresso status);
}
