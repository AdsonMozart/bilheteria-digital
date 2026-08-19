package br.com.mozart.bilheteria_digital.pagamento.domain;

import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id", nullable = false, unique = true)
    private Reserva reserva;

    @Column(name = "pagamento_stripe_id", length = 120)
    private String pagamentoStripeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status = StatusPagamento.PENDENTE;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_criacao", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataCriacao;

    // construtores
    protected Pagamento() {

    }

    public Pagamento(Reserva reserva, BigDecimal valor) {
        this.reserva = reserva;
        this.valor = valor;
    }


    // metodo de vinculação do ID recebido pelo stripe
    public void vincularPagamentoStripe(String pagamentoStripeId) {
        this.pagamentoStripeId = pagamentoStripeId;
    }

    // metodo de comportamentos do pagamento
    public void aprovar() {
        if (this.status == StatusPagamento.APROVADO) {
            return;
        }

        this.status = StatusPagamento.APROVADO;
    }

    public void recusar() {
        if (this.status == StatusPagamento.APROVADO) {
            throw new IllegalStateException("Pagamento aprovado nao pode ser recusado");
        }

        this.status = StatusPagamento.RECUSADO;
    }

    // metodo de comparacao para validar aprovacao do pagamento
    public boolean estaAprovado() {
        return this.status == StatusPagamento.APROVADO;
    }

    // gets
    public Long getId() {
        return id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public String getPagamentoStripeId() {
        return pagamentoStripeId;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}
