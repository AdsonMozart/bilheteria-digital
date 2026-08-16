CREATE TABLE reserva_assentos (
      reserva_id BIGINT NOT NULL,
      assento_id BIGINT NOT NULL,

      PRIMARY KEY (reserva_id, assento_id),

      CONSTRAINT fk_reserva_assentos_reserva
          FOREIGN KEY (reserva_id) REFERENCES reservas(id),

      CONSTRAINT fk_reserva_assentos_assento
          FOREIGN KEY (assento_id) REFERENCES assentos(id)
);