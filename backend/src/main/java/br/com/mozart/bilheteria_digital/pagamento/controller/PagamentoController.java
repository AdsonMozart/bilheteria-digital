package br.com.mozart.bilheteria_digital.pagamento.controller;

import br.com.mozart.bilheteria_digital.pagamento.dto.CriarPaymentIntentResponse;
import br.com.mozart.bilheteria_digital.pagamento.dto.PagamentoResponse;
import br.com.mozart.bilheteria_digital.pagamento.service.PagamentoService;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagamentos")
@Tag(name = "Pagamentos", description = "Criacao e confirmacao de pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/reservas/{reservaId}")
    @Operation(summary = "Criar pagamento para reserva")
    public ResponseEntity<PagamentoResponse> criarPagamento(@AuthenticationPrincipal Usuario cliente, @PathVariable Long reservaId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoService.criarPagamento(cliente, reservaId));
    }

    @PostMapping("/reservas/{reservaId}/payment-intent")
    @Operation(summary = "Criar PaymentIntent Stripe")
    public ResponseEntity<CriarPaymentIntentResponse> criarPaymentIntent(
            @AuthenticationPrincipal Usuario cliente,
            @PathVariable Long reservaId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoService.criarPaymentIntent(cliente, reservaId));
    }

    @PostMapping("/{pagamentoId}/aprovar")
    @Operation(summary = "Aprovar pagamento manualmente")
    public ResponseEntity<PagamentoResponse> aprovar(@AuthenticationPrincipal Usuario cliente, @PathVariable Long pagamentoId) {
        return ResponseEntity.ok(pagamentoService.aprovarPagamento(cliente, pagamentoId));
    }

    @PostMapping("/{pagamentoId}/recusar")
    @Operation(summary = "Recusar pagamento manualmente")
    public ResponseEntity<PagamentoResponse> recusar(@AuthenticationPrincipal Usuario cliente, @PathVariable Long pagamentoId) {
        return ResponseEntity.ok(pagamentoService.recusarPagamento(cliente, pagamentoId));
    }
}
