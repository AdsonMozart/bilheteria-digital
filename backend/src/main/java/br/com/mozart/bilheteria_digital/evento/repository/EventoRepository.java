package br.com.mozart.bilheteria_digital.evento.repository;

import br.com.mozart.bilheteria_digital.evento.domain.Evento;
import br.com.mozart.bilheteria_digital.evento.domain.StatusEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByStatus(StatusEvento status);

    List<Evento> findByOrganizador_Id(Long organizadorId);

    @Modifying
    @Query("""
        UPDATE Evento e
        SET e.capacidadeVendida = e.capacidadeVendida + :quantidade
        WHERE e.id = :eventoId
          AND e.status = 'PUBLICADO'
          AND e.tipoCapacidade = 'GERAL'
          AND e.capacidadeVendida + :quantidade <= e.capacidade
        """)
    int reservarCapacidadeGeral(@Param("eventoId") Long eventoId, @Param("quantidade") Integer quantidade);

    @Modifying
    @Query("""
        UPDATE Evento e
        SET e.capacidadeVendida = e.capacidadeVendida - :quantidade
        WHERE e.id = :eventoId
          AND e.capacidadeVendida >= :quantidade
        """)
    int liberarCapacidadeGeral(
            @Param("eventoId") Long eventoId,
            @Param("quantidade") Integer quantidade
    );
}
