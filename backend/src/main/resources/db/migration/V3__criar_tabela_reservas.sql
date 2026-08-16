CREATE TABLE reservas (
      id BIGINT AUTO_INCREMENT PRIMARY KEY,
      evento_id BIGINT NOT NULL,
      cliente_id BIGINT NOT NULL,
      quantidade INT NOT NULL,
      valor_total DECIMAL(10,2) NOT NULL,
      status ENUM('PENDENTE', 'PAGA', 'RECUSADA', 'EXPIRADA', 'CANCELADA') NOT NULL DEFAULT 'PENDENTE',
      validade DATETIME NOT NULL,
      data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT fk_reservas_evento
          FOREIGN KEY (evento_id) REFERENCES eventos(id),

      CONSTRAINT fk_reservas_cliente
          FOREIGN KEY (cliente_id) REFERENCES usuarios(id)
);