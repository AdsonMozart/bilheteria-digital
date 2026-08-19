package br.com.mozart.bilheteria_digital.pagamento.controller;

import br.com.mozart.bilheteria_digital.pagamento.dto.CriarPaymentIntentResponse;
import br.com.mozart.bilheteria_digital.pagamento.dto.PagamentoResponse;
import br.com.mozart.bilheteria_digital.pagamento.service.PagamentoService;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/reservas/{reservaId}")
    public ResponseEntity<PagamentoResponse> criarPagamento(@AuthenticationPrincipal Usuario cliente, @PathVariable Long reservaId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoService.criarPagamento(cliente, reservaId));
    }

    @PostMapping("/reservas/{reservaId}/payment-intent")
    public ResponseEntity<CriarPaymentIntentResponse> criarPaymentIntent(
            @AuthenticationPrincipal Usuario cliente,
            @PathVariable Long reservaId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagamentoService.criarPaymentIntent(cliente, reservaId));
    }

    @PostMapping("/{pagamentoId}/aprovar")
    public ResponseEntity<PagamentoResponse> aprovar(@AuthenticationPrincipal Usuario cliente, @PathVariable Long pagamentoId) {
        return ResponseEntity.ok(pagamentoService.aprovarPagamento(cliente, pagamentoId));
    }

    @PostMapping("/{pagamentoId}/recusar")
    public ResponseEntity<PagamentoResponse> recusar(@AuthenticationPrincipal Usuario cliente, @PathVariable Long pagamentoId) {
        return ResponseEntity.ok(pagamentoService.recusarPagamento(cliente, pagamentoId));
    }
}
