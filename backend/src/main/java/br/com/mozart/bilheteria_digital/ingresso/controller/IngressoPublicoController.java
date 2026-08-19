package br.com.mozart.bilheteria_digital.ingresso.controller;

import br.com.mozart.bilheteria_digital.ingresso.dto.IngressoCompartilhadoResponse;
import br.com.mozart.bilheteria_digital.ingresso.service.IngressoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingressos")
@Tag(name = "Ingressos publicos", description = "Compartilhamento publico de ingresso")
public class IngressoPublicoController {

    private final IngressoService ingressoService;

    public IngressoPublicoController(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }

    @GetMapping("/compartilhado/{tokenCompartilhamento}")
    @Operation(summary = "Buscar ingresso compartilhado")
    public ResponseEntity<IngressoCompartilhadoResponse> buscarCompartilhado(
            @PathVariable String tokenCompartilhamento
    ) {
        return ResponseEntity.ok(
                ingressoService.buscarIngressoCompartilhado(tokenCompartilhamento)
        );
    }
}
