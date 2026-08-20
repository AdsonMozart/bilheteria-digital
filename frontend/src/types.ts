export type UserRole = 'CLIENTE' | 'ORGANIZADOR' | 'PORTARIA'

export type UsuarioLogado = {
  id: number
  nome: string
  email: string
  nivelAcesso: UserRole
}

export type AuthResponse = {
  token: string
}

export type EventoResumo = {
  id: number
  titulo: string
  tipo: 'SHOW' | 'FILME'
  status: string
  nomeLocal: string
  dataHora: string
  tipoCapacidade: 'GERAL' | 'ASSENTOS'
  preco: number
  capacidade: number
  capacidadeVendida: number
}

export type Assento = {
  id: number
  setor: string
  fileira: string
  numero: number
  status: 'DISPONIVEL' | 'RESERVADO' | 'VENDIDO'
}

export type EventoDetalhe = EventoResumo & {
  descricao?: string
  urlImagem?: string
  enderecoLocal?: string
  assentos: Assento[]
}

export type Reserva = {
  id: number
  eventoId: number
  tituloEvento: string
  clienteId: number
  quantidade: number
  valorTotal: number
  status: string
  validade: string
  dataCriacao: string
}

export type Pagamento = {
  id: number
  reservaId: number
  status: string
  valor: number
  dataCriacao: string
}

export type PaymentIntent = {
  pagamentoId: number
  reservaId: number
  stripePaymentIntentId: string
  clientSecret: string
}

export type Ingresso = {
  id: number
  reservaId: number
  eventoId: number
  tituloEvento: string
  codigo: string
  assinaturaQr: string
  tokenCompartilhamento: string
  status: string
  validadoEm?: string
  dataCriacao: string
}

export type IngressoCompartilhado = {
  id: number
  eventoId: number
  tituloEvento: string
  codigo: string
  status: string
  validadoEm?: string
  dataCriacao: string
}

export type ResultadoPortaria = {
  resultado: 'VALIDO' | 'INVALIDO' | 'JA_UTILIZADO' | 'EVENTO_ERRADO'
  mensagem: string
  ingressoId?: number
  eventoId?: number
  codigo?: string
}

export type CatalogoItem = {
  origem: 'TMDB' | 'TICKETMASTER'
  idExterno: string
  titulo: string
  tipo: 'SHOW' | 'FILME'
  descricao?: string
  urlImagem?: string
  dataLancamento?: string
  avaliacao?: number
  totalVotos?: number
}

export type CatalogoDetalhe = CatalogoItem & {
  duracaoMinutos?: number
  generos?: string[]
}

export type EventoFormData = {
  origemExterna: 'MANUAL' | 'TMDB' | 'TICKETMASTER'
  idExterno?: string | null
  titulo: string
  tipo: 'SHOW' | 'FILME'
  descricao?: string | null
  urlImagem?: string | null
  nomeLocal: string
  enderecoLocal?: string | null
  dataHora: string
  tipoCapacidade: 'GERAL' | 'ASSENTOS'
  preco: number
  capacidade: number
}

export type PageResponse<T> = {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

export type ApiErrorBody = {
  status?: number
  erro?: string
  mensagem?: string
  message?: string
  campos?: Record<string, string>
}
