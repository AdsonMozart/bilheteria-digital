package br.com.mozart.bilheteria_digital.pagamento.stripe;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class StripeService {

    private final StripePropriedades propriedades;

    public StripeService(StripePropriedades propriedades) {
        this.propriedades = propriedades;
    }

    private void validarSecretKey() {
        if (!StringUtils.hasText(propriedades.secretKey())) {
            throw new IllegalStateException("STRIPE_SECRET_KEY nao configurada");
        }

        if (!propriedades.secretKey().startsWith("sk_test_") && !propriedades.secretKey().startsWith("sk_live_")) {
            throw new IllegalStateException("STRIPE_SECRET_KEY invalida. Use uma chave secreta da Stripe iniciada por sk_test_ ou sk_live_");
        }
    }

    private Long converterParaCentavos(BigDecimal valor) {
        return valor.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    public PaymentIntent criarPaymentIntent(Long reservaId, BigDecimal valor) {
        validarSecretKey();

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(converterParaCentavos(valor))
                    .setCurrency("brl")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .putMetadata("reservaId", String.valueOf(reservaId))
                    .build();

            RequestOptions requestOptions = RequestOptions.builder()
                    .setApiKey(propriedades.secretKey())
                    .setIdempotencyKey("payment-intent-reserva-" + reservaId)
                    .build();

            return PaymentIntent.create(params, requestOptions);
        } catch (StripeException ex) {
            throw new IllegalStateException("Falha ao criar PaymentIntent na Stripe", ex);
        }
    }

    public PaymentIntent buscarPaymentIntent(String paymentIntentId) {
        validarSecretKey();

        try {
            RequestOptions requestOptions = RequestOptions.builder()
                    .setApiKey(propriedades.secretKey())
                    .build();

            return PaymentIntent.retrieve(paymentIntentId, requestOptions);
        } catch (StripeException ex) {
            throw new IllegalStateException("Falha ao consultar PaymentIntent na Stripe", ex);
        }
    }

    public Event construirEventoWebhook(String payload, String assinatura) {
        if (!StringUtils.hasText(propriedades.webhookSecret())) {
            throw new IllegalStateException("STRIPE_WEBHOOK_SECRET nao configurado");
        }

        try {
            return Webhook.constructEvent(
                    payload,
                    assinatura,
                    propriedades.webhookSecret()
            );
        } catch (SignatureVerificationException ex) {
            throw new IllegalArgumentException("Assinatura do webhook Stripe invalida", ex);
        }
    }
}
