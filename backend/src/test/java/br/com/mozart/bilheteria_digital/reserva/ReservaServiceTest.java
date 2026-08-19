package br.com.mozart.bilheteria_digital.reserva;

import br.com.mozart.bilheteria_digital.assento.domain.Assento;
import br.com.mozart.bilheteria_digital.assento.domain.StatusAssento;
import br.com.mozart.bilheteria_digital.assento.repository.AssentoRepository;
import br.com.mozart.bilheteria_digital.evento.domain.Evento;
import br.com.mozart.bilheteria_digital.evento.domain.OrigemExterna;
import br.com.mozart.bilheteria_digital.evento.domain.TipoCapacidade;
import br.com.mozart.bilheteria_digital.evento.domain.TipoEvento;
import br.com.mozart.bilheteria_digital.evento.repository.EventoRepository;
import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import br.com.mozart.bilheteria_digital.reserva.domain.StatusReserva;
import br.com.mozart.bilheteria_digital.reserva.dto.CriarReservaRequest;
import br.com.mozart.bilheteria_digital.reserva.dto.ReservaResponse;
import br.com.mozart.bilheteria_digital.reserva.repository.ReservaRepository;
import br.com.mozart.bilheteria_digital.reserva.service.ReservaService;
import br.com.mozart.bilheteria_digital.reservaassento.repository.ReservaAssentoRepository;
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
class ReservaServiceTest {

    private final ReservaRepository reservaRepository = mock(ReservaRepository.class);
    private final EventoRepository eventoRepository = mock(EventoRepository.class);
    private final AssentoRepository assentoRepository = mock(AssentoRepository.class);
    private final ReservaAssentoRepository reservaAssentoRepository = mock(ReservaAssentoRepository.class);
    private final ReservaService reservaService = new ReservaService(
            reservaRepository,
            eventoRepository,
            assentoRepository,
            reservaAssentoRepository
    );

    @Test
    void deveCriarReservaGeral() {
        Usuario cliente = novoUsuario(1L);
        Evento evento = novoEvento(1L, TipoCapacidade.GERAL);
        CriarReservaRequest request = new CriarReservaRequest(evento.getId(), 2, null);

        when(eventoRepository.reservarCapacidadeGeral(evento.getId(), request.quantidade())).thenReturn(1);
        when(eventoRepository.findById(evento.getId())).thenReturn(Optional.of(evento));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva reserva = invocation.getArgument(0);
            ReflectionTestUtils.setField(reserva, "id", 1L);
            return reserva;
        });

        ReservaResponse response = reservaService.criarReserva(cliente, request);

        assertThat(response.status()).isEqualTo(StatusReserva.PENDENTE.name());
        assertThat(response.quantidade()).isEqualTo(2);
        assertThat(response.valorTotal()).isEqualByComparingTo("80");
    }

    @Test
    void deveCriarReservaComAssento() {
        Usuario cliente = novoUsuario(1L);
        Evento evento = novoEvento(1L, TipoCapacidade.ASSENTOS);
        Assento assento = novoAssento(1L, evento);
        CriarReservaRequest request = new CriarReservaRequest(evento.getId(), null, List.of(assento.getId()));
        evento.publicar();

        when(eventoRepository.findById(evento.getId())).thenReturn(Optional.of(evento));
        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva reserva = invocation.getArgument(0);
            ReflectionTestUtils.setField(reserva, "id", 1L);
            return reserva;
        });
        when(assentoRepository.findById(assento.getId())).thenReturn(Optional.of(assento));
        when(assentoRepository.reservarAssento(assento.getId(), 1L, StatusAssento.DISPONIVEL, StatusAssento.RESERVADO)).thenReturn(1);

        ReservaResponse response = reservaService.criarReserva(cliente, request);

        assertThat(response.status()).isEqualTo(StatusReserva.PENDENTE.name());
        assertThat(response.quantidade()).isEqualTo(1);
        verify(reservaAssentoRepository).save(any());
    }

    @Test
    void deveExpirarReservaVencidaEDevolverEstoque() {
        Usuario cliente = novoUsuario(1L);
        Evento evento = novoEvento(1L, TipoCapacidade.GERAL);
        Reserva reserva = new Reserva(evento, cliente, 2, BigDecimal.valueOf(80), LocalDateTime.now().minusMinutes(1));
        ReflectionTestUtils.setField(reserva, "id", 1L);

        when(reservaRepository.findByStatusAndValidadeBefore(any(), any())).thenReturn(List.of(reserva));

        reservaService.expirarReservasVencidas();

        assertThat(reserva.getStatus()).isEqualTo(StatusReserva.EXPIRADA);
        verify(eventoRepository).liberarCapacidadeGeral(evento.getId(), reserva.getQuantidade());
    }

    private Usuario novoUsuario(Long id) {
        Usuario usuario = new Usuario("Cliente", "cliente" + id + "@teste.com", "senha", AcessoUsuario.CLIENTE);
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

    private Evento novoEvento(Long id, TipoCapacidade tipoCapacidade) {
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
        ReflectionTestUtils.setField(evento, "id", id);
        return evento;
    }

    private Assento novoAssento(Long id, Evento evento) {
        Assento assento = new Assento(evento, "UNICO", "A", 1);
        ReflectionTestUtils.setField(assento, "id", id);
        return assento;
    }
}
