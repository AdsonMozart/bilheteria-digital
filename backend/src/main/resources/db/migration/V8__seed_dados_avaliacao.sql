INSERT IGNORE INTO usuarios (nome, email, senha_hash, nivel_acesso)
VALUES
    ('Organizador Teste', 'organizador@teste.com', '$2a$10$.AncOLIFrX/Og8ZEeDpHrOHM5PGr9.8GnWW/UVacUVdjBU2IB6qLC', 'ORGANIZADOR'),
    ('Cliente Teste 1', 'cliente1@teste.com', '$2a$10$.AncOLIFrX/Og8ZEeDpHrOHM5PGr9.8GnWW/UVacUVdjBU2IB6qLC', 'CLIENTE'),
    ('Cliente Teste 2', 'cliente2@teste.com', '$2a$10$.AncOLIFrX/Og8ZEeDpHrOHM5PGr9.8GnWW/UVacUVdjBU2IB6qLC', 'CLIENTE'),
    ('Portaria Teste', 'portaria@teste.com', '$2a$10$.AncOLIFrX/Og8ZEeDpHrOHM5PGr9.8GnWW/UVacUVdjBU2IB6qLC', 'PORTARIA');

INSERT INTO eventos (
    organizador_id,
    origem_externa,
    id_evento_externo,
    titulo,
    tipo_evento,
    descricao,
    url_imagem,
    nome_local,
    endereco_local,
    data_hora,
    tipo_capacidade,
    preco,
    capacidade,
    capacidade_vendida,
    status
)
SELECT
    u.id,
    'MANUAL',
    NULL,
    'Show Teste Pista',
    'SHOW',
    'Evento publicado para teste de reserva por quantidade.',
    NULL,
    'Arena Teste',
    'Rua Exemplo, 100',
    '2026-12-20 20:00:00',
    'GERAL',
    50.00,
    100,
    0,
    'PUBLICADO'
FROM usuarios u
WHERE u.email = 'organizador@teste.com'
  AND NOT EXISTS (
      SELECT 1
      FROM eventos e
      WHERE e.titulo = 'Show Teste Pista'
        AND e.organizador_id = u.id
  );

INSERT INTO eventos (
    organizador_id,
    origem_externa,
    id_evento_externo,
    titulo,
    tipo_evento,
    descricao,
    url_imagem,
    nome_local,
    endereco_local,
    data_hora,
    tipo_capacidade,
    preco,
    capacidade,
    capacidade_vendida,
    status
)
SELECT
    u.id,
    'MANUAL',
    NULL,
    'Cinema Teste Assentos',
    'FILME',
    'Evento publicado para teste de reserva com mapa de assentos.',
    NULL,
    'Sala Teste',
    'Shopping Exemplo',
    '2026-12-21 19:30:00',
    'ASSENTOS',
    35.00,
    20,
    0,
    'PUBLICADO'
FROM usuarios u
WHERE u.email = 'organizador@teste.com'
  AND NOT EXISTS (
      SELECT 1
      FROM eventos e
      WHERE e.titulo = 'Cinema Teste Assentos'
        AND e.organizador_id = u.id
  );

INSERT IGNORE INTO assentos (evento_id, setor, fileira, numero, status)
SELECT e.id, 'UNICO', 'A', 1, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'A', 2, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'A', 3, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'A', 4, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'A', 5, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'A', 6, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'A', 7, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'A', 8, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'A', 9, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'A', 10, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'B', 1, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'B', 2, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'B', 3, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'B', 4, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'B', 5, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'B', 6, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'B', 7, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'B', 8, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'B', 9, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos'
UNION ALL SELECT e.id, 'UNICO', 'B', 10, 'DISPONIVEL' FROM eventos e WHERE e.titulo = 'Cinema Teste Assentos';
