package br.com.mozart.bilheteria_digital.ingresso.controller;

import br.com.mozart.bilheteria_digital.ingresso.dto.IngressoCompartilhadoResponse;
import br.com.mozart.bilheteria_digital.ingresso.service.IngressoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingressos")
public class IngressoPublicoController {

    private final IngressoService ingressoService;

    public IngressoPublicoController(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }

    @GetMapping("/compartilhado/{tokenCompartilhamento}")
    public ResponseEntity<IngressoCompartilhadoResponse> buscarCompartilhado(
            @PathVariable String tokenCompartilhamento
    ) {
        return ResponseEntity.ok(
                ingressoService.buscarIngressoCompartilhado(tokenCompartilhamento)
        );
    }
}
