package br.com.mozart.bilheteria_digital.pagamento;

import br.com.mozart.bilheteria_digital.assento.repository.AssentoRepository;
import br.com.mozart.bilheteria_digital.evento.domain.Evento;
import br.com.mozart.bilheteria_digital.evento.domain.OrigemExterna;
import br.com.mozart.bilheteria_digital.evento.domain.TipoCapacidade;
import br.com.mozart.bilheteria_digital.evento.domain.TipoEvento;
import br.com.mozart.bilheteria_digital.evento.repository.EventoRepository;
import br.com.mozart.bilheteria_digital.ingresso.service.IngressoService;
import br.com.mozart.bilheteria_digital.pagamento.domain.Pagamento;
import br.com.mozart.bilheteria_digital.pagamento.domain.StatusPagamento;
import br.com.mozart.bilheteria_digital.pagamento.dto.PagamentoResponse;
import br.com.mozart.bilheteria_digital.pagamento.repository.PagamentoRepository;
import br.com.mozart.bilheteria_digital.pagamento.service.PagamentoService;
import br.com.mozart.bilheteria_digital.pagamento.stripe.StripeService;
import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import br.com.mozart.bilheteria_digital.reserva.domain.StatusReserva;
import br.com.mozart.bilheteria_digital.reserva.repository.ReservaRepository;
import br.com.mozart.bilheteria_digital.usuario.domain.AcessoUsuario;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    private final PagamentoRepository pagamentoRepository = mock(PagamentoRepository.class);
    private final ReservaRepository reservaRepository = mock(ReservaRepository.class);
    private final EventoRepository eventoRepository = mock(EventoRepository.class);
    private final IngressoService ingressoService = mock(IngressoService.class);
    private final AssentoRepository assentoRepository = mock(AssentoRepository.class);
    private final StripeService stripeService = mock(StripeService.class);
    private final PagamentoService pagamentoService = new PagamentoService(
            pagamentoRepository,
            reservaRepository,
            eventoRepository,
            ingressoService,
            assentoRepository,
            stripeService
    );

    @Test
    void deveAprovarPagamentoEGerarIngresso() {
        Usuario cliente = novoUsuario(1L);
        Reserva reserva = novaReserva(1L, cliente, TipoCapacidade.GERAL);
        Pagamento pagamento = novoPagamento(1L, reserva);

        when(pagamentoRepository.findById(pagamento.getId())).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(pagamento)).thenReturn(pagamento);

        PagamentoResponse response = pagamentoService.aprovarPagamento(cliente, pagamento.getId());

        assertThat(response.status()).isEqualTo(StatusPagamento.APROVADO.name());
        assertThat(reserva.getStatus()).isEqualTo(StatusReserva.PAGA);
        verify(ingressoService).gerarIngressosParaReserva(reserva);
    }

    @Test
    void deveRecusarPagamentoEDevolverEstoque() {
        Usuario cliente = novoUsuario(1L);
        Reserva reserva = novaReserva(1L, cliente, TipoCapacidade.GERAL);
        Pagamento pagamento = novoPagamento(1L, reserva);

        when(pagamentoRepository.findById(pagamento.getId())).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(pagamento)).thenReturn(pagamento);

        PagamentoResponse response = pagamentoService.recusarPagamento(cliente, pagamento.getId());

        assertThat(response.status()).isEqualTo(StatusPagamento.RECUSADO.name());
        assertThat(reserva.getStatus()).isEqualTo(StatusReserva.RECUSADA);
        verify(eventoRepository).liberarCapacidadeGeral(reserva.getEvento().getId(), reserva.getQuantidade());
    }

    @Test
    void naoDeveReprocessarPagamentoJaAprovado() {
        Usuario cliente = novoUsuario(1L);
        Reserva reserva = novaReserva(1L, cliente, TipoCapacidade.GERAL);
        Pagamento pagamento = novoPagamento(1L, reserva);
        pagamento.aprovar();
        reserva.marcarComoPaga();

        when(pagamentoRepository.findById(pagamento.getId())).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(pagamento)).thenReturn(pagamento);

        PagamentoResponse response = pagamentoService.aprovarPagamento(cliente, pagamento.getId());

        assertThat(response.status()).isEqualTo(StatusPagamento.APROVADO.name());
        verify(ingressoService, never()).gerarIngressosParaReserva(reserva);
    }

    @Test
    void naoDeveAprovarPagamentoRecusado() {
        Usuario cliente = novoUsuario(1L);
        Reserva reserva = novaReserva(1L, cliente, TipoCapacidade.GERAL);
        Pagamento pagamento = novoPagamento(1L, reserva);
        pagamento.recusar();
        reserva.marcarComoRecusada();

        when(pagamentoRepository.findById(pagamento.getId())).thenReturn(Optional.of(pagamento));

        assertThatThrownBy(() -> pagamentoService.aprovarPagamento(cliente, pagamento.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Pagamento recusado nao pode ser aprovado");
    }

    @Test
    void naoDeveRecusarPagamentoJaRecusadoNemDevolverEstoqueDuasVezes() {
        Usuario cliente = novoUsuario(1L);
        Reserva reserva = novaReserva(1L, cliente, TipoCapacidade.GERAL);
        Pagamento pagamento = novoPagamento(1L, reserva);
        pagamento.recusar();
        reserva.marcarComoRecusada();

        when(pagamentoRepository.findById(pagamento.getId())).thenReturn(Optional.of(pagamento));
        when(pagamentoRepository.save(pagamento)).thenReturn(pagamento);

        PagamentoResponse response = pagamentoService.recusarPagamento(cliente, pagamento.getId());

        assertThat(response.status()).isEqualTo(StatusPagamento.RECUSADO.name());
        verify(eventoRepository, never()).liberarCapacidadeGeral(reserva.getEvento().getId(), reserva.getQuantidade());
    }

    private Usuario novoUsuario(Long id) {
        Usuario usuario = new Usuario("Cliente", "cliente" + id + "@teste.com", "senha", AcessoUsuario.CLIENTE);
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

    private Reserva novaReserva(Long id, Usuario cliente, TipoCapacidade tipoCapacidade) {
        Evento evento = new Evento(
                novoUsuario(99L),
                OrigemExterna.MANUAL,
                null,
                "Evento Teste",
                TipoEvento.SHOW,
                "Descricao",
                null,
                "Local",
                "Endereco",
                LocalDateTime.now().plusDays(5),
                tipoCapacidade,
                BigDecimal.valueOf(40),
                100
        );
        ReflectionTestUtils.setField(evento, "id", 1L);

        Reserva reserva = new Reserva(evento, cliente, 2, BigDecimal.valueOf(80), LocalDateTime.now().plusMinutes(15));
        ReflectionTestUtils.setField(reserva, "id", id);
        return reserva;
    }

    private Pagamento novoPagamento(Long id, Reserva reserva) {
        Pagamento pagamento = new Pagamento(reserva, reserva.getValorTotal());
        ReflectionTestUtils.setField(pagamento, "id", id);
        return pagamento;
    }
}
