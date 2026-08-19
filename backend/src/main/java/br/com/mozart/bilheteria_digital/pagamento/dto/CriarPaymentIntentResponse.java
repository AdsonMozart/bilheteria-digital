package br.com.mozart.bilheteria_digital.pagamento.dto;

public record CriarPaymentIntentResponse(
        Long pagamentoId,
        Long reservaId,
        String stripePaymentIntentId,
        String clientSecret
) {
}
