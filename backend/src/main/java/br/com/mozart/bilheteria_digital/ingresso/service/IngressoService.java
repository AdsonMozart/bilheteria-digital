package br.com.mozart.bilheteria_digital.ingresso.service;

import br.com.mozart.bilheteria_digital.ingresso.domain.Ingresso;
import br.com.mozart.bilheteria_digital.ingresso.dto.IngressoCompartilhadoResponse;
import br.com.mozart.bilheteria_digital.ingresso.dto.IngressoResponse;
import br.com.mozart.bilheteria_digital.ingresso.repository.IngressoRepository;
import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class IngressoService {

    private final IngressoRepository ingressoRepository;
    private final SecretKey secretKey;

    public IngressoService(
            IngressoRepository ingressoRepository,
            @Value("${app.jwt.secret}") String jwtSecret
    ) {
        this.ingressoRepository = ingressoRepository;
        this.secretKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public void gerarIngressosParaReserva(Reserva reserva) {
        List<Ingresso> ingressosExistentes = ingressoRepository.findByReserva_Id(reserva.getId());

        if (!ingressosExistentes.isEmpty()) {
            return;
        }

        for (int i = 0; i < reserva.getQuantidade(); i++) {
            String codigo = UUID.randomUUID().toString();
            String tokenCompartilhamento = UUID.randomUUID().toString();
            String assinaturaQr = gerarAssinaturaQr(reserva, codigo);

            Ingresso ingresso = new Ingresso(
                    reserva,
                    codigo,
                    assinaturaQr,
                    tokenCompartilhamento
            );

            ingressoRepository.save(ingresso);
        }
    }

    public List<IngressoResponse> listarIngressosDoCliente(Usuario cliente) {
        return ingressoRepository.findByReserva_Cliente_Id(cliente.getId())
                .stream()
                .map(IngressoResponse::from)
                .toList();
    }

    private String gerarAssinaturaQr(Reserva reserva, String codigo) {
        Instant agora = Instant.now();

        return Jwts.builder()
                .claim("rid", reserva.getId())
                .claim("eid", reserva.getEvento().getId())
                .claim("jti", codigo)
                .issuedAt(java.util.Date.from(agora))
                .signWith(secretKey)
                .compact();
    }

    public IngressoCompartilhadoResponse buscarIngressoCompartilhado(String tokenCompartilhamento) {
        Ingresso ingresso = ingressoRepository.findByTokenCompartilhamento(tokenCompartilhamento)
                .orElseThrow(() -> new IllegalArgumentException("Ingresso compartilhado nao encontrado"));

        return IngressoCompartilhadoResponse.from(ingresso);
    }
}
