package br.com.mozart.bilheteria_digital.pagamento.stripe;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.stripe")
public record StripePropriedades(String secretKey, String webhookSecret) {
}
