CREATE TABLE assentos (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      evento_id BIGINT NOT NULL,
      setor VARCHAR(50) NOT NULL,
      fileira VARCHAR(10) NOT NULL,
      numero INT NOT NULL,
      status ENUM('DISPONIVEL', 'RESERVADO', 'VENDIDO') NOT NULL DEFAULT 'DISPONIVEL',
      reserva_id BIGINT NULL,

      CONSTRAINT fk_assentos_evento
          FOREIGN KEY (evento_id) REFERENCES eventos(id),

      CONSTRAINT uk_assento_posicao
          UNIQUE (evento_id, setor, fileira, numero)
);