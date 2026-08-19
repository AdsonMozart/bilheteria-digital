package br.com.mozart.bilheteria_digital.evento.controller;

import br.com.mozart.bilheteria_digital.evento.dto.CriarEventoRequest;
import br.com.mozart.bilheteria_digital.evento.dto.EventoResponse;
import br.com.mozart.bilheteria_digital.evento.service.EventoService;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizador/eventos")
public class OrganizadorEventoController {

    private final EventoService eventoService;

    public OrganizadorEventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    public ResponseEntity<EventoResponse> criar(@AuthenticationPrincipal Usuario organizador, @RequestBody CriarEventoRequest request) {
        return ResponseEntity.ok(eventoService.criarEvento(organizador, request));
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> listarMeusEventos(@AuthenticationPrincipal Usuario organizador) {
        return ResponseEntity.ok(eventoService.listarEventosDoOrganizador(organizador));
    }

    @PostMapping("/{id}/publicar")
    public ResponseEntity<EventoResponse> publicar(@AuthenticationPrincipal Usuario organizador, @PathVariable Long id) {
        return ResponseEntity.ok(eventoService.publicarEvento(organizador, id));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<EventoResponse> cancelar(@AuthenticationPrincipal Usuario organizador, @PathVariable Long id) {
        return ResponseEntity.ok(eventoService.cancelarEvento(organizador, id));
    }

}
