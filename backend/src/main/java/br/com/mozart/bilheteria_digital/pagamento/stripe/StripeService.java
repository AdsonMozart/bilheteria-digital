package br.com.mozart.bilheteria_digital.pagamento.stripe;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
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
    }

    private Long converterParaCentavos(BigDecimal valor) {
        return valor.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }

    public PaymentIntent criarPaymentIntent(Long reservaId, BigDecimal valor) {
        validarSecretKey();

        Stripe.apiKey =  propriedades.secretKey();

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(converterParaCentavos(valor))
                    .setCurrency("brl")
                    .putMetadata("reservaId", String.valueOf(reservaId))
                    .build();

            return PaymentIntent.create(params);
        } catch (StripeException ex) {
            throw new IllegalStateException("Falha ao criar PaymentIntent na Stripe", ex);
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
