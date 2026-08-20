# Bilheteria Digital

Plataforma de eventos e ingressos criada para o Desafio Elite Dev 2026.

O sistema cobre o fluxo principal pedido no desafio: um organizador cria e publica eventos, um cliente navega pelos eventos publicados, reserva ingressos por quantidade ou por mapa de assentos, paga em ambiente de teste da Stripe, recebe ingressos com QR Code, compartilha ingresso por link publico e a portaria valida a entrada.

Frontend publicado:

```text
https://bilheteria-digital.vercel.app
```

Rota de login publicada:

```text
https://bilheteria-digital.vercel.app/login
```

## Sumario Para Avaliacao

1. O frontend e uma SPA React/Vite publicada na Vercel.
2. O backend e uma API Java 21/Spring Boot.
3. O banco usado e MySQL 8, com schema e dados iniciais criados por Flyway.
4. A autenticacao usa JWT e tres papeis: `ORGANIZADOR`, `CLIENTE` e `PORTARIA`.
5. Os usuarios e eventos de teste sao semeados automaticamente pela migration `V8__seed_dados_avaliacao.sql`.
6. O pagamento usa Stripe em modo teste. Localmente, o webhook depende do Stripe CLI; em producao, o webhook deve ser configurado no Dashboard da Stripe.

## Tecnologias

Backend:

- Java 21
- Spring Boot 4.1
- Spring Security
- JWT com JJWT
- Spring Data JPA
- Flyway
- MySQL 8
- Stripe Java SDK
- Springdoc OpenAPI/Swagger

Frontend:

- React
- Vite
- TypeScript
- React Router
- Stripe Payment Element
- qrcode.react
- html5-qrcode

Infra local:

- Docker Compose para MySQL
- Maven Wrapper no backend
- npm no frontend

## Como O Projeto Atende Aos Requisitos

| Requisito do desafio | Implementacao no projeto |
| --- | --- |
| Navegacao e busca por eventos publicados | Home com lista, busca por titulo, tipo, local, datas e ordenacao |
| Criacao e gerenciamento pelo organizador | Area `/organizador` e tela `/organizador/novo` |
| Catalogo externo de shows ou filmes | Integracoes com TMDb e Ticketmaster em `/api/catalogo` |
| Reserva por quantidade | Eventos com `tipoCapacidade=GERAL` |
| Reserva por mapa de assentos | Eventos com `tipoCapacidade=ASSENTOS` e componente `SeatMap` |
| Pagamento simulado com confirmacao e recusa | Stripe test mode com `payment_intent.succeeded` e `payment_intent.payment_failed` |
| Area de meus ingressos com QR | `/meus-ingressos` com QR Code gerado a partir da assinatura do ingresso |
| Portaria com valido, invalido, usado ou evento errado | `/portaria` e `POST /api/portaria/validar` |
| Leitura de QR pela camera e digitacao manual | `html5-qrcode` na tela de portaria e campo manual de codigo |
| Autenticacao com tres papeis | JWT com roles `ORGANIZADOR`, `CLIENTE`, `PORTARIA` |
| Armazenamento de eventos, reservas e ingressos | Tabelas `eventos`, `reservas`, `ingressos`, `pagamentos`, `assentos` |
| Evitar venda dupla do mesmo lugar | Update condicional de assento `DISPONIVEL -> RESERVADO` |
| QR que nao possa ser forjado | QR assinado como JWT com `JWT_SECRET` |
| Compartilhamento por link | Token publico em `/ingressos/compartilhado/:token` |
| Nao validar ingresso duas vezes | Update condicional `VALIDO -> USADO` na validacao |
| Dados de teste semeados | Migration `V8__seed_dados_avaliacao.sql` |
| Docker Compose | `docker-compose.yml` com MySQL 8 |
| Testes | Testes de servico no backend para auth, eventos, reservas, pagamentos e portaria |
| Deploy | Frontend publicado na Vercel |

## Arquitetura

O backend e organizado por dominio de negocio:

```text
backend/src/main/java/br/com/mozart/bilheteria_digital
|-- auth
|-- catalogo
|   |-- ticketmaster
|   `-- tmdb
|-- evento
|-- assento
|-- reserva
|-- reservaassento
|-- pagamento
|   `-- stripe
|-- ingresso
|-- portaria
|-- usuario
`-- common
```

Camadas usadas em cada dominio:

- `controller`: entrada HTTP.
- `service`: regras de negocio.
- `repository`: acesso ao banco.
- `domain`: entidades e enums.
- `dto`: contratos de entrada e saida.

O frontend fica em `frontend/src`:

```text
components/
context/
pages/
services/
types.ts
utils/
```

## Rotas Do Frontend

Estas sao as rotas mapeadas no React Router e confirmadas no bundle publicado da Vercel:

| Rota | Acesso | Finalidade |
| --- | --- | --- |
| `/` | Publico | Listagem, busca e filtros de eventos publicados |
| `/eventos/:id` | Publico, mas reserva exige cliente | Detalhe do evento, reserva por quantidade ou assento |
| `/login` | Publico | Login e botoes de preenchimento dos usuarios de teste |
| `/cadastro` | Publico | Cadastro de novo usuario cliente |
| `/minhas-reservas` | CLIENTE | Lista reservas do cliente |
| `/meus-ingressos` | CLIENTE | Lista ingressos emitidos e QR Codes |
| `/pagamento/:reservaId` | CLIENTE | Checkout Stripe da reserva |
| `/organizador` | ORGANIZADOR | Lista eventos do organizador, publica e cancela |
| `/organizador/novo` | ORGANIZADOR | Cria evento manualmente ou a partir de catalogo externo |
| `/portaria` | PORTARIA | Valida ingresso por camera ou digitacao manual |
| `/conta` | Autenticado | Mostra nome, email e perfil do usuario logado |
| `/ingressos/compartilhado/:token` | Publico | Exibe ingresso compartilhado por link |

## Rotas Principais Da API

| Metodo | Rota | Acesso | Finalidade |
| --- | --- | --- | --- |
| `GET` | `/api/health` | Publico | Status da API |
| `POST` | `/api/auth/login` | Publico | Login |
| `POST` | `/api/auth/registrar` | Publico | Cadastro de cliente |
| `GET` | `/api/usuarios/me` | Autenticado | Usuario logado |
| `GET` | `/api/eventos` | Publico | Lista eventos publicados |
| `GET` | `/api/eventos/buscar` | Publico | Busca eventos com filtros e paginacao |
| `GET` | `/api/eventos/{id}` | Publico | Detalha evento publicado |
| `GET` | `/api/catalogo/buscar` | ORGANIZADOR | Busca TMDb ou Ticketmaster |
| `GET` | `/api/catalogo/{origem}/{idExterno}` | ORGANIZADOR | Detalha item externo |
| `GET` | `/api/organizador/eventos` | ORGANIZADOR | Lista eventos do organizador |
| `POST` | `/api/organizador/eventos` | ORGANIZADOR | Cria evento |
| `POST` | `/api/organizador/eventos/{id}/publicar` | ORGANIZADOR | Publica evento |
| `POST` | `/api/organizador/eventos/{id}/cancelar` | ORGANIZADOR | Cancela evento |
| `POST` | `/api/reservas` | CLIENTE | Cria reserva |
| `GET` | `/api/reservas/minhas` | CLIENTE | Lista reservas do cliente |
| `GET` | `/api/reservas/{id}` | CLIENTE | Busca reserva do cliente |
| `POST` | `/api/pagamentos/reservas/{reservaId}/payment-intent` | CLIENTE | Cria/reusa PaymentIntent |
| `POST` | `/api/webhooks/stripe` | Stripe | Recebe webhook de pagamento |
| `GET` | `/api/me/ingressos` | CLIENTE | Lista ingressos do cliente |
| `GET` | `/api/ingressos/compartilhado/{token}` | Publico | Busca ingresso compartilhado |
| `POST` | `/api/portaria/validar` | PORTARIA | Valida ingresso |

## Dados De Teste

A senha de todos os usuarios semeados e:

```text
123456
```

Usuarios:

| Perfil | Email | Uso |
| --- | --- | --- |
| ORGANIZADOR | `organizador@teste.com` | Criar, publicar e cancelar eventos |
| CLIENTE | `cliente1@teste.com` | Reservar, pagar e ver ingressos |
| CLIENTE | `cliente2@teste.com` | Testar concorrencia/segundo comprador |
| PORTARIA | `portaria@teste.com` | Validar ingressos |

Eventos semeados:

| Evento | Tipo | Capacidade |
| --- | --- | --- |
| `Show Teste Pista` | SHOW | Capacidade geral, 100 ingressos |
| `Cinema Teste Assentos` | FILME | Mapa de assentos, 20 assentos |

## Configuracao Local

### 1. Pre-requisitos

Instale:

- Java 21
- Docker e Docker Compose
- Node.js LTS
- npm
- Stripe CLI
- Conta TMDb
- Conta Ticketmaster Developer
- Conta Stripe em modo teste

### 2. Clonar

```powershell
git clone URL_DO_REPOSITORIO
cd bilheteria-digital
```

### 3. Subir MySQL Local

```powershell
docker compose up -d
```

Dados padrao do `docker-compose.yml`:

```text
host: localhost
porta: 3306
database: bilheteria_digital
usuario: bilheteria_user
senha: 102030
```

### 4. Configurar Variaveis Do Backend

O backend Spring Boot nao carrega `.env` automaticamente. Configure as variaveis no terminal, na IDE ou na plataforma de deploy.

No PowerShell:

```powershell
cd backend
$env:DB_URL="jdbc:mysql://localhost:3306/bilheteria_digital?createDatabaseIfNotExist=true&serverTimezone=America/Bahia"
$env:DB_USERNAME="bilheteria_user"
$env:DB_PASSWORD="102030"
$env:JWT_SECRET="chave-local-com-pelo-menos-32-caracteres"
$env:APP_CORS_ALLOWED_ORIGINS="http://localhost:5173,http://127.0.0.1:5173"
$env:TMDB_ACCESS_TOKEN="seu_token_tmdb"
$env:TICKETMASTER_API_KEY="sua_chave_ticketmaster"
$env:TICKETMASTER_COUNTRY_CODE="BR"
$env:STRIPE_SECRET_KEY="sk_test_sua_chave_secreta"
$env:STRIPE_WEBHOOK_SECRET="whsec_configurado_depois_com_stripe_listen"
```

Variaveis aceitas pelo backend:

```text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
JWT_EXPIRATION_HOURS
APP_CORS_ALLOWED_ORIGINS
TMDB_ACCESS_TOKEN
TICKETMASTER_API_KEY
TICKETMASTER_COUNTRY_CODE
STRIPE_SECRET_KEY
STRIPE_WEBHOOK_SECRET
PORT
```

### 5. Rodar Backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

API local:

```text
http://localhost:8080
```

Healthcheck:

```http
GET http://localhost:8080/api/health
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

### 6. Configurar Frontend

Crie `frontend/.env`:

```env
VITE_API_URL=http://localhost:8080
VITE_STRIPE_PUBLISHABLE_KEY=pk_test_sua_chave_publicavel
```

Instale e rode:

```powershell
cd frontend
npm install
npm run dev
```

Frontend local:

```text
http://localhost:5173
```

## Teste Manual Pelo Site Publicado

Use:

```text
https://bilheteria-digital.vercel.app/login
```

Antes de testar o site publicado, confirme que o frontend da Vercel aponta para o backend correto por `VITE_API_URL` e que o backend permite CORS para:

```text
https://bilheteria-digital.vercel.app
```

No backend em producao, a variavel deve ficar sem barra final:

```env
APP_CORS_ALLOWED_ORIGINS=https://bilheteria-digital.vercel.app
```

### Fluxo 1: Login Dos Perfis

1. Acesse `/login`.
2. Clique em um perfil de teste.
3. A senha `123456` sera preenchida.
4. Clique em `Entrar`.
5. Valide o redirecionamento:
   - Organizador vai para `/organizador`.
   - Portaria vai para `/portaria`.
   - Cliente vai para `/`.

Resultado esperado:

- Login funciona.
- Header muda conforme o perfil.
- Rotas protegidas redirecionam usuarios sem permissao.

### Fluxo 2: Navegar E Buscar Eventos

1. Acesse `/`.
2. Veja os eventos publicados.
3. Busque por `Show`, `Cinema`, `Arena` ou `Sala`.
4. Altere tipo para `Shows` ou `Filmes`.
5. Use filtros laterais de data e ordenacao.
6. Abra um evento em `/eventos/:id`.

Resultado esperado:

- Eventos publicados aparecem.
- Filtros retornam resultados coerentes.
- Detalhe mostra data, local, endereco, preco e disponibilidade.

### Fluxo 3: Reserva De Capacidade Geral

1. Entre como `cliente1@teste.com`.
2. Abra o evento `Show Teste Pista`.
3. Escolha quantidade.
4. Clique em `Reservar agora`.
5. A aplicacao redireciona para `/pagamento/:reservaId`.
6. A reserva aparece em `/minhas-reservas`.

Resultado esperado:

- Reserva criada com status `PENDENTE`.
- Capacidade disponivel e reduzida no backend.
- A reserva tem validade de 15 minutos.

### Fluxo 4: Reserva Com Mapa De Assentos

1. Entre como `cliente1@teste.com`.
2. Abra o evento `Cinema Teste Assentos`.
3. Selecione um ou mais assentos disponiveis.
4. Clique em `Reservar agora`.
5. Confira a tela de pagamento.

Resultado esperado:

- Apenas assentos disponiveis podem ser selecionados.
- Assentos reservados ou vendidos ficam indisponiveis.
- O backend usa update condicional para impedir venda duplicada.

### Fluxo 5: Criar Evento Como Organizador

1. Entre como `organizador@teste.com`.
2. Acesse `/organizador`.
3. Clique em `Criar evento`.
4. Opcionalmente busque no catalogo:
   - Origem `TMDB`, busca `matrix`.
   - Origem `TICKETMASTER`, busca `metallica`.
5. Clique em `Usar dados` para preencher parte do formulario.
6. Complete local, data, tipo de capacidade, preco e capacidade.
7. Salve o evento.
8. Na lista do organizador, clique em `Publicar`.

Resultado esperado:

- Evento nasce como `RASCUNHO`.
- Evento publicado aparece na Home.
- Se o evento for `ASSENTOS`, o backend gera assentos ao publicar.

### Fluxo 6: Cancelar Evento

1. Entre como `organizador@teste.com`.
2. Acesse `/organizador`.
3. Clique em `Cancelar` em um evento.

Resultado esperado:

- Evento muda para `CANCELADO`.
- Reservas ativas sao canceladas.
- Ingressos emitidos sao cancelados.
- Estoque/assentos sao devolvidos quando aplicavel.

### Fluxo 7: Pagamento Aprovado

1. Entre como cliente.
2. Crie uma reserva.
3. Na tela `/pagamento/:reservaId`, preencha o Payment Element da Stripe.
4. Use cartao de teste aprovado da Stripe:

```text
4242 4242 4242 4242
```

Use qualquer data futura, CVC e CEP validos.

Resultado esperado:

- Stripe confirma o PaymentIntent.
- Webhook `payment_intent.succeeded` chega ao backend.
- Reserva vira `PAGA`.
- Pagamento vira `APROVADO`.
- Ingressos sao emitidos.
- `/meus-ingressos` mostra QR Code.

### Fluxo 8: Pagamento Recusado

1. Crie uma nova reserva.
2. Use um cartao de teste recusado da Stripe, como:

```text
4000 0000 0000 0002
```

Resultado esperado:

- Stripe recusa.
- Webhook `payment_intent.payment_failed` chega ao backend.
- Reserva vira `RECUSADA`.
- Estoque ou assento e liberado.

### Fluxo 9: Meus Ingressos E Compartilhamento

1. Finalize um pagamento aprovado.
2. Acesse `/meus-ingressos`.
3. Veja o QR Code e o codigo do ingresso.
4. Clique em `Abrir link publico` ou `Copiar link`.
5. Abra o link em uma aba anonima.

Resultado esperado:

- O link publico abre `/ingressos/compartilhado/:token`.
- O ingresso compartilhado mostra dados publicos do ingresso.

Observacao tecnica: a tela autenticada de meus ingressos mostra o QR assinado. A tela publica de compartilhamento mostra QR com o codigo simples do ingresso, e a portaria aceita tanto a assinatura quanto o codigo simples.

### Fluxo 10: Validacao Da Portaria

1. Entre como `portaria@teste.com`.
2. Acesse `/portaria`.
3. Escolha o evento.
4. Leia o QR pela camera ou cole manualmente o codigo.
5. Clique em `Validar codigo`.

Resultados esperados:

| Caso | Resultado |
| --- | --- |
| Primeiro uso do ingresso correto | `VALIDO` |
| Repetir validacao do mesmo ingresso | `JA_UTILIZADO` |
| Validar ingresso em outro evento | `EVENTO_ERRADO` |
| Codigo inexistente/invalido | `INVALIDO` |

## Teste Manual Pela API

Use `http://localhost:8080` localmente ou a URL do backend publicado.

### Healthcheck

```http
GET /api/health
```

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "cliente1@teste.com",
  "senha": "123456"
}
```

Resposta esperada:

```json
{
  "token": "JWT_GERADO"
}
```

Use esse token como Bearer nas rotas protegidas.

### Buscar Eventos

```http
GET /api/eventos/buscar?titulo=teste&page=0&size=10&sort=dataHora,asc
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
sort=preco,asc
sort=preco,desc
```

### Criar Reserva Geral

```http
POST /api/reservas
Authorization: Bearer TOKEN_CLIENTE
Content-Type: application/json

{
  "eventoId": ID_EVENTO_GERAL,
  "quantidade": 1,
  "assentoIds": null
}
```

### Criar Reserva Com Assento

```http
POST /api/reservas
Authorization: Bearer TOKEN_CLIENTE
Content-Type: application/json

{
  "eventoId": ID_EVENTO_ASSENTOS,
  "quantidade": null,
  "assentoIds": [ID_ASSENTO_DISPONIVEL]
}
```

### Criar PaymentIntent

```http
POST /api/pagamentos/reservas/ID_RESERVA/payment-intent
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

### Validar Ingresso

```http
POST /api/portaria/validar
Authorization: Bearer TOKEN_PORTARIA
Content-Type: application/json

{
  "eventoId": ID_EVENTO,
  "codigo": "CODIGO_OU_ASSINATURA_QR"
}
```

## Stripe Local

Em desenvolvimento local, o backend nao recebe webhooks da Stripe sozinho. Use Stripe CLI:

```powershell
stripe login
stripe listen --events payment_intent.succeeded,payment_intent.payment_failed --forward-to localhost:8080/api/webhooks/stripe
```

O comando retorna um segredo parecido com:

```text
whsec_...
```

Configure esse valor no backend e reinicie:

```powershell
$env:STRIPE_WEBHOOK_SECRET="whsec_..."
```

Em producao, nao use `stripe listen`. Configure o endpoint no Stripe Dashboard:

```text
https://URL_DO_BACKEND/api/webhooks/stripe
```

Eventos necessarios:

```text
payment_intent.succeeded
payment_intent.payment_failed
```

## Testes Automatizados

Rodar testes do backend:

```powershell
cd backend
.\mvnw.cmd test
```

Cenarios cobertos:

- Cadastro e login.
- Montagem de permissao JWT por perfil.
- Criacao de evento como rascunho.
- Publicacao de evento e geracao de assentos.
- Cancelamento de evento, reservas e ingressos.
- Reserva por quantidade geral.
- Reserva com assento.
- Expiracao de reserva vencida e devolucao de estoque.
- Reuso/idempotencia de PaymentIntent em concorrencia.
- Pagamento aprovado gerando ingresso.
- Pagamento recusado devolvendo estoque.
- Protecao contra reprocessamento de pagamento aprovado/recusado.
- Validacao de ingresso valido.
- Validacao por codigo simples.
- Bloqueio de segunda validacao do mesmo ingresso.
- Evento errado.
- Codigo invalido.

Nao ha testes automatizados de frontend nesta versao; os fluxos visuais foram planejados para teste manual pelo navegador.

## Deploy

### Frontend Na Vercel

Configuracao:

```text
Root Directory: frontend
Framework Preset: Vite
Build Command: npm run build
Output Directory: dist
```

Variaveis:

```env
VITE_API_URL=https://URL_DO_BACKEND
VITE_STRIPE_PUBLISHABLE_KEY=pk_test_...
```

O arquivo `frontend/vercel.json` redireciona todas as rotas para `index.html`, permitindo refresh direto em rotas como `/login`, `/organizador` e `/portaria`.

### Backend No Railway

Configuracoes recomendadas:

Se o Railway usa a raiz do repositorio:

```bash
cd backend && ./mvnw clean package -DskipTests
cd backend && java -jar target/bilheteria-digital-0.0.1-SNAPSHOT.jar
```

Se o Railway usa `backend` como root directory:

```bash
./mvnw clean package -DskipTests
java -jar target/bilheteria-digital-0.0.1-SNAPSHOT.jar
```

Variaveis:

```env
SPRING_PROFILES_ACTIVE=prod
NIXPACKS_JDK_VERSION=21
PORT=8080
DB_URL=jdbc:mysql://HOST:PORT/DATABASE?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bahia
DB_USERNAME=USUARIO
DB_PASSWORD=SENHA
JWT_SECRET=chave-com-pelo-menos-32-caracteres
APP_CORS_ALLOWED_ORIGINS=https://bilheteria-digital.vercel.app
TMDB_ACCESS_TOKEN=...
TICKETMASTER_API_KEY=...
TICKETMASTER_COUNTRY_CODE=BR
STRIPE_SECRET_KEY=sk_test_...
STRIPE_WEBHOOK_SECRET=whsec_...
```

Healthcheck:

```text
/api/health
```

### Banco MySQL No Railway

Use um servico MySQL separado do backend. O backend deve apontar para ele por `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`.

Nao rode MySQL dentro do mesmo container do backend se quiser persistencia. O MySQL gerenciado do Railway persiste os dados entre deploys/restarts.

## Regras De Negocio Importantes

- Cadastro publico cria somente usuario `CLIENTE`.
- Usuarios `ORGANIZADOR` e `PORTARIA` sao criados pelo seed para avaliacao.
- Evento criado pelo organizador nasce como `RASCUNHO`.
- Apenas eventos `PUBLICADO` aparecem nas rotas publicas.
- Evento com assentos gera mapa ao ser publicado.
- Reserva fica `PENDENTE` por 15 minutos.
- Um agendamento roda a cada 60 segundos para expirar reservas vencidas.
- Reserva vencida libera estoque ou assentos.
- Pagamento aprovado marca reserva como `PAGA` e gera ingressos.
- Pagamento recusado marca reserva como `RECUSADA` e libera estoque.
- Cancelamento de evento cancela reservas e ingressos relacionados.
- QR autenticado e assinado com o mesmo segredo JWT do backend.
- A portaria marca o ingresso como `USADO` em update condicional para impedir dupla validacao.

## Limitacoes Conhecidas

- O backend nao carrega `.env` automaticamente; variaveis devem ser configuradas no terminal, IDE ou plataforma.
- O frontend publicado depende de `VITE_API_URL` ter sido configurado antes do build na Vercel.
- O fluxo Stripe completo exige webhook configurado. Sem webhook, o PaymentIntent pode ser confirmado na Stripe, mas a reserva nao muda para `PAGA` no sistema.
- Em producao, a leitura por camera exige HTTPS e permissao do navegador.
- Nao ha recuperacao de senha, envio de ingresso por email, nota fiscal, revenda ou app nativo, pois esses itens foram marcados como fora do escopo no desafio.
- O projeto usa MySQL. Migrar para Supabase/Postgres exigiria adaptar migrations SQL, driver JDBC e tipos especificos.

## Uso De IA

IA foi usada como apoio para:

- Interpretar o documento do desafio.
- Planejar os fluxos de teste.
- Revisar aderencia entre requisitos e funcionalidades.
- Apoiar a escrita e revisao de documentacao.
- Apoiar investigacao de deploy, CORS, Railway, Vercel e Stripe.

As decisoes finais de escopo, organizacao por dominio, regras de reserva/pagamento/portaria, validacao manual e ajustes do projeto foram revisadas diretamente no codigo.

## Checklist Rapido Do Avaliador

1. Acessar `/login`.
2. Entrar como organizador.
3. Criar evento manual ou usando catalogo.
4. Publicar evento.
5. Sair e entrar como cliente.
6. Buscar evento na Home.
7. Reservar ingresso geral.
8. Reservar assento em evento com mapa.
9. Pagar com Stripe em modo teste.
10. Confirmar emissao em `/meus-ingressos`.
11. Abrir link publico do ingresso.
12. Entrar como portaria.
13. Validar QR/codigo.
14. Repetir validacao para conferir `JA_UTILIZADO`.
15. Testar evento errado e codigo invalido.
16. Rodar `.\mvnw.cmd test` no backend.

## Comandos Uteis

```powershell
# Banco local
docker compose up -d
docker compose down
docker logs bilheteria_mysql

# Backend
cd backend
.\mvnw.cmd spring-boot:run
.\mvnw.cmd test
.\mvnw.cmd clean package -DskipTests

# Frontend
cd frontend
npm install
npm run dev
npm run build
npm run lint
```
