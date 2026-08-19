package br.com.mozart.bilheteria_digital.pagamento.service;

import br.com.mozart.bilheteria_digital.assento.domain.StatusAssento;
import br.com.mozart.bilheteria_digital.assento.repository.AssentoRepository;
import br.com.mozart.bilheteria_digital.evento.repository.EventoRepository;
import br.com.mozart.bilheteria_digital.ingresso.service.IngressoService;
import br.com.mozart.bilheteria_digital.pagamento.domain.Pagamento;
import br.com.mozart.bilheteria_digital.pagamento.domain.StatusPagamento;
import br.com.mozart.bilheteria_digital.pagamento.dto.CriarPaymentIntentResponse;
import br.com.mozart.bilheteria_digital.pagamento.dto.PagamentoResponse;
import br.com.mozart.bilheteria_digital.pagamento.repository.PagamentoRepository;
import br.com.mozart.bilheteria_digital.pagamento.stripe.StripeService;
import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import br.com.mozart.bilheteria_digital.reserva.repository.ReservaRepository;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import com.stripe.model.PaymentIntent;
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
    public PagamentoResponse criarPagamento(Usuario cliente, Long reservaId) {
        Reserva reserva = buscarReservaDoCliente(cliente, reservaId);

        if(!reserva.estaPendente()) {
            throw new IllegalArgumentException("Reserva nao esta pendente");
        }

        pagamentoRepository.findByReserva_Id(reservaId).ifPresent(pagamento -> {
            throw new IllegalArgumentException("Pagamento ja existe para esta reserva");
        });

        Pagamento pagamento = new Pagamento(reserva, reserva.getValorTotal());

        return PagamentoResponse.from(pagamentoRepository.save(pagamento));
    }

    @Transactional
    public CriarPaymentIntentResponse criarPaymentIntent(Usuario cliente, Long reservaId) {
        Reserva reserva = buscarReservaDoCliente(cliente, reservaId);

        if (!reserva.estaPendente()) {
            throw new IllegalArgumentException("Reserva nao esta pendente");
        }

        Pagamento pagamento = pagamentoRepository.findByReserva_Id(reservaId)
                .orElseGet(() -> pagamentoRepository.save(new Pagamento(reserva, reserva.getValorTotal())));

        if (pagamento.getPagamentoStripeId() != null) {
            throw new IllegalArgumentException("PaymentIntent ja existe para esta reserva");
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
    public PagamentoResponse aprovarPagamento(Usuario cliente, Long pagamentoId) {
        Pagamento pagamento = buscarPagamentoDoCliente(cliente, pagamentoId);

        processarPagamentoAprovado(pagamento);

        return PagamentoResponse.from(pagamentoRepository.save(pagamento));
    }

    @Transactional
    public PagamentoResponse recusarPagamento(Usuario cliente, Long pagamentoId) {
        Pagamento pagamento = buscarPagamentoDoCliente(cliente, pagamentoId);

        processarPagamentoRecusado(pagamento);

        return PagamentoResponse.from(pagamentoRepository.save(pagamento));
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
        }

        ingressoService.gerarIngressosParaReserva(reserva);
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

    private Reserva buscarReservaDoCliente(Usuario cliente, Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva nao encontrada"));

        if (!reserva.pertenceAoCliente(cliente.getId())) {
            throw new IllegalArgumentException("Reserva nao pertence ao cliente logado");
        }

        return reserva;
    }

    @Transactional
    private Pagamento buscarPagamentoDoCliente(Usuario cliente, Long pagamentoId) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento nao encontrado"));

        if (!pagamento.getReserva().pertenceAoCliente(cliente.getId())) {
            throw new IllegalArgumentException("Pagamento nao pertence ao cliente logado");
        }

        return pagamento;
    }
}
