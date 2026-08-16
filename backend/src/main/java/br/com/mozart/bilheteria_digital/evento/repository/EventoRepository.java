package br.com.mozart.bilheteria_digital.evento.repository;

import br.com.mozart.bilheteria_digital.evento.domain.Evento;
import br.com.mozart.bilheteria_digital.evento.domain.StatusEvento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByStatus(StatusEvento status);

    List<Evento> findByOrganizadorId(Long organizadorId);

}
