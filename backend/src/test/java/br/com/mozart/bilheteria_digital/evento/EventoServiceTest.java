package br.com.mozart.bilheteria_digital.evento;

import br.com.mozart.bilheteria_digital.assento.repository.AssentoRepository;
import br.com.mozart.bilheteria_digital.assento.service.AssentoService;
import br.com.mozart.bilheteria_digital.evento.domain.Evento;
import br.com.mozart.bilheteria_digital.evento.domain.OrigemExterna;
import br.com.mozart.bilheteria_digital.evento.domain.StatusEvento;
import br.com.mozart.bilheteria_digital.evento.domain.TipoCapacidade;
import br.com.mozart.bilheteria_digital.evento.domain.TipoEvento;
import br.com.mozart.bilheteria_digital.evento.dto.CriarEventoRequest;
import br.com.mozart.bilheteria_digital.evento.dto.EventoResponse;
import br.com.mozart.bilheteria_digital.evento.repository.EventoRepository;
import br.com.mozart.bilheteria_digital.evento.service.EventoService;
import br.com.mozart.bilheteria_digital.ingresso.domain.Ingresso;
import br.com.mozart.bilheteria_digital.ingresso.domain.StatusIngresso;
import br.com.mozart.bilheteria_digital.ingresso.repository.IngressoRepository;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventoServiceTest {

    private final EventoRepository eventoRepository = mock(EventoRepository.class);
    private final AssentoService assentoService = mock(AssentoService.class);
    private final ReservaRepository reservaRepository = mock(ReservaRepository.class);
    private final AssentoRepository assentoRepository = mock(AssentoRepository.class);
    private final IngressoRepository ingressoRepository = mock(IngressoRepository.class);
    private final EventoService eventoService = new EventoService(
            eventoRepository,
            assentoService,
            reservaRepository,
            assentoRepository,
            ingressoRepository
    );

    @Test
    void deveCriarEventoComoRascunho() {
        Usuario organizador = novoUsuario(1L, AcessoUsuario.ORGANIZADOR);
        CriarEventoRequest request = new CriarEventoRequest(
                OrigemExterna.MANUAL,
                null,
                "Show Teste",
                TipoEvento.SHOW,
                "Descricao",
                null,
                "Arena",
                "Rua 1",
                LocalDateTime.now().plusDays(10),
                TipoCapacidade.GERAL,
                BigDecimal.valueOf(50),
                100
        );

        when(eventoRepository.save(any(Evento.class))).thenAnswer(invocation -> {
            Evento evento = invocation.getArgument(0);
            ReflectionTestUtils.setField(evento, "id", 1L);
            return evento;
        });

        EventoResponse response = eventoService.criarEvento(organizador, request);

        assertThat(response.titulo()).isEqualTo("Show Teste");
        assertThat(response.status()).isEqualTo(StatusEvento.RASCUNHO.name());
    }

    @Test
    void devePublicarEventoEGerarAssentosQuandoNecessario() {
        Usuario organizador = novoUsuario(1L, AcessoUsuario.ORGANIZADOR);
        Evento evento = novoEvento(1L, organizador, TipoCapacidade.ASSENTOS);

        when(eventoRepository.findById(evento.getId())).thenReturn(Optional.of(evento));
        when(eventoRepository.save(evento)).thenReturn(evento);

        EventoResponse response = eventoService.publicarEvento(organizador, evento.getId());

        assertThat(response.status()).isEqualTo(StatusEvento.PUBLICADO.name());
        verify(assentoService).gerarAssentosParaEvento(evento);
    }

    @Test
    void deveCancelarEventoReservasIngressosEDevolverEstoque() {
        Usuario organizador = novoUsuario(1L, AcessoUsuario.ORGANIZADOR);
        Usuario cliente = novoUsuario(2L, AcessoUsuario.CLIENTE);
        Evento evento = novoEvento(1L, organizador, TipoCapacidade.GERAL);
        Reserva reserva = novaReserva(1L, evento, cliente);
        Ingresso ingresso = novoIngresso(1L, reserva);

        when(eventoRepository.findById(evento.getId())).thenReturn(Optional.of(evento));
        when(reservaRepository.findByEvento_Id(evento.getId())).thenReturn(List.of(reserva));
        when(ingressoRepository.findByReserva_Evento_Id(evento.getId())).thenReturn(List.of(ingresso));
        when(eventoRepository.save(evento)).thenReturn(evento);

        EventoResponse response = eventoService.cancelarEvento(organizador, evento.getId());

        assertThat(response.status()).isEqualTo(StatusEvento.CANCELADO.name());
        assertThat(reserva.getStatus()).isEqualTo(StatusReserva.CANCELADA);
        assertThat(ingresso.getStatus()).isEqualTo(StatusIngresso.CANCELADO);
        verify(eventoRepository).liberarCapacidadeGeral(evento.getId(), reserva.getQuantidade());
    }

    private Usuario novoUsuario(Long id, AcessoUsuario acessoUsuario) {
        Usuario usuario = new Usuario("Usuario", "usuario" + id + "@teste.com", "senha", acessoUsuario);
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

    private Evento novoEvento(Long id, Usuario organizador, TipoCapacidade tipoCapacidade) {
        Evento evento = new Evento(
                organizador,
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
        ReflectionTestUtils.setField(evento, "id", id);
        return evento;
    }

    private Reserva novaReserva(Long id, Evento evento, Usuario cliente) {
        Reserva reserva = new Reserva(evento, cliente, 2, BigDecimal.valueOf(80), LocalDateTime.now().plusMinutes(15));
        ReflectionTestUtils.setField(reserva, "id", id);
        return reserva;
    }

    private Ingresso novoIngresso(Long id, Reserva reserva) {
        Ingresso ingresso = new Ingresso(reserva, "codigo", "assinatura", "token");
        ReflectionTestUtils.setField(ingresso, "id", id);
        return ingresso;
    }
}
