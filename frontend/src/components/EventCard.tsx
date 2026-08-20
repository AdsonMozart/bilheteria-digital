import { Link } from 'react-router-dom'
import type { EventoDetalhe, EventoResumo } from '../types'
import { formatCurrency, formatDateTime } from '../utils/format'

type EventCardProps = {
  evento: EventoResumo | EventoDetalhe
  compact?: boolean
}

export function EventCard({ evento, compact }: EventCardProps) {
  const image = 'urlImagem' in evento ? evento.urlImagem : undefined
  const disponiveis = Math.max(evento.capacidade - evento.capacidadeVendida, 0)

  return (
    <article className={compact ? 'event-card event-card-compact' : 'event-card'}>
      <Link to={`/eventos/${evento.id}`} className="event-image" aria-label={evento.titulo}>
        {image ? <img src={image} alt="" /> : <span>{evento.tipo}</span>}
      </Link>

      <div className="event-card-body">
        <div className="event-meta">
          <span>{formatDateTime(evento.dataHora)}</span>
          <span>{evento.tipo}</span>
        </div>
        <h3>
          <Link to={`/eventos/${evento.id}`}>{evento.titulo}</Link>
        </h3>
        <p>{evento.nomeLocal}</p>
        <div className="event-footer">
          <strong>{formatCurrency(evento.preco)}</strong>
          <span>{disponiveis} disponiveis</span>
        </div>
      </div>
    </article>
  )
}
