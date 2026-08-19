package br.com.mozart.bilheteria_digital.reserva.controller;

import br.com.mozart.bilheteria_digital.reserva.dto.CriarReservaRequest;
import br.com.mozart.bilheteria_digital.reserva.dto.ReservaResponse;
import br.com.mozart.bilheteria_digital.reserva.service.ReservaService;
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
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Reservas de ingressos por quantidade ou assento")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @Operation(summary = "Criar reserva")
    public ResponseEntity<ReservaResponse> criar(@AuthenticationPrincipal Usuario cliente, @Valid @RequestBody CriarReservaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.criarReserva(cliente, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar reserva do cliente logado")
    public ResponseEntity<ReservaResponse> buscar(
            @AuthenticationPrincipal Usuario cliente,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(reservaService.buscarReservaDoCliente(cliente, id));
    }

    @GetMapping("/minhas")
    @Operation(summary = "Listar reservas do cliente logado")
    public ResponseEntity<List<ReservaResponse>> minhasReservas(
            @AuthenticationPrincipal Usuario cliente
    ) {
        return ResponseEntity.ok(reservaService.listarReservasDoCliente(cliente));
    }
}

