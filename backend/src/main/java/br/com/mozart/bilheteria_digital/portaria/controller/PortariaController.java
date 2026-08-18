package br.com.mozart.bilheteria_digital.portaria.controller;

import br.com.mozart.bilheteria_digital.portaria.dto.ValidacaoIngressoResponse;
import br.com.mozart.bilheteria_digital.portaria.dto.ValidarIngressoRequest;
import br.com.mozart.bilheteria_digital.portaria.service.PortariaService;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portaria")
public class PortariaController {

    private final PortariaService portariaService;

    public PortariaController(PortariaService portariaService) {
        this.portariaService = portariaService;
    }

    @PostMapping("/validar")
    public ResponseEntity<ValidacaoIngressoResponse> validar(@AuthenticationPrincipal Usuario usuarioPortaria, @RequestBody ValidarIngressoRequest request) {
        return ResponseEntity.ok(portariaService.validarIngresso(usuarioPortaria, request));
    }
}
