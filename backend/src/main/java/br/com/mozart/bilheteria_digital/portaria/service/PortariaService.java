package br.com.mozart.bilheteria_digital.portaria.service;

import br.com.mozart.bilheteria_digital.auth.service.JwtService;
import br.com.mozart.bilheteria_digital.ingresso.domain.Ingresso;
import br.com.mozart.bilheteria_digital.ingresso.domain.StatusIngresso;
import br.com.mozart.bilheteria_digital.ingresso.repository.IngressoRepository;
import br.com.mozart.bilheteria_digital.portaria.dto.ResultadoValidacao;
import br.com.mozart.bilheteria_digital.portaria.dto.ValidacaoIngressoResponse;
import br.com.mozart.bilheteria_digital.portaria.dto.ValidarIngressoRequest;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class PortariaService {

    private final IngressoRepository ingressoRepository;
    private final JwtService jwtService;

    public PortariaService(
            IngressoRepository ingressoRepository,
            JwtService jwtService
    ) {
        this.ingressoRepository = ingressoRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public ValidacaoIngressoResponse validarIngresso(Usuario usuarioPortaria, ValidarIngressoRequest request) {
        Ingresso ingresso = buscarIngressoPeloQr(request.codigo());

        if (ingresso == null) {
            return resposta(ResultadoValidacao.INVALIDO, "Ingresso invalido", null, request.eventoId(), null);
        }

        Long eventoIdIngresso = ingresso.getReserva().getEvento().getId();

        if (!eventoIdIngresso.equals(request.eventoId())) {
            return resposta(ResultadoValidacao.EVENTO_ERRADO, "Ingresso pertence a outro evento", ingresso.getId(), eventoIdIngresso, ingresso.getCodigo());
        }

        if (!ingresso.estaValido()) {
            return resposta(ResultadoValidacao.JA_UTILIZADO, "Ingresso ja utilizado. Entrada nao liberada.", ingresso.getId(), eventoIdIngresso, ingresso.getCodigo());
        }

        int linhasAfetadas = ingressoRepository.validarIngresso(
                ingresso.getId(),
                usuarioPortaria,
                LocalDateTime.now(),
                StatusIngresso.VALIDO,
                StatusIngresso.USADO
        );

        if (linhasAfetadas == 0) {
            return resposta(ResultadoValidacao.JA_UTILIZADO, "Ingresso ja utilizado. Entrada nao liberada.", ingresso.getId(), eventoIdIngresso, ingresso.getCodigo());
        }

        return resposta(ResultadoValidacao.VALIDO, "Entrada liberada.", ingresso.getId(), eventoIdIngresso, ingresso.getCodigo());
    }

    private Ingresso buscarIngressoPeloQr(String codigoEscaneado) {
        Claims claims = extrairClaims(codigoEscaneado);

        if (claims != null) {
            String codigo = claims.get("jti", String.class);
            Long eventoIdAssinatura = extrairLong(claims.get("eid"));

            if (codigo == null || eventoIdAssinatura == null) {
                return null;
            }

            Ingresso ingresso = ingressoRepository.findByCodigo(codigo).orElse(null);

            if (ingresso == null || !ingresso.getAssinaturaQr().equals(codigoEscaneado)) {
                return null;
            }

            Long eventoIdIngresso = ingresso.getReserva().getEvento().getId();
            return eventoIdIngresso.equals(eventoIdAssinatura) ? ingresso : null;
        }

        return ingressoRepository.findByCodigo(codigoEscaneado).orElse(null);
    }

    private Claims extrairClaims(String codigo) {
        try {
            return jwtService.extrairClaims(codigo);
        } catch (Exception ex) {
            return null;
        }
    }

    private Long extrairLong(Object valor) {
        if (valor instanceof Number numero) {
            return numero.longValue();
        }

        return null;
    }

    private ValidacaoIngressoResponse resposta(
            ResultadoValidacao resultado,
            String mensagem,
            Long ingressoId,
            Long eventoId,
            String codigo
    ) {
        return new ValidacaoIngressoResponse(
                resultado,
                mensagem,
                ingressoId,
                eventoId,
                codigo
        );
    }
}
