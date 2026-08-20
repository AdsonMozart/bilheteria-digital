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
APP_CORS_ALLOWED_ORIGINS
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
database: bilheteria_digital
usuario: bilheteria_user
senha: troque-esta-senha-local
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

O arquivo `backend/.env.example` mostra quais variaveis sao necessarias. O backend carrega automaticamente um arquivo local `backend/.env`, que e ignorado pelo Git. Voce tambem pode configurar as mesmas variaveis no terminal ou na configuracao de execucao da IDE.

Para usar arquivo local:

```powershell
cd backend
Copy-Item .env.example .env
```

Depois preencha os valores no `backend/.env`.

No PowerShell:

```powershell
cd backend
$env:DB_USERNAME="bilheteria_user"
$env:DB_PASSWORD="102030"
$env:JWT_SECRET="chave-local-com-pelo-menos-32-caracteres"
$env:APP_CORS_ALLOWED_ORIGINS="http://localhost:5173"
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
GET http://localhost:8080/api/health
```

Resposta esperada:

```json
{
  "status": "UP",
  "service": "bilheteria-digital"
}
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## 6. Usuarios E Eventos De Avaliacao

A migration `V8__seed_dados_avaliacao.sql` cria automaticamente usuarios e eventos de teste quando o backend inicia com o Flyway.

A senha de todos os usuarios abaixo e:

```text
123456
```

Usuarios disponiveis:

```text
organizador@teste.com  ORGANIZADOR
cliente1@teste.com     CLIENTE
cliente2@teste.com     CLIENTE
portaria@teste.com     PORTARIA
```

Eventos publicados criados automaticamente:

```text
Show Teste Pista       SHOW   capacidade geral
Cinema Teste Assentos  FILME  20 assentos disponiveis
```

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
  "email": "cliente1@teste.com",
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

Buscar eventos com filtros e paginacao:

```http
GET http://localhost:8080/api/eventos/buscar?page=0&size=10&sort=dataHora,asc
```

Filtros disponiveis:

```text
titulo
tipo=SHOW ou FILME
local
dataInicio=2026-12-01
dataFim=2026-12-31
organizadorId
page
size
sort=dataHora,asc
sort=preco,desc
```

Exemplo:

```http
GET http://localhost:8080/api/eventos/buscar?titulo=teste&tipo=SHOW&local=arena&page=0&size=5&sort=preco,desc
```

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

## 21. Testar Cancelamento Com Devolucao Ao Estoque

Use o token do organizador.

```http
POST http://localhost:8080/api/organizador/eventos/ID_EVENTO/cancelar
Authorization: Bearer TOKEN_ORGANIZADOR
```

Comportamento esperado:

- O evento vira `CANCELADO`.
- Reservas ativas do evento viram `CANCELADA`.
- Ingressos emitidos do evento viram `CANCELADO`.
- Capacidade geral ou assentos vinculados sao liberados.

## 22. Expiracao Automatica De Reservas

Reservas pendentes expiram automaticamente. O backend verifica reservas vencidas a cada 60 segundos.

Comportamento esperado:

- Reserva `PENDENTE` vencida vira `EXPIRADA`.
- Evento de capacidade geral tem estoque devolvido.
- Evento com assentos tem os assentos liberados.

## 23. Rodar O Frontend

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

## 24. Status HTTP Esperados

Principais retornos:

- `200 OK`: consultas, login, publicacao/cancelamento e operacoes processadas.
- `201 Created`: cadastro, criacao de evento, criacao de reserva, criacao de pagamento e criacao de PaymentIntent.
- `400 Bad Request`: request invalido ou regra de negocio violada.
- `401 Unauthorized`: ausencia ou invalidade do JWT em rota protegida.
- `403 Forbidden`: usuario autenticado sem permissao para a rota.
- `404 Not Found`: recurso nao encontrado.
- `409 Conflict`: conflito de estado ou dados.

Resposta padrao de erro:

```json
{
  "timestamp": "2026-08-19T19:00:00",
  "status": 400,
  "erro": "Requisicao invalida",
  "mensagem": "Mensagem do erro",
  "path": "/api/exemplo",
  "campos": null
}
```

## 25. Configuracao Para Producao

O backend possui profile `prod`.

Variaveis principais:

```text
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:mysql://HOST:PORTA/DATABASE
DB_USERNAME=usuario
DB_PASSWORD=senha
JWT_SECRET=chave-com-pelo-menos-32-caracteres
APP_CORS_ALLOWED_ORIGINS=https://seu-frontend.vercel.app
TMDB_ACCESS_TOKEN=token_tmdb
TICKETMASTER_API_KEY=chave_ticketmaster
STRIPE_SECRET_KEY=sk_test_ou_sk_live
STRIPE_WEBHOOK_SECRET=whsec_producao
```

Em producao, configure o webhook da Stripe para:

```text
https://URL_DO_BACKEND/api/webhooks/stripe
```

## 26. Estado Atual E Limitacoes Conhecidas

Este projeto possui backend funcional para os principais fluxos: autenticacao, catalogo externo TMDb/Ticketmaster, eventos, reservas, Stripe test mode, ingressos, compartilhamento e portaria.

Pontos ainda pendentes ou limitados nesta versao:

- O endpoint publico de cadastro cria apenas usuarios `CLIENTE`. Usuarios `ORGANIZADOR` e `PORTARIA` sao criados pela migration de seed para avaliacao.
- O fluxo Stripe completo exige Stripe CLI rodando localmente para encaminhar webhooks ao backend.
- A documentacao de deploy final deve ser preenchida quando as URLs de producao estiverem disponiveis.

## 27. Testes Do Backend

Foram adicionados testes basicos para:

- login e cadastro;
- permissoes por perfil;
- criacao, publicacao e cancelamento de eventos;
- reserva geral;
- reserva por assento;
- expiracao automatica;
- pagamento aprovado e recusado;
- validacao da portaria.

## 28. Uso De IA

Durante o desenvolvimento, ferramentas de IA foram usadas como apoio para revisao de requisitos, organizacao de tarefas, sugestao de testes e apoio na escrita de partes do backend. As decisoes de regra de negocio, nomes finais, validacao manual e testes foram revisados no proprio projeto.

## 29. Comandos Uteis

Rodar testes do backend:

```powershell
cd backend
.\mvnw.cmd test
```

Gerar o `.jar` do backend:

```powershell
cd backend
.\mvnw.cmd package
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

## 30. Checklist De Avaliacao

Para validar o sistema de ponta a ponta:

1. MySQL rodando.
2. Backend rodando em `localhost:8080`.
3. `GET /api/health` retornando `UP`.
4. Usuarios de avaliacao criados pela migration `V8`.
5. Login funcionando para `ORGANIZADOR`, `CLIENTE` e `PORTARIA`.
6. Busca TMDb funcionando.
7. Busca Ticketmaster funcionando.
8. Evento geral criado e publicado.
9. Evento com assentos criado e publicado.
10. Filtros e paginacao em `/api/eventos/buscar` funcionando.
11. Reserva geral criada.
12. Reserva com assento criada.
13. PaymentIntent criado.
14. Stripe CLI encaminhando webhook com `[200]`.
15. Reserva aprovada virando `PAGA`.
16. Ingresso gerado.
17. Compartilhamento publico funcionando.
18. Portaria retornando `VALIDO`, `JA_UTILIZADO`, `EVENTO_ERRADO` e `INVALIDO`.
19. Cancelamento de evento devolvendo estoque.
20. Testes passando com `.\mvnw.cmd test`.
