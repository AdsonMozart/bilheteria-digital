import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { EmptyState, ErrorState, LoadingState } from '../components/StateViews'
import { getErrorMessage, organizadorApi } from '../services/api'
import type { EventoResumo } from '../types'
import { formatCurrency, formatDateTime, statusLabel } from '../utils/format'

export function OrganizadorPage() {
  const [eventos, setEventos] = useState<EventoResumo[]>([])
  const [loading, setLoading] = useState(true)
  const [actionLoading, setActionLoading] = useState<number | null>(null)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  useEffect(() => {
    let active = true

    organizadorApi
      .meusEventos()
      .then((lista) => {
        if (active) {
          setEventos(lista)
        }
      })
      .catch((erro) => {
        if (active) {
          setError(getErrorMessage(erro))
        }
      })
      .finally(() => {
        if (active) {
          setLoading(false)
        }
      })

    return () => {
      active = false
    }
  }, [])

  async function loadEventos() {
    setLoading(true)
    setError('')

    try {
      setEventos(await organizadorApi.meusEventos())
    } catch (erro) {
      setError(getErrorMessage(erro))
    } finally {
      setLoading(false)
    }
  }

  async function changeStatus(id: number, action: 'publicar' | 'cancelar') {
    setActionLoading(id)
    setMessage('')
    setError('')

    try {
      if (action === 'publicar') {
        await organizadorApi.publicar(id)
        setMessage('Evento publicado.')
      } else {
        await organizadorApi.cancelar(id)
        setMessage('Evento cancelado.')
      }

      await loadEventos()
    } catch (erro) {
      setError(getErrorMessage(erro))
    } finally {
      setActionLoading(null)
    }
  }

  return (
    <main className="page-shell">
      <div className="section-heading">
        <div>
          <span className="eyebrow">Organizador</span>
          <h1>Meus eventos</h1>
        </div>
        <Link to="/organizador/novo" className="primary-button">
          Criar evento
        </Link>
      </div>

      {message && <p className="message">{message}</p>}
      {error && <ErrorState message={error} />}
      {loading && <LoadingState />}
      {!loading && !error && eventos.length === 0 && (
        <EmptyState title="Nenhum evento criado" text="Crie um evento manualmente ou a partir do catalogo." />
      )}

      <div className="table-list">
        {eventos.map((evento) => (
          <article className="row-card" key={evento.id}>
            <div>
              <span className="eyebrow">{evento.tipo}</span>
              <h2>{evento.titulo}</h2>
              <p>{formatDateTime(evento.dataHora)} - {evento.nomeLocal}</p>
            </div>
            <div>
              <strong>{formatCurrency(evento.preco)}</strong>
              <span>{statusLabel(evento.status)}</span>
            </div>
            <div>
              <span>{evento.capacidadeVendida}/{evento.capacidade} vendidos</span>
              <span>{evento.tipoCapacidade}</span>
            </div>
            <div className="row-actions">
              <Link to={`/eventos/${evento.id}`}>Ver</Link>
              {evento.status === 'RASCUNHO' && (
                <button type="button" onClick={() => changeStatus(evento.id, 'publicar')} disabled={actionLoading === evento.id}>
                  Publicar
                </button>
              )}
              {evento.status !== 'CANCELADO' && (
                <button
                  type="button"
                  className="danger-button"
                  onClick={() => changeStatus(evento.id, 'cancelar')}
                  disabled={actionLoading === evento.id}
                >
                  Cancelar
                </button>
              )}
            </div>
          </article>
        ))}
      </div>
    </main>
  )
}
