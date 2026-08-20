import { type FormEvent, useEffect, useMemo, useState } from 'react'
import { EventCard } from '../components/EventCard'
import { EmptyState, ErrorState, LoadingState } from '../components/StateViews'
import { eventosApi, getErrorMessage } from '../services/api'
import type { EventoResumo } from '../types'

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
          <span className="eyebrow">Eventos e ingressos</span>
          <h1>Encontre seu proximo evento</h1>
          <p>Shows, filmes e experiencias publicadas pelos organizadores.</p>
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
      </section>

      <section className="page-section">
        <div className="section-heading">
          <div>
            <span className="eyebrow">Em destaque</span>
            <h2>Eventos publicados</h2>
          </div>
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
