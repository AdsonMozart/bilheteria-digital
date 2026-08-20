package br.com.mozart.bilheteria_digital.pagamento.service;

import br.com.mozart.bilheteria_digital.assento.domain.StatusAssento;
import br.com.mozart.bilheteria_digital.assento.repository.AssentoRepository;
import br.com.mozart.bilheteria_digital.evento.repository.EventoRepository;
import br.com.mozart.bilheteria_digital.ingresso.service.IngressoService;
import br.com.mozart.bilheteria_digital.pagamento.domain.Pagamento;
import br.com.mozart.bilheteria_digital.pagamento.domain.StatusPagamento;
import br.com.mozart.bilheteria_digital.pagamento.dto.CriarPaymentIntentResponse;
import br.com.mozart.bilheteria_digital.pagamento.repository.PagamentoRepository;
import br.com.mozart.bilheteria_digital.pagamento.stripe.StripeService;
import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import br.com.mozart.bilheteria_digital.reserva.repository.ReservaRepository;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import com.stripe.model.PaymentIntent;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final ReservaRepository reservaRepository;
    private final EventoRepository eventoRepository;
    private final IngressoService ingressoService;
    private final AssentoRepository assentoRepository;
    private final StripeService stripeService;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            ReservaRepository reservaRepository,
            EventoRepository eventoRepository,
            IngressoService ingressoService,
            AssentoRepository assentoRepository,
            StripeService stripeService
    ) {
        this.pagamentoRepository = pagamentoRepository;
        this.reservaRepository = reservaRepository;
        this.eventoRepository = eventoRepository;
        this.ingressoService = ingressoService;
        this.assentoRepository = assentoRepository;
        this.stripeService = stripeService;
    }

    @Transactional
    public CriarPaymentIntentResponse criarPaymentIntent(Usuario cliente, Long reservaId) {
        Reserva reserva = buscarReservaDoCliente(cliente, reservaId);

        if (!reserva.estaPendente()) {
            throw new IllegalArgumentException("Reserva nao esta pendente");
        }

        Pagamento pagamento = buscarOuCriarPagamento(reserva);

        if (pagamento.getPagamentoStripeId() != null) {
            PaymentIntent paymentIntent = stripeService.buscarPaymentIntent(pagamento.getPagamentoStripeId());

            return new CriarPaymentIntentResponse(
                    pagamento.getId(),
                    reserva.getId(),
                    paymentIntent.getId(),
                    paymentIntent.getClientSecret()
            );
        }

        PaymentIntent paymentIntent = stripeService.criarPaymentIntent(
                reserva.getId(),
                reserva.getValorTotal()
        );

        pagamento.vincularPagamentoStripe(paymentIntent.getId());
        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);

        return new CriarPaymentIntentResponse(
                pagamentoSalvo.getId(),
                reserva.getId(),
                paymentIntent.getId(),
                paymentIntent.getClientSecret()
        );
    }

    @Transactional
    public void aprovarPagamentoStripe(String pagamentoStripeId) {
        Pagamento pagamento = pagamentoRepository.findByPagamentoStripeId(pagamentoStripeId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento Stripe nao encontrado"));

        processarPagamentoAprovado(pagamento);

        pagamentoRepository.save(pagamento);
    }

    @Transactional
    public void recusarPagamentoStripe(String pagamentoStripeId) {
        Pagamento pagamento = pagamentoRepository.findByPagamentoStripeId(pagamentoStripeId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento Stripe nao encontrado"));

        processarPagamentoRecusado(pagamento);

        pagamentoRepository.save(pagamento);
    }

    private void processarPagamentoAprovado(Pagamento pagamento) {
        if (pagamento.getStatus() == StatusPagamento.APROVADO) {
            return;
        }

        if (pagamento.getStatus() == StatusPagamento.RECUSADO) {
            throw new IllegalStateException("Pagamento recusado nao pode ser aprovado");
        }

        Reserva reserva = pagamento.getReserva();

        if (!reserva.estaPendente()) {
            throw new IllegalStateException("Reserva nao esta pendente para aprovacao");
        }

        pagamento.aprovar();
        reserva.marcarComoPaga();

        if (reserva.getEvento().possuiAssentos()) {
            assentoRepository.venderAssentosDaReserva(
                    reserva.getId(),
                    StatusAssento.RESERVADO,
                    StatusAssento.VENDIDO
            );
            registrarIngressosVendidos(reserva);
        }

        ingressoService.gerarIngressosParaReserva(reserva);
    }

    private void registrarIngressosVendidos(Reserva reserva) {
        int linhasAfetadas = eventoRepository.registrarIngressosVendidos(
                reserva.getEvento().getId(),
                reserva.getQuantidade()
        );

        if (linhasAfetadas == 0) {
            throw new IllegalStateException("Capacidade indisponivel para registrar ingressos vendidos");
        }
    }

    private void processarPagamentoRecusado(Pagamento pagamento) {
        if (pagamento.getStatus() == StatusPagamento.RECUSADO) {
            return;
        }

        Reserva reserva = pagamento.getReserva();

        if (!reserva.estaPendente()) {
            throw new IllegalStateException("Reserva nao esta pendente para recusa");
        }

        pagamento.recusar();
        reserva.marcarComoRecusada();
        liberarEstoqueDaReserva(reserva);
    }

    private void liberarEstoqueDaReserva(Reserva reserva) {
        if (reserva.getEvento().possuiAssentos()) {
            assentoRepository.liberarAssentosDaReserva(
                    reserva.getId(),
                    StatusAssento.DISPONIVEL
            );
        } else {
            eventoRepository.liberarCapacidadeGeral(
                    reserva.getEvento().getId(),
                    reserva.getQuantidade()
            );
        }
    }

    private Pagamento buscarOuCriarPagamento(Reserva reserva) {
        return pagamentoRepository.findByReserva_Id(reserva.getId())
                .orElseGet(() -> salvarNovoPagamentoOuBuscarExistente(reserva));
    }

    private Pagamento salvarNovoPagamentoOuBuscarExistente(Reserva reserva) {
        try {
            return pagamentoRepository.save(new Pagamento(reserva, reserva.getValorTotal()));
        } catch (DataIntegrityViolationException ex) {
            return pagamentoRepository.findByReserva_Id(reserva.getId())
                    .orElseThrow(() -> ex);
        }
    }

    private Reserva buscarReservaDoCliente(Usuario cliente, Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva nao encontrada"));

        if (!reserva.pertenceAoCliente(cliente.getId())) {
            throw new IllegalArgumentException("Reserva nao pertence ao cliente logado");
        }

        return reserva;
    }
}
