package br.com.mozart.bilheteria_digital.catalogo.tmdb;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TmdbPropriedades.class)
public class TmdbConfiguracao {
}
