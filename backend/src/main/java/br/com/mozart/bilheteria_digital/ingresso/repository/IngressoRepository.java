package br.com.mozart.bilheteria_digital.ingresso.repository;

import br.com.mozart.bilheteria_digital.ingresso.domain.Ingresso;
import br.com.mozart.bilheteria_digital.ingresso.domain.StatusIngresso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IngressoRepository extends JpaRepository<Ingresso, Long> {

    List<Ingresso> findByReserva_Id(Long reservaId);

    List<Ingresso> findByReserva_Cliente_Id(Long clientId);

    Optional<Ingresso> findByCodigo(String codigo);

    Optional<Ingresso> findByTokenCompartilhamento(String tokenCompartilamento);

    List<Ingresso> findByStatus(StatusIngresso status);

    @Modifying
    @Query("""
            UPDATE Ingresso i
            SET i.status = :statusUsado,
                i.validadoEm = :validadoEm,
                i.validadoPor = :usuarioPortaria
            WHERE i.id = :ingressoId
              AND i.status = :statusValido
            """)
    int validarIngresso(
            @Param("ingressoId") Long ingressoId,
            @Param("usuarioPortaria") br.com.mozart.bilheteria_digital.usuario.domain.Usuario usuarioPortaria,
            @Param("validadoEm") LocalDateTime validadoEm,
            @Param("statusValido") StatusIngresso statusValido,
            @Param("statusUsado") StatusIngresso statusUsado
    );
}
