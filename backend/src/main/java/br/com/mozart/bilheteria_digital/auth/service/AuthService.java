package br.com.mozart.bilheteria_digital.auth.service;

import br.com.mozart.bilheteria_digital.auth.dto.AuthResponse;
import br.com.mozart.bilheteria_digital.auth.dto.LoginRequest;
import br.com.mozart.bilheteria_digital.auth.dto.RegistroRequest;
import br.com.mozart.bilheteria_digital.usuario.domain.AcessoUsuario;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import br.com.mozart.bilheteria_digital.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse registrar(RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email ja cadastrado");
        }

        Usuario usuario = new Usuario(
                request.nome(),
                request.email(),
                passwordEncoder.encode(request.senha()),
                AcessoUsuario.CLIENTE
        );

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        String token = jwtService.gerarToken(usuarioSalvo);

        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Email ou senha invalidos"));

        if (!passwordEncoder.matches(request.senha(), usuario.getSenhaHash())) {
            throw new IllegalArgumentException("Email ou senha invalidos");
        }

        String token = jwtService.gerarToken(usuario);
        return new AuthResponse(token);
    }
}
