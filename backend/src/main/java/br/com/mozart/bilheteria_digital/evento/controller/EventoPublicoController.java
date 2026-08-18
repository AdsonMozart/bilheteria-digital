package br.com.mozart.bilheteria_digital.evento.controller;

import br.com.mozart.bilheteria_digital.evento.dto.EventoDetalheResponse;
import br.com.mozart.bilheteria_digital.evento.dto.EventoResponse;
import br.com.mozart.bilheteria_digital.evento.service.EventoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoPublicoController {

    private final EventoService eventoService;

    public EventoPublicoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    public ResponseEntity<List<EventoResponse>> listarEventosPublicados() {
        return ResponseEntity.ok(eventoService.listarEventosPublicados());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventoDetalheResponse> detalharEventoPublicado(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.detalharEventoPublicado(id));
    }
}
