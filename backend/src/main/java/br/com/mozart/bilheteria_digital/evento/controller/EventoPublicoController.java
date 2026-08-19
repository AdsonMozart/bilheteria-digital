package br.com.mozart.bilheteria_digital.evento.controller;

import br.com.mozart.bilheteria_digital.evento.dto.EventoDetalheResponse;
import br.com.mozart.bilheteria_digital.evento.dto.EventoResponse;
import br.com.mozart.bilheteria_digital.evento.domain.TipoEvento;
import br.com.mozart.bilheteria_digital.evento.service.EventoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
@Tag(name = "Eventos publicos", description = "Consulta publica de eventos publicados")
public class EventoPublicoController {

    private final EventoService eventoService;

    public EventoPublicoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping
    @Operation(summary = "Listar eventos publicados")
    public ResponseEntity<List<EventoResponse>> listarEventosPublicados() {
        return ResponseEntity.ok(eventoService.listarEventosPublicados());
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar eventos publicados com filtros e paginacao")
    public ResponseEntity<Page<EventoResponse>> buscarEventosPublicados(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) TipoEvento tipo,
            @RequestParam(required = false) String local,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Long organizadorId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(eventoService.buscarEventosPublicados(
                titulo,
                tipo,
                local,
                dataInicio,
                dataFim,
                organizadorId,
                pageable
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhar evento publicado")
    public ResponseEntity<EventoDetalheResponse> detalharEventoPublicado(@PathVariable Long id) {
        return ResponseEntity.ok(eventoService.detalharEventoPublicado(id));
    }
}
