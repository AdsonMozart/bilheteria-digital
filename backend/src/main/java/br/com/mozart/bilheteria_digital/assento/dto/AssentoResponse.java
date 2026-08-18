package br.com.mozart.bilheteria_digital.assento.dto;

import br.com.mozart.bilheteria_digital.assento.domain.Assento;

public record AssentoResponse(
        Long id,
        String setor,
        String fileira,
        Integer numero,
        String status
) {
    public static AssentoResponse from(Assento assento) {
        return new AssentoResponse(
                assento.getId(),
                assento.getSetor(),
                assento.getFileira(),
                assento.getNumero(),
                assento.getStatus().name()
        );
    }
}
