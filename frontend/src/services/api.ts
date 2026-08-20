import type {
  ApiErrorBody,
  AuthResponse,
  CatalogoDetalhe,
  CatalogoItem,
  EventoDetalhe,
  EventoFormData,
  EventoResumo,
  Ingresso,
  IngressoCompartilhado,
  PageResponse,
  PaymentIntent,
  Reserva,
  ResultadoPortaria,
  UsuarioLogado,
} from '../types'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

type RequestOptions = {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE'
  body?: unknown
  headers?: Record<string, string>
  auth?: boolean
}

export function getToken() {
  return localStorage.getItem('authToken')
}

export function setToken(token: string) {
  localStorage.setItem('authToken', token)
}

export function clearToken() {
  localStorage.removeItem('authToken')
}

export async function apiRequest<T>(path: string, options: RequestOptions = {}) {
  const headers: Record<string, string> = {
    ...options.headers,
  }
  const token = getToken()

  if (options.body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }

  if (token && options.auth !== false) {
    headers.Authorization = `Bearer ${token}`
  }

  let response: Response

  try {
    response = await fetch(`${API_URL}${path}`, {
      method: options.method || 'GET',
      headers,
      body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
    })
  } catch {
    throw new Error('Nao foi possivel conectar ao backend.')
  }

  const text = await response.text()
  const data = parseResponseBody(text)

  if (!response.ok) {
    throw data || { mensagem: response.statusText }
  }

  return data as T
}

export function getErrorMessage(error: unknown) {
  if (error instanceof Error) {
    return error.message
  }

  if (typeof error === 'object' && error) {
    const body = error as ApiErrorBody
    return body.mensagem || body.message || body.erro || 'Operacao nao concluida.'
  }

  return 'Operacao nao concluida.'
}

export function toQuery(params: Record<string, string | number | undefined | null>) {
  const search = new URLSearchParams()

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      search.set(key, String(value))
    }
  })

  const query = search.toString()
  return query ? `?${query}` : ''
}

export const authApi = {
  login: (email: string, senha: string) =>
    apiRequest<AuthResponse>('/api/auth/login', {
      method: 'POST',
      body: { email, senha },
      auth: false,
    }),
  registrar: (nome: string, email: string, senha: string) =>
    apiRequest<AuthResponse>('/api/auth/registrar', {
      method: 'POST',
      body: { nome, email, senha },
      auth: false,
    }),
  me: () => apiRequest<UsuarioLogado>('/api/usuarios/me'),
}

export const eventosApi = {
  listar: () => apiRequest<EventoResumo[]>('/api/eventos', { auth: false }),
  buscar: (params: Record<string, string | number | undefined>) =>
    apiRequest<PageResponse<EventoResumo>>(`/api/eventos/buscar${toQuery(params)}`, { auth: false }),
  detalhar: (id: number | string) => apiRequest<EventoDetalhe>(`/api/eventos/${id}`, { auth: false }),
}

export const organizadorApi = {
  meusEventos: () => apiRequest<EventoResumo[]>('/api/organizador/eventos'),
  criarEvento: (body: EventoFormData) =>
    apiRequest<EventoResumo>('/api/organizador/eventos', {
      method: 'POST',
      body,
    }),
  publicar: (id: number | string) =>
    apiRequest<EventoResumo>(`/api/organizador/eventos/${id}/publicar`, {
      method: 'POST',
    }),
  cancelar: (id: number | string) =>
    apiRequest<EventoResumo>(`/api/organizador/eventos/${id}/cancelar`, {
      method: 'POST',
    }),
}

export const catalogoApi = {
  buscar: (origem: 'TMDB' | 'TICKETMASTER', q: string) =>
    apiRequest<CatalogoItem[]>(`/api/catalogo/buscar${toQuery({ origem, q })}`),
  detalhar: (origem: 'TMDB' | 'TICKETMASTER', idExterno: string) =>
    apiRequest<CatalogoDetalhe>(`/api/catalogo/${origem}/${idExterno}`),
}

export const reservasApi = {
  criar: (body: { eventoId: number; quantidade?: number | null; assentoIds?: number[] | null }) =>
    apiRequest<Reserva>('/api/reservas', {
      method: 'POST',
      body,
    }),
  buscar: (id: number | string) => apiRequest<Reserva>(`/api/reservas/${id}`),
  minhas: () => apiRequest<Reserva[]>('/api/reservas/minhas'),
}

export const pagamentosApi = {
  paymentIntent: (reservaId: number | string) =>
    apiRequest<PaymentIntent>(`/api/pagamentos/reservas/${reservaId}/payment-intent`, {
      method: 'POST',
    }),
}

export const ingressosApi = {
  meus: () => apiRequest<Ingresso[]>('/api/me/ingressos'),
  compartilhado: (token: string) =>
    apiRequest<IngressoCompartilhado>(`/api/ingressos/compartilhado/${token}`, { auth: false }),
}

export const portariaApi = {
  validar: (eventoId: number, codigo: string) =>
    apiRequest<ResultadoPortaria>('/api/portaria/validar', {
      method: 'POST',
      body: { eventoId, codigo },
    }),
}

function parseResponseBody(text: string) {
  if (!text) {
    return null
  }

  try {
    return JSON.parse(text)
  } catch {
    return text
  }
}
