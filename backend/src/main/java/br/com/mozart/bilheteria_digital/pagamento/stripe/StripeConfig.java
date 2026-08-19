package br.com.mozart.bilheteria_digital.pagamento.stripe;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StripePropriedades.class)
public class StripeConfig {
}
