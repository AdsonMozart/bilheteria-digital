package br.com.mozart.bilheteria_digital.pagamento.controller;

import br.com.mozart.bilheteria_digital.pagamento.service.PagamentoService;
import br.com.mozart.bilheteria_digital.pagamento.stripe.StripeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/stripe")
public class StripeWebhookController {

    private final StripeService stripeService;
    private final PagamentoService pagamentoService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StripeWebhookController(
            StripeService stripeService,
            PagamentoService pagamentoService
    ) {
        this.stripeService = stripeService;
        this.pagamentoService = pagamentoService;
    }

    @PostMapping
    public ResponseEntity<String> receber(@RequestBody String payload, @RequestHeader("Stripe-Signature") String assinatura) {
        Event evento = stripeService.construirEventoWebhook(payload, assinatura);

        if ("payment_intent.succeeded".equals(evento.getType())) {
            pagamentoService.aprovarPagamentoStripe(extrairPaymentIntentId(evento));
        }

        if ("payment_intent.payment_failed".equals(evento.getType())) {
            pagamentoService.recusarPagamentoStripe(extrairPaymentIntentId(evento));
        }

        return ResponseEntity.ok("Webhook Stripe processado");
    }

    private String extrairPaymentIntentId(Event evento) {
        return evento.getDataObjectDeserializer()
                .getObject()
                .filter(PaymentIntent.class::isInstance)
                .map(PaymentIntent.class::cast)
                .map(PaymentIntent::getId)
                .orElseGet(() -> extrairPaymentIntentIdDoJson(evento));
    }

    private String extrairPaymentIntentIdDoJson(Event evento) {
        try {
            String id = objectMapper.readTree(evento.getDataObjectDeserializer().getRawJson())
                    .path("id")
                    .asText(null);

            if (id == null) {
                throw new IllegalArgumentException("Evento Stripe nao contem um PaymentIntent valido");
            }

            return id;
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Evento Stripe nao contem um PaymentIntent valido", ex);
        }
    }
}
