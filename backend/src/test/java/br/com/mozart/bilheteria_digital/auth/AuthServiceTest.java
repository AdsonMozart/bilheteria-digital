package br.com.mozart.bilheteria_digital.auth;

import br.com.mozart.bilheteria_digital.auth.dto.AuthResponse;
import br.com.mozart.bilheteria_digital.auth.dto.LoginRequest;
import br.com.mozart.bilheteria_digital.auth.dto.RegistroRequest;
import br.com.mozart.bilheteria_digital.auth.service.AuthService;
import br.com.mozart.bilheteria_digital.auth.service.JwtService;
import br.com.mozart.bilheteria_digital.common.security.JwtAuthFilter;
import br.com.mozart.bilheteria_digital.usuario.domain.AcessoUsuario;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import br.com.mozart.bilheteria_digital.usuario.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final AuthService authService = new AuthService(usuarioRepository, passwordEncoder, jwtService);

    @AfterEach
    void limparContextoDeSeguranca() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveCadastrarCliente() {
        RegistroRequest request = new RegistroRequest("Cliente", "cliente@teste.com", "123456");

        when(usuarioRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.senha())).thenReturn("senha-hash");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            ReflectionTestUtils.setField(usuario, "id", 1L);
            return usuario;
        });
        when(jwtService.gerarToken(any(Usuario.class))).thenReturn("token");

        AuthResponse response = authService.registrar(request);

        assertThat(response.token()).isEqualTo("token");
    }

    @Test
    void deveFazerLoginComCredenciaisValidas() {
        LoginRequest request = new LoginRequest("cliente@teste.com", "123456");
        Usuario usuario = novoUsuario(1L, "Cliente", request.email(), "senha-hash", AcessoUsuario.CLIENTE);

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(request.senha(), usuario.getSenhaHash())).thenReturn(true);
        when(jwtService.gerarToken(usuario)).thenReturn("token-login");

        AuthResponse response = authService.login(request);

        assertThat(response.token()).isEqualTo("token-login");
    }

    @Test
    void deveRecusarLoginComSenhaInvalida() {
        LoginRequest request = new LoginRequest("cliente@teste.com", "errada");
        Usuario usuario = novoUsuario(1L, "Cliente", request.email(), "senha-hash", AcessoUsuario.CLIENTE);

        when(usuarioRepository.findByEmail(request.email())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(request.senha(), usuario.getSenhaHash())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Email ou senha invalidos");
    }

    @Test
    void deveMontarPermissaoDeAcordoComPerfilDoUsuario() throws Exception {
        JwtAuthFilter filtro = new JwtAuthFilter(jwtService, usuarioRepository);
        Claims claims = mock(Claims.class);
        Usuario organizador = novoUsuario(1L, "Organizador", "organizador@teste.com", "senha-hash", AcessoUsuario.ORGANIZADOR);
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer token-valido");
        when(jwtService.extrairClaims("token-valido")).thenReturn(claims);
        when(claims.getSubject()).thenReturn(organizador.getEmail());
        when(usuarioRepository.findByEmail(organizador.getEmail())).thenReturn(Optional.of(organizador));

        filtro.doFilter(request, response, new MockFilterChain());

        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_ORGANIZADOR");
    }

    private Usuario novoUsuario(Long id, String nome, String email, String senhaHash, AcessoUsuario acessoUsuario) {
        Usuario usuario = new Usuario(nome, email, senhaHash, acessoUsuario);
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }
}
