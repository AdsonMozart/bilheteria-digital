package br.com.mozart.bilheteria_digital.assento.repository;

import br.com.mozart.bilheteria_digital.assento.domain.Assento;
import br.com.mozart.bilheteria_digital.assento.domain.StatusAssento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssentoRepository extends JpaRepository<Assento, Long> {

    List<Assento> findByEvento_Id(Long eventoId);

    List<Assento> findByEvento_IdAndStatus(Long eventoId, StatusAssento status);

    boolean existsByEvento_Id(Long eventoId);

    @Modifying
    @Query("""
            UPDATE Assento a
            SET a.status = :statusReservado,
                a.reservaId = :reservaId
            WHERE a.id = :assentoId
              AND a.status = :statusDisponivel
            """)
    int reservarAssento(
            @Param("assentoId") Long assentoId,
            @Param("reservaId") Long reservaId,
            @Param("statusDisponivel") StatusAssento statusDisponivel,
            @Param("statusReservado") StatusAssento statusReservado
    );

    @Modifying
    @Query("""
            UPDATE Assento a
            SET a.status = :statusDisponivel,
                a.reservaId = null
            WHERE a.reservaId = :reservaId
            """)
    int liberarAssentosDaReserva(
            @Param("reservaId") Long reservaId,
            @Param("statusDisponivel") StatusAssento statusDisponivel
    );

    @Modifying
    @Query("""
            UPDATE Assento a
            SET a.status = :statusVendido
            WHERE a.reservaId = :reservaId
              AND a.status = :statusReservado
            """)
    int venderAssentosDaReserva(
            @Param("reservaId") Long reservaId,
            @Param("statusReservado") StatusAssento statusReservado,
            @Param("statusVendido") StatusAssento statusVendido
    );
}
