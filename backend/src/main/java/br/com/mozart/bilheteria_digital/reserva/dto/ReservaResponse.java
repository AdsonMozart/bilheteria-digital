package br.com.mozart.bilheteria_digital.reserva.dto;

import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservaResponse(
        Long id,
        Long eventoId,
        String tituloEvento,
        Long clienteId,
        Integer quantidade,
        BigDecimal valorTotal,
        String status,
        LocalDateTime validade,
        LocalDateTime dataCriacao
        ) {

    public static ReservaResponse from(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getEvento().getId(),
                reserva.getEvento().getTitulo(),
                reserva.getCliente().getId(),
                reserva.getQuantidade(),
                reserva.getValorTotal(),
                reserva.getStatus().name(),
                reserva.getValidade(),
                reserva.getDataCriacao()
        );
    }
}
