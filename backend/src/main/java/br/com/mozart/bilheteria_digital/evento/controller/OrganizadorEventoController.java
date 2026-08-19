package br.com.mozart.bilheteria_digital.evento.controller;

import br.com.mozart.bilheteria_digital.evento.dto.CriarEventoRequest;
import br.com.mozart.bilheteria_digital.evento.dto.EventoResponse;
import br.com.mozart.bilheteria_digital.evento.service.EventoService;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizador/eventos")
@Tag(name = "Organizador", description = "Criacao e gerenciamento de eventos")
public class OrganizadorEventoController {

    private final EventoService eventoService;

    public OrganizadorEventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @PostMapping
    @Operation(summary = "Criar evento")
    public ResponseEntity<EventoResponse> criar(@AuthenticationPrincipal Usuario organizador, @Valid @RequestBody CriarEventoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventoService.criarEvento(organizador, request));
    }

    @GetMapping
    @Operation(summary = "Listar eventos do organizador logado")
    public ResponseEntity<List<EventoResponse>> listarMeusEventos(@AuthenticationPrincipal Usuario organizador) {
        return ResponseEntity.ok(eventoService.listarEventosDoOrganizador(organizador));
    }

    @PostMapping("/{id}/publicar")
    @Operation(summary = "Publicar evento")
    public ResponseEntity<EventoResponse> publicar(@AuthenticationPrincipal Usuario organizador, @PathVariable Long id) {
        return ResponseEntity.ok(eventoService.publicarEvento(organizador, id));
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar evento")
    public ResponseEntity<EventoResponse> cancelar(@AuthenticationPrincipal Usuario organizador, @PathVariable Long id) {
        return ResponseEntity.ok(eventoService.cancelarEvento(organizador, id));
    }

}
