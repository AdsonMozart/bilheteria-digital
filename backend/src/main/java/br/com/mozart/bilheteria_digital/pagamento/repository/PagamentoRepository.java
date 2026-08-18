package br.com.mozart.bilheteria_digital.pagamento.repository;

import br.com.mozart.bilheteria_digital.pagamento.domain.Pagamento;
import br.com.mozart.bilheteria_digital.pagamento.domain.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    Optional<Pagamento> findByReserva_Id(Long reservaId);

    Optional<Pagamento> findByPagamentoStripeId(String pagamentoStripeId);

    List<Pagamento> findByStatus(StatusPagamento status);

}
