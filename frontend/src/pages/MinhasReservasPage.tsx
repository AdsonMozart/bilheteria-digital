import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { EmptyState, ErrorState, LoadingState } from '../components/StateViews'
import { getErrorMessage, reservasApi } from '../services/api'
import type { Reserva } from '../types'
import { formatCurrency, formatDateTime, statusLabel } from '../utils/format'

export function MinhasReservasPage() {
  const [reservas, setReservas] = useState<Reserva[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    reservasApi
      .minhas()
      .then(setReservas)
      .catch((erro) => setError(getErrorMessage(erro)))
      .finally(() => setLoading(false))
  }, [])

  return (
    <main className="page-shell">
      <div className="section-heading">
        <div>
          <span className="eyebrow">Cliente</span>
          <h1>Minhas reservas</h1>
        </div>
      </div>

      {loading && <LoadingState />}
      {error && <ErrorState message={error} />}
      {!loading && !error && reservas.length === 0 && (
        <EmptyState title="Voce ainda nao tem reservas" text="Escolha um evento publicado para iniciar." />
      )}

      <div className="table-list">
        {reservas.map((reserva) => (
          <article className="row-card" key={reserva.id}>
            <div>
              <span className="eyebrow">Reserva #{reserva.id}</span>
              <h2>{reserva.tituloEvento}</h2>
              <p>{formatDateTime(reserva.dataCriacao)}</p>
            </div>
            <div>
              <strong>{formatCurrency(reserva.valorTotal)}</strong>
              <span>{statusLabel(reserva.status)}</span>
            </div>
            <div className="row-actions">
              <Link to={`/eventos/${reserva.eventoId}`}>Evento</Link>
              {reserva.status === 'PENDENTE' && <Link to={`/pagamento/${reserva.id}`}>Pagar</Link>}
            </div>
          </article>
        ))}
      </div>
    </main>
  )
}
