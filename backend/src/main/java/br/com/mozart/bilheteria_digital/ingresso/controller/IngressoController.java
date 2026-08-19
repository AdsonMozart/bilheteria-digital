package br.com.mozart.bilheteria_digital.ingresso.controller;

import br.com.mozart.bilheteria_digital.ingresso.dto.IngressoResponse;
import br.com.mozart.bilheteria_digital.ingresso.service.IngressoService;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/me/ingressos")
@Tag(name = "Ingressos", description = "Ingressos do cliente logado")
public class IngressoController {

    private final IngressoService ingressoService;

    public IngressoController(IngressoService ingressoService) {
        this.ingressoService = ingressoService;
    }

    @GetMapping
    @Operation(summary = "Listar ingressos do cliente logado")
    public ResponseEntity<List<IngressoResponse>> listarMeusIngressos(
            @AuthenticationPrincipal Usuario cliente
    ) {
        return ResponseEntity.ok(ingressoService.listarIngressosDoCliente(cliente));
    }
}
