# Bilheteria Digital

Sistema de eventos e ingressos desenvolvido com backend em Java/Spring Boot, frontend em React/Vite/TypeScript e banco MySQL. A aplicacao cobre fluxos de organizador, cliente e portaria: catalogo externo, criacao de eventos, reserva por capacidade geral ou assentos, pagamento com Stripe em modo teste, emissao de ingressos com QR assinado, compartilhamento publico e validacao de entrada.

Este README foi escrito para que uma pessoa avaliadora consiga configurar o ambiente do zero e testar as funcionalidades principais sem depender de credenciais locais do autor do projeto.

## Tecnologias

- Java 21
- Spring Boot 4.1
- Spring Security com JWT
- Spring Data JPA
- Flyway
- MySQL 8
- React
- Vite
- TypeScript
- Stripe em modo teste
- TMDb API
- Ticketmaster Discovery API

## Arquitetura

O backend segue uma arquitetura em camadas, organizada por dominios de negocio.

Estrutura principal:

```text
backend/src/main/java/br/com/mozart/bilheteria_digital
|-- auth
|-- catalogo
|-- evento
|-- assento
|-- reserva
|-- pagamento
|-- ingresso
|-- portaria
|-- usuario
`-- common
```

Cada dominio concentra suas proprias classes de controller, service, repository, DTOs e entidades quando aplicavel. As integracoes externas ficam isoladas:

```text
catalogo/tmdb
catalogo/ticketmaster
pagamento/stripe
```

Essa organizacao facilita a leitura do sistema por funcionalidade e evita misturar regra de negocio com detalhes de API externa.

## Protecao De Credenciais

Credenciais reais nao devem ser versionadas no repositorio.

O projeto usa variaveis de ambiente para todos os segredos e chaves externas:

```text
DB_USERNAME
DB_PASSWORD
JWT_SECRET
TMDB_ACCESS_TOKEN
TICKETMASTER_API_KEY
STRIPE_SECRET_KEY
STRIPE_WEBHOOK_SECRET
VITE_API_URL
VITE_STRIPE_PUBLISHABLE_KEY
```

Arquivos `.env` reais sao ignorados pelo Git. O repositorio contem apenas exemplos seguros:

```text
backend/.env.example
frontend/.env.example
```

O avaliador deve criar as proprias credenciais de teste nas plataformas externas e configura-las localmente.

## Pre-Requisitos

Instale os itens abaixo antes de iniciar:

1. Java 21.
2. Docker e Docker Compose.
3. Node.js LTS.
4. npm.
5. Stripe CLI.
6. Insomnia, Postman ou outro cliente HTTP.
7. Conta no TMDb.
8. Conta no Ticketmaster Developer.
9. Conta Stripe em modo teste.

## 1. Clonar O Projeto

```powershell
git clone URL_DO_REPOSITORIO
cd bilheteria-digital
```

Todos os comandos a seguir consideram que o terminal esta na raiz do projeto.

## 2. Subir O Banco MySQL

O projeto inclui um `docker-compose.yml` com MySQL 8.

Execute:

```powershell
docker compose up -d
```

Dados locais do banco:

```text
host: localhost
porta: 3306
database: bilheteria
usuario: bilheteria_user
senha: 102030
```

As tabelas sao criadas automaticamente pelo Flyway quando o backend inicia.

Para verificar se o container esta rodando:

```powershell
docker ps
```

O container esperado e:

```text
bilheteria_mysql
```

## 3. Configurar Variaveis Do Backend

O arquivo `backend/.env.example` mostra quais variaveis sao necessarias. Como o Spring Boot nao carrega `.env` automaticamente neste projeto, configure as variaveis no terminal ou na configuracao de execucao da IDE.

No PowerShell:

```powershell
cd backend
$env:DB_USERNAME="bilheteria_user"
$env:DB_PASSWORD="102030"
$env:JWT_SECRET="chave-local-com-pelo-menos-32-caracteres"
$env:TMDB_ACCESS_TOKEN="TOKEN_TMDB_DO_AVALIADOR"
$env:TICKETMASTER_API_KEY="CONSUMER_KEY_TICKETMASTER_DO_AVALIADOR"
$env:TICKETMASTER_COUNTRY_CODE="BR"
$env:STRIPE_SECRET_KEY="STRIPE_SECRET_KEY_DE_TESTE_DO_AVALIADOR"
```

O `STRIPE_WEBHOOK_SECRET` e configurado depois de iniciar o `stripe listen`, conforme explicado na secao de pagamentos.

## 4. Obter Chaves Externas

### TMDb

1. Acesse o painel do TMDb.
2. Crie ou acesse uma aplicacao.
3. Copie o token de leitura da API, normalmente chamado de `API Read Access Token`.
4. Configure a variavel:

```powershell
$env:TMDB_ACCESS_TOKEN="TOKEN_TMDB_DO_AVALIADOR"
```

Esse token e usado pelo backend como Bearer Token ao consultar filmes.

### Ticketmaster

1. Acesse o Ticketmaster Developer.
2. Crie uma aplicacao.
3. Copie a `Consumer Key`.
4. Configure:

```powershell
$env:TICKETMASTER_API_KEY="CONSUMER_KEY_TICKETMASTER_DO_AVALIADOR"
```

Por padrao, o projeto busca eventos do Brasil:

```powershell
$env:TICKETMASTER_COUNTRY_CODE="BR"
```

Para facilitar testes com mais resultados, pode ser usado:

```powershell
$env:TICKETMASTER_COUNTRY_CODE="US"
```

### Stripe

1. Acesse o Dashboard da Stripe.
2. Ative o modo de teste.
3. Copie a chave secreta de teste.
4. Configure:

```powershell
$env:STRIPE_SECRET_KEY="STRIPE_SECRET_KEY_DE_TESTE_DO_AVALIADOR"
```

A chave publicavel de teste sera usada no frontend:

```text
VITE_STRIPE_PUBLISHABLE_KEY
```

## 5. Rodar O Backend

Com o MySQL rodando e as variaveis configuradas:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

URL local:

```text
http://localhost:8080
```

Teste rapido:

```http
GET http://localhost:8080/api/eventos
```

Resposta esperada em banco vazio:

```json
[]
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## 6. Criar Usuarios De Avaliacao

Estado atual conhecido: o cadastro publico cria usuarios `CLIENTE`, mas nao cria `ORGANIZADOR` nem `PORTARIA`. Enquanto a migration de seed nao estiver presente, esses usuarios devem ser criados diretamente no banco para testar todos os perfis.

A senha dos usuarios abaixo e:

```text
123456
```

Execute no MySQL:

```sql
INSERT INTO usuarios (nome, email, senha_hash, nivel_acesso)
VALUES
('Organizador Teste', 'organizador@teste.com', '$2a$10$.AncOLIFrX/Og8ZEeDpHrOHM5PGr9.8GnWW/UVacUVdjBU2IB6qLC', 'ORGANIZADOR'),
('Cliente Teste', 'cliente@teste.com', '$2a$10$.AncOLIFrX/Og8ZEeDpHrOHM5PGr9.8GnWW/UVacUVdjBU2IB6qLC', 'CLIENTE'),
('Portaria Teste', 'portaria@teste.com', '$2a$10$.AncOLIFrX/Og8ZEeDpHrOHM5PGr9.8GnWW/UVacUVdjBU2IB6qLC', 'PORTARIA');
```

Caso os usuarios ja existam, remova linhas duplicadas manualmente ou altere os emails no SQL.

## 7. Testar Login Dos Perfis

No Insomnia, crie uma requisicao:

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

Body para organizador:

```json
{
  "email": "organizador@teste.com",
  "senha": "123456"
}
```

Body para cliente:

```json
{
  "email": "cliente@teste.com",
  "senha": "123456"
}
```

Body para portaria:

```json
{
  "email": "portaria@teste.com",
  "senha": "123456"
}
```

Resposta esperada:

```json
{
  "token": "JWT_GERADO_PELO_BACKEND"
}
```

Guarde um token para cada perfil. Nas proximas requisicoes protegidas, use `Auth > Bearer Token` no Insomnia.

## 8. Testar Catalogo TMDb

Use o token do organizador.

Buscar filmes:

```http
GET http://localhost:8080/api/catalogo/buscar?origem=TMDB&q=matrix
Authorization: Bearer TOKEN_ORGANIZADOR
```

Resposta esperada:

```json
[
  {
    "origem": "TMDB",
    "idExterno": "603",
    "titulo": "Matrix",
    "tipo": "FILME",
    "descricao": "...",
    "urlImagem": "https://image.tmdb.org/t/p/w500/...",
    "dataLancamento": "1999-03-31",
    "avaliacao": 8.2,
    "totalVotos": 26000
  }
]
```

Detalhar filme:

```http
GET http://localhost:8080/api/catalogo/TMDB/603
Authorization: Bearer TOKEN_ORGANIZADOR
```

## 9. Testar Catalogo Ticketmaster

Use o token do organizador.

Buscar shows:

```http
GET http://localhost:8080/api/catalogo/buscar?origem=TICKETMASTER&q=metallica
Authorization: Bearer TOKEN_ORGANIZADOR
```

Detalhar um show:

```http
GET http://localhost:8080/api/catalogo/TICKETMASTER/ID_EXTERNO
Authorization: Bearer TOKEN_ORGANIZADOR
```

O `ID_EXTERNO` deve ser copiado da resposta da busca.

## 10. Criar Evento De Capacidade Geral

Use o token do organizador.

```http
POST http://localhost:8080/api/organizador/eventos
Authorization: Bearer TOKEN_ORGANIZADOR
Content-Type: application/json
```

Body:

```json
{
  "origemExterna": "MANUAL",
  "idExterno": null,
  "titulo": "Show Teste Pista",
  "tipo": "SHOW",
  "descricao": "Evento de teste com capacidade geral.",
  "urlImagem": null,
  "nomeLocal": "Arena Teste",
  "enderecoLocal": "Rua Exemplo, 100",
  "dataHora": "2026-12-20T20:00:00",
  "tipoCapacidade": "GERAL",
  "preco": 50.00,
  "capacidade": 100
}
```

Resposta esperada: status HTTP `201 Created` e um objeto de evento com `status` igual a `RASCUNHO`.

Guarde o `id` retornado.

Publique o evento:

```http
POST http://localhost:8080/api/organizador/eventos/ID_EVENTO/publicar
Authorization: Bearer TOKEN_ORGANIZADOR
```

Resposta esperada: evento com `status` igual a `PUBLICADO`.

## 11. Criar Evento Com Assentos

Use o token do organizador.

```http
POST http://localhost:8080/api/organizador/eventos
Authorization: Bearer TOKEN_ORGANIZADOR
Content-Type: application/json
```

Body:

```json
{
  "origemExterna": "MANUAL",
  "idExterno": null,
  "titulo": "Cinema Teste Assentos",
  "tipo": "FILME",
  "descricao": "Evento de teste com mapa de assentos.",
  "urlImagem": null,
  "nomeLocal": "Sala Teste",
  "enderecoLocal": "Shopping Exemplo",
  "dataHora": "2026-12-21T19:30:00",
  "tipoCapacidade": "ASSENTOS",
  "preco": 35.00,
  "capacidade": 30
}
```

Publique o evento:

```http
POST http://localhost:8080/api/organizador/eventos/ID_EVENTO/publicar
Authorization: Bearer TOKEN_ORGANIZADOR
```

Ao publicar um evento `ASSENTOS`, o backend gera os assentos automaticamente.

Consulte o detalhe publico:

```http
GET http://localhost:8080/api/eventos/ID_EVENTO
```

A resposta deve conter a lista `assentos`.

## 12. Listar Eventos Publicados

Rota publica:

```http
GET http://localhost:8080/api/eventos
```

Resposta esperada: lista com os eventos publicados.

Detalhar um evento:

```http
GET http://localhost:8080/api/eventos/ID_EVENTO
```

## 13. Criar Reserva De Capacidade Geral

Use o token do cliente.

```http
POST http://localhost:8080/api/reservas
Authorization: Bearer TOKEN_CLIENTE
Content-Type: application/json
```

Body:

```json
{
  "eventoId": ID_EVENTO_GERAL,
  "quantidade": 1,
  "assentoIds": null
}
```

Resposta esperada: status HTTP `201 Created` e reserva com `status` igual a `PENDENTE`.

Guarde o `id` da reserva.

## 14. Criar Reserva Com Assentos

Use o token do cliente.

Primeiro consulte o detalhe do evento com assentos:

```http
GET http://localhost:8080/api/eventos/ID_EVENTO_ASSENTOS
```

Escolha um assento com:

```json
"status": "DISPONIVEL"
```

Depois crie a reserva:

```http
POST http://localhost:8080/api/reservas
Authorization: Bearer TOKEN_CLIENTE
Content-Type: application/json
```

Body:

```json
{
  "eventoId": ID_EVENTO_ASSENTOS,
  "quantidade": null,
  "assentoIds": [ID_ASSENTO_DISPONIVEL]
}
```

Resposta esperada: reserva `PENDENTE`.

## 15. Configurar Webhook Stripe Local

O pagamento real depende de webhook. Abra um segundo terminal e execute:

```powershell
stripe login
```

Depois:

```powershell
stripe listen --events payment_intent.succeeded,payment_intent.payment_failed --forward-to localhost:8080/api/webhooks/stripe
```

O Stripe CLI exibira um segredo de webhook. Configure esse valor no terminal do backend:

```powershell
$env:STRIPE_WEBHOOK_SECRET="WEBHOOK_SECRET_MOSTRADO_PELO_STRIPE_LISTEN"
```

O backend precisa ser reiniciado depois que essa variavel for configurada.

Ao testar pagamento, mantenha dois terminais abertos:

```text
Terminal 1: backend Spring Boot
Terminal 2: stripe listen
```

## 16. Criar PaymentIntent

Use uma reserva `PENDENTE` criada pelo cliente.

```http
POST http://localhost:8080/api/pagamentos/reservas/ID_RESERVA/payment-intent
Authorization: Bearer TOKEN_CLIENTE
```

Resposta esperada:

```json
{
  "pagamentoId": 1,
  "reservaId": 1,
  "stripePaymentIntentId": "pi_...",
  "clientSecret": "pi_..._secret_..."
}
```

Guarde o `stripePaymentIntentId`.

## 17. Confirmar Pagamento Aprovado

No terminal, confirme o PaymentIntent:

```powershell
stripe payment_intents confirm pi_ID_RETORNADO --payment-method pm_card_visa --return-url http://localhost:5173/pagamento/sucesso
```

No terminal do `stripe listen`, o resultado esperado e:

```text
--> payment_intent.succeeded
<-- [200] POST http://localhost:8080/api/webhooks/stripe
```

Depois consulte a reserva:

```http
GET http://localhost:8080/api/reservas/ID_RESERVA
Authorization: Bearer TOKEN_CLIENTE
```

Status esperado:

```text
PAGA
```

Consulte os ingressos:

```http
GET http://localhost:8080/api/me/ingressos
Authorization: Bearer TOKEN_CLIENTE
```

A resposta deve conter ingresso com `status` igual a `VALIDO`.

## 18. Confirmar Pagamento Recusado

Crie uma nova reserva e um novo PaymentIntent.

Confirme com metodo recusado:

```powershell
stripe payment_intents confirm pi_ID_RETORNADO --payment-method pm_card_chargeDeclined --return-url http://localhost:5173/pagamento/erro
```

No terminal do `stripe listen`, o resultado esperado e:

```text
--> payment_intent.payment_failed
<-- [200] POST http://localhost:8080/api/webhooks/stripe
```

Depois consulte a reserva:

```http
GET http://localhost:8080/api/reservas/ID_RESERVA
Authorization: Bearer TOKEN_CLIENTE
```

Status esperado:

```text
RECUSADA
```

## 19. Testar Compartilhamento Publico Do Ingresso

Depois de um pagamento aprovado, liste os ingressos do cliente:

```http
GET http://localhost:8080/api/me/ingressos
Authorization: Bearer TOKEN_CLIENTE
```

Copie o campo:

```text
tokenCompartilhamento
```

Acesse a rota publica:

```http
GET http://localhost:8080/api/ingressos/compartilhado/TOKEN_COMPARTILHAMENTO
```

Essa rota nao exige JWT.

Resposta esperada: dados publicos do ingresso, sem expor a assinatura crua do QR.

## 20. Testar Validacao Da Portaria

Use o token do usuario portaria.

Liste os ingressos como cliente e copie o campo:

```text
assinaturaQr
```

Esse campo representa o conteudo assinado que seria colocado no QR Code.

Valide na portaria:

```http
POST http://localhost:8080/api/portaria/validar
Authorization: Bearer TOKEN_PORTARIA
Content-Type: application/json
```

Body:

```json
{
  "eventoId": ID_EVENTO_DO_INGRESSO,
  "codigo": "ASSINATURA_QR_DO_INGRESSO"
}
```

Primeira validacao esperada:

```json
{
  "resultado": "VALIDO",
  "mensagem": "Ingresso validado com sucesso"
}
```

Repita a mesma chamada.

Segunda validacao esperada:

```json
{
  "resultado": "JA_UTILIZADO",
  "mensagem": "Ingresso ja utilizado"
}
```

Teste tambem um evento errado:

```json
{
  "eventoId": ID_DE_OUTRO_EVENTO,
  "codigo": "ASSINATURA_QR_DO_INGRESSO"
}
```

Resultado esperado:

```text
EVENTO_ERRADO
```

Teste tambem um codigo invalido:

```json
{
  "eventoId": ID_EVENTO_DO_INGRESSO,
  "codigo": "codigo-invalido"
}
```

Resultado esperado:

```text
INVALIDO
```

## 21. Rodar O Frontend

Crie o arquivo local:

```text
frontend/.env
```

Conteudo:

```env
VITE_API_URL=http://localhost:8080
VITE_STRIPE_PUBLISHABLE_KEY=STRIPE_PUBLISHABLE_KEY_DE_TESTE_DO_AVALIADOR
```

Instale dependencias e inicie:

```powershell
cd frontend
npm install
npm run dev
```

URL local:

```text
http://localhost:5173
```

## 22. Status HTTP Esperados

Principais retornos:

- `200 OK`: consultas, login, publicacao/cancelamento e operacoes processadas.
- `201 Created`: cadastro, criacao de evento, criacao de reserva, criacao de pagamento e criacao de PaymentIntent.
- `400 Bad Request`: request invalido ou regra de negocio violada.
- `401 Unauthorized`: ausencia ou invalidade do JWT em rota protegida.
- `403 Forbidden`: usuario autenticado sem permissao para a rota.

## 23. Estado Atual E Limitacoes Conhecidas

Este projeto possui backend funcional para os principais fluxos: autenticacao, catalogo externo TMDb/Ticketmaster, eventos, reservas, Stripe test mode, ingressos, compartilhamento e portaria.

Pontos ainda pendentes ou limitados nesta versao:

- Nao ha migration de seed versionada criando usuarios e eventos automaticamente. Por isso este README inclui SQL manual para criar usuarios de avaliacao.
- O endpoint publico de cadastro cria apenas usuarios `CLIENTE`. Usuarios `ORGANIZADOR` e `PORTARIA` precisam ser criados por seed ou SQL manual.
- O fluxo Stripe completo exige Stripe CLI rodando localmente para encaminhar webhooks ao backend.
- A listagem publica de eventos ainda nao documenta filtros avancados por cidade/data no README.
- A documentacao de deploy final deve ser preenchida quando as URLs de producao estiverem disponiveis.

## 24. Comandos Uteis

Rodar testes do backend:

```powershell
cd backend
.\mvnw.cmd test
```

Limpar e testar backend:

```powershell
cd backend
.\mvnw.cmd clean test
```

Parar banco:

```powershell
docker compose down
```

Ver logs do MySQL:

```powershell
docker logs bilheteria_mysql
```

## 25. Checklist De Avaliacao

Para validar o sistema de ponta a ponta:

1. MySQL rodando.
2. Backend rodando em `localhost:8080`.
3. Usuarios de avaliacao criados.
4. Login funcionando para `ORGANIZADOR`, `CLIENTE` e `PORTARIA`.
5. Busca TMDb funcionando.
6. Busca Ticketmaster funcionando.
7. Evento geral criado e publicado.
8. Evento com assentos criado e publicado.
9. Reserva geral criada.
10. Reserva com assento criada.
11. PaymentIntent criado.
12. Stripe CLI encaminhando webhook com `[200]`.
13. Reserva aprovada virando `PAGA`.
14. Ingresso gerado.
15. Compartilhamento publico funcionando.
16. Portaria retornando `VALIDO`, `JA_UTILIZADO`, `EVENTO_ERRADO` e `INVALIDO`.
