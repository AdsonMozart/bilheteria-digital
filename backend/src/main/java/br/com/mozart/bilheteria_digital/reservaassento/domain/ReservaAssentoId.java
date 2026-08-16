package br.com.mozart.bilheteria_digital.reservaassento.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ReservaAssentoId implements Serializable {

    @Column(name = "reserva_id")
    private Long reservaId;

    @Column(name = "assento_id")
    private Long assentoId;

    // construtores
    protected ReservaAssentoId() {

    }

    public ReservaAssentoId(Long reservaId, Long assentoId) {
        this.reservaId = reservaId;
        this.assentoId = assentoId;
    }

    // gets

    public Long getReservaId() {
        return reservaId;
    }

    public Long getAssentoId() {
        return assentoId;
    }

    // equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReservaAssentoId that = (ReservaAssentoId) o;
        return Objects.equals(getReservaId(), that.getReservaId()) && Objects.equals(getAssentoId(), that.getAssentoId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReservaId(), getAssentoId());
    }
}
