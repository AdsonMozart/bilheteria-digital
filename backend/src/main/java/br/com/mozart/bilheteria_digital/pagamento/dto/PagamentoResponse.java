package br.com.mozart.bilheteria_digital.pagamento.dto;

import br.com.mozart.bilheteria_digital.pagamento.domain.Pagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagamentoResponse(
        Long id,
        Long reservaId,
        String status,
        BigDecimal valor,
        LocalDateTime dataCriacao
) {

    public static PagamentoResponse from(Pagamento pagamento) {
        return new PagamentoResponse(
                pagamento.getId(),
                pagamento.getReserva().getId(),
                pagamento.getStatus().name(),
                pagamento.getValor(),
                pagamento.getDataCriacao()
        );
    }
}
