CREATE TABLE ingressos (
       id BIGINT AUTO_INCREMENT PRIMARY KEY,
       reserva_id BIGINT NOT NULL,
       codigo VARCHAR(60) NOT NULL UNIQUE,
       assinatura_qr TEXT NOT NULL,
       token_compartilhamento VARCHAR(60) NOT NULL UNIQUE,
       status ENUM('VALIDO', 'USADO', 'CANCELADO') NOT NULL DEFAULT 'VALIDO',
       validado_em DATETIME NULL,
       validado_por BIGINT NULL,
       data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

       CONSTRAINT fk_ingressos_reserva
           FOREIGN KEY (reserva_id) REFERENCES reservas(id),

       CONSTRAINT fk_ingressos_validador
           FOREIGN KEY (validado_por) REFERENCES usuarios(id)
);