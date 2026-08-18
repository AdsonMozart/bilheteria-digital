package br.com.mozart.bilheteria_digital.reserva.controller;

import br.com.mozart.bilheteria_digital.reserva.dto.CriarReservaRequest;
import br.com.mozart.bilheteria_digital.reserva.dto.ReservaResponse;
import br.com.mozart.bilheteria_digital.reserva.service.ReservaService;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> criar(@AuthenticationPrincipal Usuario cliente, @RequestBody CriarReservaRequest request) {
        return ResponseEntity.ok(reservaService.criarReservaGeral(cliente, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponse> buscar(
            @AuthenticationPrincipal Usuario cliente,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(reservaService.buscarReservaDoCliente(cliente, id));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<ReservaResponse>> minhasReservas(
            @AuthenticationPrincipal Usuario cliente
    ) {
        return ResponseEntity.ok(reservaService.listarReservasDoCliente(cliente));
    }
}

