package br.com.mozart.bilheteria_digital.portaria;

import br.com.mozart.bilheteria_digital.auth.service.JwtService;
import br.com.mozart.bilheteria_digital.evento.domain.Evento;
import br.com.mozart.bilheteria_digital.evento.domain.OrigemExterna;
import br.com.mozart.bilheteria_digital.evento.domain.TipoCapacidade;
import br.com.mozart.bilheteria_digital.evento.domain.TipoEvento;
import br.com.mozart.bilheteria_digital.ingresso.domain.Ingresso;
import br.com.mozart.bilheteria_digital.ingresso.domain.StatusIngresso;
import br.com.mozart.bilheteria_digital.ingresso.repository.IngressoRepository;
import br.com.mozart.bilheteria_digital.portaria.dto.ResultadoValidacao;
import br.com.mozart.bilheteria_digital.portaria.dto.ValidacaoIngressoResponse;
import br.com.mozart.bilheteria_digital.portaria.dto.ValidarIngressoRequest;
import br.com.mozart.bilheteria_digital.portaria.service.PortariaService;
import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import br.com.mozart.bilheteria_digital.usuario.domain.AcessoUsuario;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortariaServiceTest {

    private static final String SEGREDO = "chave-local-com-pelo-menos-32-caracteres";

    private final IngressoRepository ingressoRepository = mock(IngressoRepository.class);
    private final JwtService jwtService = new JwtService(SEGREDO, 24);
    private final PortariaService portariaService = new PortariaService(ingressoRepository, jwtService);

    @Test
    void deveValidarIngressoValido() {
        Usuario portaria = novoUsuario(3L, AcessoUsuario.PORTARIA);
        Reserva reserva = novaReserva(1L, 10L);
        String codigo = "codigo-qr";
        String assinatura = gerarAssinatura(reserva.getId(), reserva.getEvento().getId(), codigo);
        Ingresso ingresso = novoIngresso(1L, reserva, codigo, assinatura);

        when(ingressoRepository.findByCodigo(codigo)).thenReturn(Optional.of(ingresso));
        when(ingressoRepository.validarIngresso(any(), any(), any(), any(), any())).thenReturn(1);

        ValidacaoIngressoResponse response = portariaService.validarIngresso(
                portaria,
                new ValidarIngressoRequest(reserva.getEvento().getId(), assinatura)
        );

        assertThat(response.resultado()).isEqualTo(ResultadoValidacao.VALIDO);
        assertThat(response.mensagem()).isEqualTo("Entrada liberada.");
    }

    @Test
    void deveValidarIngressoPagoPeloCodigoSimplesDoQrPublico() {
        Usuario portaria = novoUsuario(3L, AcessoUsuario.PORTARIA);
        Reserva reserva = novaReserva(1L, 10L);
        String codigo = "codigo-publico";
        String assinatura = gerarAssinatura(reserva.getId(), reserva.getEvento().getId(), codigo);
        Ingresso ingresso = novoIngresso(1L, reserva, codigo, assinatura);

        when(ingressoRepository.findByCodigo(codigo)).thenReturn(Optional.of(ingresso));
        when(ingressoRepository.validarIngresso(any(), any(), any(), any(), any())).thenReturn(1);

        ValidacaoIngressoResponse response = portariaService.validarIngresso(
                portaria,
                new ValidarIngressoRequest(reserva.getEvento().getId(), codigo)
        );

        assertThat(response.resultado()).isEqualTo(ResultadoValidacao.VALIDO);
        assertThat(response.mensagem()).isEqualTo("Entrada liberada.");
    }

    @Test
    void naoDeveLiberarEntradaDuasVezesParaMesmoIngresso() {
        Usuario portaria = novoUsuario(3L, AcessoUsuario.PORTARIA);
        Reserva reserva = novaReserva(1L, 10L);
        String codigo = "codigo-concorrente";
        String assinatura = gerarAssinatura(reserva.getId(), reserva.getEvento().getId(), codigo);
        Ingresso ingresso = novoIngresso(1L, reserva, codigo, assinatura);

        when(ingressoRepository.findByCodigo(codigo)).thenReturn(Optional.of(ingresso));
        when(ingressoRepository.validarIngresso(any(), any(), any(), any(), any())).thenReturn(0);

        ValidacaoIngressoResponse response = portariaService.validarIngresso(
                portaria,
                new ValidarIngressoRequest(reserva.getEvento().getId(), codigo)
        );

        assertThat(response.resultado()).isEqualTo(ResultadoValidacao.JA_UTILIZADO);
        assertThat(response.mensagem()).isEqualTo("Ingresso ja utilizado. Entrada nao liberada.");
    }

    @Test
    void deveRetornarJaUtilizadoQuandoIngressoNaoEstaValido() {
        Usuario portaria = novoUsuario(3L, AcessoUsuario.PORTARIA);
        Reserva reserva = novaReserva(1L, 10L);
        String codigo = "codigo-usado";
        String assinatura = gerarAssinatura(reserva.getId(), reserva.getEvento().getId(), codigo);
        Ingresso ingresso = novoIngresso(1L, reserva, codigo, assinatura);
        ingresso.marcarComoUsado(portaria, LocalDateTime.now());

        when(ingressoRepository.findByCodigo(codigo)).thenReturn(Optional.of(ingresso));

        ValidacaoIngressoResponse response = portariaService.validarIngresso(
                portaria,
                new ValidarIngressoRequest(reserva.getEvento().getId(), assinatura)
        );

        assertThat(response.resultado()).isEqualTo(ResultadoValidacao.JA_UTILIZADO);
    }

    @Test
    void deveRetornarEventoErradoQuandoIngressoForDeOutroEvento() {
        Usuario portaria = novoUsuario(3L, AcessoUsuario.PORTARIA);
        Reserva reserva = novaReserva(1L, 10L);
        String codigo = "codigo-evento-errado";
        String assinatura = gerarAssinatura(reserva.getId(), reserva.getEvento().getId(), codigo);
        Ingresso ingresso = novoIngresso(1L, reserva, codigo, assinatura);

        when(ingressoRepository.findByCodigo(codigo)).thenReturn(Optional.of(ingresso));

        ValidacaoIngressoResponse response = portariaService.validarIngresso(
                portaria,
                new ValidarIngressoRequest(99L, assinatura)
        );

        assertThat(response.resultado()).isEqualTo(ResultadoValidacao.EVENTO_ERRADO);
    }

    @Test
    void deveRetornarInvalidoQuandoCodigoNaoForJwtValido() {
        Usuario portaria = novoUsuario(3L, AcessoUsuario.PORTARIA);

        ValidacaoIngressoResponse response = portariaService.validarIngresso(
                portaria,
                new ValidarIngressoRequest(10L, "codigo-invalido")
        );

        assertThat(response.resultado()).isEqualTo(ResultadoValidacao.INVALIDO);
    }

    private String gerarAssinatura(Long reservaId, Long eventoId, String codigo) {
        SecretKey secretKey = Keys.hmacShaKeyFor(SEGREDO.getBytes(StandardCharsets.UTF_8));
        Instant agora = Instant.now();

        return Jwts.builder()
                .claim("rid", reservaId)
                .claim("eid", eventoId)
                .claim("jti", codigo)
                .issuedAt(Date.from(agora))
                .signWith(secretKey)
                .compact();
    }

    private Usuario novoUsuario(Long id, AcessoUsuario acessoUsuario) {
        Usuario usuario = new Usuario("Usuario", "usuario" + id + "@teste.com", "senha", acessoUsuario);
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

    private Reserva novaReserva(Long id, Long eventoId) {
        Evento evento = new Evento(
                novoUsuario(99L, AcessoUsuario.ORGANIZADOR),
                OrigemExterna.MANUAL,
                null,
                "Evento Teste",
                TipoEvento.SHOW,
                "Descricao",
                null,
                "Local",
                "Endereco",
                LocalDateTime.now().plusDays(5),
                TipoCapacidade.GERAL,
                BigDecimal.valueOf(40),
                100
        );
        ReflectionTestUtils.setField(evento, "id", eventoId);

        Reserva reserva = new Reserva(evento, novoUsuario(1L, AcessoUsuario.CLIENTE), 1, BigDecimal.valueOf(40), LocalDateTime.now().plusMinutes(15));
        ReflectionTestUtils.setField(reserva, "id", id);
        return reserva;
    }

    private Ingresso novoIngresso(Long id, Reserva reserva, String codigo, String assinatura) {
        Ingresso ingresso = new Ingresso(reserva, codigo, assinatura, "token-" + codigo);
        ReflectionTestUtils.setField(ingresso, "id", id);
        ReflectionTestUtils.setField(ingresso, "status", StatusIngresso.VALIDO);
        return ingresso;
    }
}
