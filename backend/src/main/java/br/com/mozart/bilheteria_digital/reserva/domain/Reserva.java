package br.com.mozart.bilheteria_digital.reserva.domain;

import br.com.mozart.bilheteria_digital.evento.domain.Evento;
import br.com.mozart.bilheteria_digital.usuario.domain.Usuario;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusReserva status = StatusReserva.PENDENTE;

    @Column(nullable = false)
    private LocalDateTime validade;

    @Column(name = "data_criacao", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dataCriacao;

    // construtores
    protected Reserva() {

    }

    public Reserva(Evento evento, Usuario cliente, Integer quantidade, BigDecimal valorTotal, LocalDateTime validade) {
        this.evento = evento;
        this.cliente = cliente;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.validade = validade;
    }

    // metodos atribuitivos de comportamentos para status da reserva
    public void marcarComoPaga() {
        this.status = StatusReserva.PAGA;
    }

    public void marcarComoRecusada() {
        this.status = StatusReserva.RECUSADA;
    }

    public void cancelar() {
        this.status = StatusReserva.CANCELADA;
    }

    public void expirarValidade() {
        this.status = StatusReserva.EXPIRADA;
    }

    // metodo comparativo para status da reserva
    public boolean estaPendente() {
        return this.status == StatusReserva.PENDENTE;
    }

    // gets
    public Long getId() {
        return id;
    }

    public Evento getEvento() {
        return evento;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public StatusReserva getStatus() {
        return status;
    }

    public LocalDateTime getValidade() {
        return validade;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}
