# Bilheteria Digital :ticket:

Plataforma de eventos e ingressos desenvolvida para o Desafio Elite Dev 2026. O sistema permite que um organizador crie e publique eventos, que clientes reservem e paguem ingressos, e que a portaria valide a entrada por QR Code.

## Decisoes Tecnicas :computer:

**OBS: Gostaria de ter desenvolvido melhor a proposta do sistema completamente, especialmente o frontend e suas interfaces. Foquei mais em priorizar e entregar as funcionalidades em execução e evitei o uso de IA sempre que possível, buscando demonstrar fundamentação conceitual, principalmente na lógica e nas regras de negócio. Sigo confiante e engajado para as possíveis próximas etapas!**

- Escolhi Java para o backend e React para o frontend porque sao stacks robustas, escalaveis e com excelente suporte para aplicacoes web. Tambem sao as tecnologias com que tenho mais dominio.
- Optei por MySQL por ser simples, eficiente para leituras e bastante adequado para operacoes web transacionais como eventos, reservas, pagamentos e ingressos.
- Escolhi Railway para o backend e banco porque ele integra bem com MySQL e simplifica o deploy de uma aplicacao Java a partir do GitHub.
- Modelei a aplicacao como um monolito modular dividido por dominios, evitando controllers e services concentrando responsabilidades demais.
- Mantive controller, service, repository, DTOs e entidades proximos dentro de cada dominio para melhorar legibilidade e facilitar manutencao.
- Usei DTOs para request e response porque evitam expor entidades diretamente e permitem controlar exatamente os dados da API.
- Isolei TMDb e Ticketmaster dentro do dominio de catalogo para nao misturar detalhes de APIs externas com as regras internas de criacao de eventos.
- Tratei concorrencia de reserva com updates condicionais atomicos no banco, verificando o numero de linhas afetadas para impedir venda duplicada de capacidade geral ou assento.
- Usei QR assinado com JWT para dificultar falsificacao de ingresso.
- Versionei o projeto simulando um fluxo de colaboracao real, com branches por modulo, mensagens de commit padronizadas, pull requests, revisao propria de codigo e merge para a branch principal.
---

Projeto publicado (Deploy):

```text
https://bilheteria-digital.vercel.app
```

## Stack :books:

- Backend: Java 21, Spring Boot, Spring Security, JWT, Spring Data JPA, Flyway.
- Frontend: React, Vite, TypeScript, React Router.
- Banco: MySQL 8.
- Pagamento: Stripe em modo teste.
- Deploy: Vercel para o frontend e Railway para backend + MySQL.

## Principais Funcionalidades :computer_mouse:

- Login com tres perfis: `ORGANIZADOR`, `CLIENTE` e `PORTARIA`.
- Cadastro publico de clientes.
- Busca e listagem de eventos publicados.
- Criacao de eventos manualmente ou a partir de catalogo externo.
- Integracao com TMDb e Ticketmaster.
- Reserva por quantidade para evento de pista/capacidade geral.
- Reserva por mapa de assentos para evento de cinema/teatro.
- Pagamento com Stripe usando PaymentIntent.
- Webhook Stripe para confirmar ou recusar pagamento.
- Emissao de ingresso com QR Code.
- Link publico de compartilhamento do ingresso.
- Tela de portaria com leitura por camera e digitacao manual.
- Validacao com retorno claro: `VALIDO`, `INVALIDO`, `JA_UTILIZADO` ou `EVENTO_ERRADO`.

## Dados De Teste :lock:

Todos os usuarios abaixo usam a senha abaixo, implantei os logins de cada Role de forma instantânea para facilitar a realização dos testes:

```text
123456
```

| Perfil | Email |
| --- | --- |
| Organizador | `organizador@teste.com` |
| Cliente 1 | `cliente1@teste.com` |
| Cliente 2 | `cliente2@teste.com` |
| Portaria | `portaria@teste.com` |

Eventos semeados pelo Flyway:

- `Show Teste Pista`: evento de capacidade geral.
- `Cinema Teste Assentos`: evento com mapa de assentos.

## Como Testar Pelo Site :bookmark_tabs:

### 1. Login

1. Acesse `https://bilheteria-digital.vercel.app/login`.
2. Use os botoes de perfil de teste ou digite email e senha manualmente.
3. O sistema redireciona de acordo com o perfil:
   - Organizador: painel de eventos.
   - Cliente: home de eventos.
   - Portaria: tela de validacao.

### 2. Fluxo Do Organizador

1. Entre como `organizador@teste.com`.
2. Acesse `Organizador`.
3. Clique em `Criar evento`.
4. Crie manualmente ou busque dados externos em TMDb/Ticketmaster.
5. Salve o evento.
6. Publique para ele aparecer na home.
7. Opcionalmente cancele um evento para testar devolucao de estoque.

### 3. Fluxo Do Cliente

1. Entre como `cliente1@teste.com`.
2. Acesse a home de eventos.
3. Abra o `Show Teste Pista` para testar reserva por quantidade.
4. Abra o `Cinema Teste Assentos` para testar reserva por assento.
5. Clique em `Reservar agora`.
6. Na tela de pagamento, use cartao teste da Stripe:

```text
4242 4242 4242 4242
```

Use uma data futura, CVC qualquer e CEP valido.

Resultado esperado:

- A reserva muda para paga.
- O ingresso aparece em `Meus ingressos`.
- O ingresso mostra QR Code e link publico.

Para testar recusa, use:

```text
4000 0000 0000 0002
```

### 4. Fluxo Da Portaria Com Camera

1. Entre como `portaria@teste.com`.
2. Acesse a tela `Portaria`.
3. Libere o uso da camera quando o navegador pedir permissao.
4. Na parte superior da tela, selecione qual evento sera validado:
   - `Show Teste Pista`, para evento de pista/capacidade geral.
   - `Cinema Teste Assentos`, para evento com assentos.
5. Em outra aba ou dispositivo, entre como cliente e abra `Meus ingressos`.
6. Selecione o ingresso que deseja validar.
7. Clique no link publico do ingresso.
8. Com a tela publica do ingresso aberta, aponte a camera da portaria para o QR Code e escaneie.
9. Tambem e possivel copiar o codigo e colar manualmente na tela da portaria.

Resultados esperados:

- Primeiro scan do ingresso correto: `VALIDO`.
- Segundo scan do mesmo ingresso: `JA_UTILIZADO`.
- Ingresso de outro evento: `EVENTO_ERRADO`.
- Codigo inexistente ou adulterado: `INVALIDO`.

## Como Rodar Localmente :clipboard:

### Banco

```powershell
docker compose up -d
```

Banco local:

```text
host: localhost
porta: 3306
database: bilheteria_digital
usuario: bilheteria_user
senha: 102030
```

### Backend

Configure variaveis de ambiente no terminal ou na IDE:

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
$env:STRIPE_WEBHOOK_SECRET="whsec_seu_webhook_secret"
```

Rode:

```powershell
.\mvnw.cmd spring-boot:run
```

Healthcheck:

```text
http://localhost:8080/api/health
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

### Frontend

Crie `frontend/.env`:

```env
VITE_API_URL=http://localhost:8080
VITE_STRIPE_PUBLISHABLE_KEY=pk_test_sua_chave_publicavel
```

Rode:

```powershell
cd frontend
npm install
npm run dev
```

Frontend local:

```text
http://localhost:5173
```

## Stripe :credit_card:

Em desenvolvimento local, use Stripe CLI para encaminhar webhooks:

```powershell
stripe login
stripe listen --events payment_intent.succeeded,payment_intent.payment_failed --forward-to localhost:8080/api/webhooks/stripe
```

Em producao, configure no Stripe Dashboard:
```

## Rotas Principais

Frontend:

- `/`: eventos publicados.
- `/login`: login.
- `/cadastro`: cadastro de cliente.
- `/eventos/:id`: detalhe e reserva.
- `/pagamento/:reservaId`: pagamento Stripe.
- `/minhas-reservas`: reservas do cliente.
- `/meus-ingressos`: ingressos do cliente.
- `/ingressos/compartilhado/:token`: ingresso publico.
- `/organizador`: painel do organizador.
- `/organizador/novo`: criacao de evento.
- `/portaria`: validacao de ingresso.

Backend:

- `POST /api/auth/login`
- `POST /api/auth/registrar`
- `GET /api/eventos`
- `GET /api/eventos/buscar`
- `GET /api/eventos/{id}`
- `GET /api/catalogo/buscar`
- `POST /api/organizador/eventos`
- `POST /api/organizador/eventos/{id}/publicar`
- `POST /api/organizador/eventos/{id}/cancelar`
- `POST /api/reservas`
- `GET /api/reservas/minhas`
- `POST /api/pagamentos/reservas/{reservaId}/payment-intent`
- `POST /api/webhooks/stripe`
- `GET /api/me/ingressos`
- `GET /api/ingressos/compartilhado/{token}`
- `POST /api/portaria/validar`

## Testes Automatizados

Rodar testes do backend:

```powershell
cd backend
.\mvnw.cmd test
```

Cobertura principal:

- autenticacao e permissoes;
- criacao, publicacao e cancelamento de eventos;
- reserva por quantidade;
- reserva por assento;
- expiracao e devolucao de estoque;
- pagamento aprovado e recusado;
- validacao da portaria.

## Limitacoes Conhecidas :warning:

- O cadastro publico cria apenas usuario `CLIENTE`; organizador e portaria entram pelos usuarios semeados.
- O fluxo de pagamento depende do webhook Stripe estar configurado corretamente.
- A camera da portaria exige HTTPS no deploy ou permissao do navegador em ambiente local.
- Nao foram implementados recuperacao de senha, envio de ingresso por email, nota fiscal, revenda ou app nativo, pelo fator tempo, porém seria um prazer implementar.

## Agradecimentos :tada:

Agradeço muito pela oportunidade de participar do desafio até aqui! Foi um prazer desenvolver a aplicação, principalmente porque gosto de desafios que me permitem explorar novas soluções, integrar funcionalidades e aprender coisas novas no processo, foi uma experiência surreal para mim. Mesmo com o tempo limitado e com vários pontos que eu gostaria de ter refinado ainda mais fiquei muito satisfeito com o que consegui construir e demonstrar. Sigo empolgado, engajado e confiante para os próximos passos. Obrigado pela oportunidade!

---
Desenvolvido por **Adson Mozart Santos Paixão** :black_nib:
