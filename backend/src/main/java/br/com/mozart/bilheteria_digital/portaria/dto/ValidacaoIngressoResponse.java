package br.com.mozart.bilheteria_digital.portaria.dto;

public record ValidacaoIngressoResponse(
        ResultadoValidacao resultado,
        String mensagem,
        Long ingressoId,
        Long eventoId,
        String codigo
) {
}
