package br.com.mozart.bilheteria_digital.assento.domain;

import br.com.mozart.bilheteria_digital.evento.domain.Evento;
import jakarta.persistence.*;

@Entity
@Table(name = "assentos", uniqueConstraints = {@UniqueConstraint(name = "uk_assento_posicao", columnNames = {"evento_id", "setor", "fileira", "numero"})})
public class Assento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @Column(nullable = false, length = 50)
    private String setor;

    @Column(nullable = false, length = 10)
    private String fileira;

    @Column(nullable = false)
    private Integer numero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAssento status = StatusAssento.DISPONIVEL;

    @Column(name = "reserva_id")
    private Long reservaId;

    // construtores
    protected Assento() {

    }

    public Assento(Evento evento, String setor, String fileira, Integer numero) {
        this.evento = evento;
        this.setor = setor;
        this.fileira = fileira;
        this.numero = numero;
    }

    // metodos atribuitivos de comportamentos para status do assento
    public void marcarComoReservado(Long reservaId) {
        this.status = StatusAssento.RESERVADO;
        this.reservaId = reservaId;
    }

    public void marcarComoVendido() {
        this.status = StatusAssento.VENDIDO;
    }

    public void liberar() {
        this.status = StatusAssento.DISPONIVEL;
        this.reservaId = null;
    }

    // metodo comparativo para status do assento no momento
    public boolean estaDisponivel() {
        return this.status == StatusAssento.DISPONIVEL;
    }

    // gets
    public Long getId() {
        return id;
    }

    public Evento getEvento() {
        return evento;
    }

    public String getSetor() {
        return setor;
    }

    public String getFileira() {
        return fileira;
    }

    public Integer getNumero() {
        return numero;
    }

    public StatusAssento getStatus() {
        return status;
    }

    public Long getReservaId() {
        return reservaId;
    }
}
