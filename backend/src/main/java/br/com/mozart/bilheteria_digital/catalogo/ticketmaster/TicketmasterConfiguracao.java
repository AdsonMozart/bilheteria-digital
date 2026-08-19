package br.com.mozart.bilheteria_digital.catalogo.ticketmaster;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TicketmasterPropriedades.class)
public class TicketmasterConfiguracao {
}
