CREATE TABLE eventos (
     id BIGINT AUTO_INCREMENT PRIMARY KEY,
     organizador_id BIGINT NOT NULL,
     origem_externa ENUM('TICKETMASTER', 'TMDB', 'MANUAL') NOT NULL,
     id_evento_externo VARCHAR(120),
     titulo VARCHAR(200) NOT NULL,
     tipo_evento ENUM('SHOW', 'FILME') NOT NULL,
     descricao TEXT,
     url_imagem VARCHAR(500),
     nome_local VARCHAR(200) NOT NULL,
     endereco_local VARCHAR(300),
     data_hora DATETIME NOT NULL,
     tipo_capacidade ENUM('ASSENTOS', 'GERAL') NOT NULL,
     preco DECIMAL(10,2) NOT NULL,
     capacidade INT NOT NULL,
     capacidade_vendida INT NOT NULL DEFAULT 0,
     status ENUM('RASCUNHO', 'PUBLICADO', 'CANCELADO') NOT NULL DEFAULT 'RASCUNHO',
     data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

     CONSTRAINT fk_eventos_organizador
         FOREIGN KEY (organizador_id) REFERENCES usuarios(id)
);