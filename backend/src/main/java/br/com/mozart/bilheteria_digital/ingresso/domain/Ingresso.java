package br.com.mozart.bilheteria_digital.ingresso.domain;

import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ingressos")
public class Ingresso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Column(nullable = false, unique = true, length = 60)
    private String codigo;

    @Column(name = "assinatura_qr", nullable = false, columnDefinition = "TEXT")
    private String assinaturaQr;

    @Column(name = "token_compartilhamento", nullable = false, unique = true, length = 60)
    private String tokenCompartilhamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusIngresso status = StatusIngresso.VALIDO;

    @Column(name = "validado_em")
    private LocalDateTime validadoEm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validado_por")
    private Usuario validadoPor;

    @Column(name = "data_criacao", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataCriacao;

    // construtores
    protected Ingresso() {

    }

    public Ingresso(Reserva reserva, String codigo, String assinaturaQr, String tokenCompartilhamento) {
        this.reserva = reserva;
        this.codigo = codigo;
        this.assinaturaQr = assinaturaQr;
        this.tokenCompartilhamento = tokenCompartilhamento;
    }

    // metodos referentes às alterações de status do ingresso
    public void marcarComoUsado(Usuario usuarioPortaria, LocalDateTime dataValidacao) {
        this.status = StatusIngresso.USADO;
        this.validadoPor = usuarioPortaria;
        this.validadoEm = dataValidacao;
    }

    public void cancelar() {
        this.status = StatusIngresso.CANCELADO;
    }

    // metodos de comparação para validar se esta valido
    public boolean estaValido() {
        return this.status == StatusIngresso.VALIDO;
    }

    // gets
    public Long getId() {
        return id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getAssinaturaQr() {
        return assinaturaQr;
    }

    public String getTokenCompartilhamento() {
        return tokenCompartilhamento;
    }

    public StatusIngresso getStatus() {
        return status;
    }

    public LocalDateTime getValidadoEm() {
        return validadoEm;
    }

    public Usuario getValidadoPor() {
        return validadoPor;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}
