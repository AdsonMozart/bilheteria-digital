package br.com.mozart.bilheteria_digital.reservaassento.domain;

import br.com.mozart.bilheteria_digital.assento.domain.Assento;
import br.com.mozart.bilheteria_digital.reserva.domain.Reserva;
import jakarta.persistence.*;

@Entity
@Table(name = "reserva_assentos")
public class ReservaAssento {

    @EmbeddedId
    private ReservaAssentoId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("reservaId")
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("assentoId")
    @JoinColumn(name = "assento_id", nullable = false)
    private Assento assento;

    // construtores
    protected ReservaAssento() {

    }

    public ReservaAssento(Reserva reserva, Assento assento) {
        this.reserva = reserva;
        this.assento = assento;
        this.id = new ReservaAssentoId(reserva.getId(),  assento.getId());
    }

    // gets
    public ReservaAssentoId getId() {
        return id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public Assento getAssento() {
        return assento;
    }
}
