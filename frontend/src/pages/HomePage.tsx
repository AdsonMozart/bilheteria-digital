import { type FormEvent, useEffect, useMemo, useState } from 'react'
import { EventCard } from '../components/EventCard'
import { EmptyState, ErrorState, LoadingState } from '../components/StateViews'
import { eventosApi, getErrorMessage } from '../services/api'
import type { EventoResumo } from '../types'
import { formatCurrency } from '../utils/format'

type Filters = {
  titulo: string
  tipo: string
  local: string
  dataInicio: string
  dataFim: string
  sort: string
}

const initialFilters: Filters = {
  titulo: '',
  tipo: '',
  local: '',
  dataInicio: '',
  dataFim: '',
  sort: 'dataHora,asc',
}

export function HomePage() {
  const [eventos, setEventos] = useState<EventoResumo[]>([])
  const [filters, setFilters] = useState(initialFilters)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    async function loadInitialEventos() {
      setLoading(true)
      setError('')

      try {
        const response = await eventosApi.buscar({
          ...initialFilters,
          page: 0,
          size: 24,
        })
        setEventos(response.content)
      } catch (erro) {
        setError(getErrorMessage(erro))
      } finally {
        setLoading(false)
      }
    }

    loadInitialEventos()
  }, [])

  const destaques = useMemo(() => eventos.slice(0, 3), [eventos])
  const totalVendidos = useMemo(() => eventos.reduce((total, evento) => total + evento.capacidadeVendida, 0), [eventos])
  const totalCapacidade = useMemo(() => eventos.reduce((total, evento) => total + evento.capacidade, 0), [eventos])
  const menorPreco = useMemo(() => {
    if (eventos.length === 0) {
      return 0
    }

    return Math.min(...eventos.map((evento) => evento.preco))
  }, [eventos])

  async function loadEventos(params: Filters) {
    setLoading(true)
    setError('')

    try {
      const hasFilters = Object.values(params).some(Boolean)
      if (hasFilters) {
        const response = await eventosApi.buscar({
          ...params,
          page: 0,
          size: 24,
        })
        setEventos(response.content)
      } else {
        setEventos(await eventosApi.listar())
      }
    } catch (erro) {
      setError(getErrorMessage(erro))
    } finally {
      setLoading(false)
    }
  }

  function handleSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    loadEventos(filters)
  }

  function updateFilter(key: keyof Filters, value: string) {
    setFilters((current) => ({ ...current, [key]: value }))
  }

  return (
    <main>
      <section className="hero-search">
        <div className="hero-copy">
          <span className="eyebrow">MozarTickets 01 - tempo real</span>
          <h1>Bilhetes que colocam sua noite</h1>
          <p>Busque shows, filmes e experiencias publicadas pelos organizadores com leitura clara de data, local e preco.</p>
        </div>

        <form className="search-panel" onSubmit={handleSearch}>
          <input
            value={filters.titulo}
            onChange={(event) => updateFilter('titulo', event.target.value)}
            placeholder="Buscar artistas, filmes ou eventos"
          />
          <select value={filters.tipo} onChange={(event) => updateFilter('tipo', event.target.value)}>
            <option value="">Todos os tipos</option>
            <option value="SHOW">Shows</option>
            <option value="FILME">Filmes</option>
          </select>
          <input
            value={filters.local}
            onChange={(event) => updateFilter('local', event.target.value)}
            placeholder="Cidade ou local"
          />
          <button type="submit">Buscar</button>
        </form>

        <div className="hero-console-wrap" aria-hidden="true">
          <div className="hero-console-shadow" />
          <div className="hero-console">
            <div className="hero-console-inner">
              <div className="console-status">
                <span>Operacao ao vivo</span>
                <span>{eventos.length} eventos monitorados</span>
              </div>
              <div className="console-copy">
                <p>Tudo em movimento, sem perder nenhum acesso.</p>
                <div className="metric-chip-row">
                  <span className="metric-chip">{totalVendidos} ingressos vendidos</span>
                  <span className="metric-chip">{totalCapacidade} lugares mapeados</span>
                  <span className="metric-chip">A partir de {formatCurrency(menorPreco)}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="page-section">
        <div className="section-heading">
          <div>
            <span className="eyebrow">Em destaque</span>
            <h2>Eventos publicados</h2>
          </div>
        </div>

        <div className="product-mosaic">
          <article className="product-frame">
            <span className="section-icon">01</span>
            <h3>Vendas no pulso</h3>
            <p>Acompanhe disponibilidade, preco e ocupacao de cada evento publicado.</p>
            <div className="mini-sales-row mini-sales-row-hot">
              <span>Ingressos vendidos</span>
              <strong>{totalVendidos}</strong>
            </div>
            <div className="mini-sales-row">
              <span>Capacidade total</span>
              <strong>{totalCapacidade}</strong>
            </div>
          </article>

          <article className="product-frame">
            <span className="section-icon">02</span>
            <h3>Publico real</h3>
            <p>Eventos, reservas, ingressos e portaria continuam operando pelos mesmos fluxos do sistema.</p>
            <div className="checkline">Busca por shows e filmes</div>
            <div className="checkline">Reserva com mapa de assentos</div>
            <div className="checkline">Validacao por QR Code</div>
          </article>

          <article className="product-frame mosaic-tall">
            <span className="section-icon">03</span>
            <h3>Entrada sem friccao</h3>
            <p>Portaria com camera, codigo manual e retorno visual claro para valido, invalido, usado ou evento errado.</p>
          </article>

          <article className="product-frame mosaic-wide">
            <span className="section-icon">04</span>
            <h3>Sessoes e lotes</h3>
            <p>O organizador cria eventos manualmente ou importa dados externos para abrir vendas em poucos passos.</p>
          </article>
        </div>

        {loading && <LoadingState />}
        {error && <ErrorState message={error} />}
        {!loading && !error && destaques.length > 0 && (
          <div className="featured-grid">
            {destaques.map((evento) => (
              <EventCard evento={evento} key={evento.id} />
            ))}
          </div>
        )}
      </section>

      <section className="page-section event-browser">
        <aside className="filters-panel">
          <h2>Filtros</h2>
          <label>
            Data inicial
            <input
              type="date"
              value={filters.dataInicio}
              onChange={(event) => updateFilter('dataInicio', event.target.value)}
            />
          </label>
          <label>
            Data final
            <input
              type="date"
              value={filters.dataFim}
              onChange={(event) => updateFilter('dataFim', event.target.value)}
            />
          </label>
          <label>
            Ordenacao
            <select value={filters.sort} onChange={(event) => updateFilter('sort', event.target.value)}>
              <option value="dataHora,asc">Data mais proxima</option>
              <option value="preco,asc">Menor preco</option>
              <option value="preco,desc">Maior preco</option>
            </select>
          </label>
          <button type="button" onClick={() => loadEventos(filters)}>
            Aplicar filtros
          </button>
          <button
            type="button"
            className="ghost-button"
            onClick={() => {
              setFilters(initialFilters)
              loadEventos(initialFilters)
            }}
          >
            Limpar
          </button>
        </aside>

        <div>
          <div className="section-heading">
            <div>
              <span className="eyebrow">Proximos</span>
              <h2>Todos os eventos</h2>
            </div>
            <span>{eventos.length} resultados</span>
          </div>

          {!loading && !error && eventos.length === 0 && (
            <EmptyState title="Nenhum evento encontrado" text="Ajuste os filtros e tente novamente." />
          )}

          <div className="event-list">
            {eventos.map((evento) => (
              <EventCard evento={evento} key={evento.id} compact />
            ))}
          </div>
        </div>
      </section>
    </main>
  )
}
