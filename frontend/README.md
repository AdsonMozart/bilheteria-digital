# Frontend Bilheteria Digital

SPA em React, Vite e TypeScript para os fluxos do desafio:

- navegacao, busca e filtros de eventos publicados;
- reserva por quantidade ou mapa de assentos;
- pagamento simulado aprovado ou recusado;
- area de reservas e ingressos com QR;
- compartilhamento publico de ingresso;
- painel do organizador com catalogo externo, criacao, publicacao e cancelamento;
- tela de portaria com leitura de QR por camera e digitacao manual.

## Rodar Localmente

Crie `frontend/.env`:

```env
VITE_API_URL=http://localhost:8080
VITE_STRIPE_PUBLISHABLE_KEY=pk_test_sua_chave_publicavel_de_teste
```

Instale e execute:

```powershell
npm install
npm run dev
```

URL local:

```text
http://localhost:5173
```

## Usuarios De Teste

Senha para todos:

```text
123456
```

Perfis:

```text
organizador@teste.com
cliente1@teste.com
cliente2@teste.com
portaria@teste.com
```

## Deploy Na Vercel

Configure o projeto apontando para a pasta `frontend`.

Build command:

```text
npm run build
```

Output directory:

```text
dist
```

Variavel obrigatoria:

```text
VITE_API_URL=https://url-do-backend
```

O arquivo `vercel.json` ja redireciona todas as rotas para `index.html`, permitindo refresh direto em rotas como `/eventos/1` e `/portaria`.
