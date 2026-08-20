import { QRCodeSVG } from 'qrcode.react'
import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { ErrorState, LoadingState } from '../components/StateViews'
import { getErrorMessage, ingressosApi } from '../services/api'
import type { IngressoCompartilhado } from '../types'
import { formatDateTime, statusLabel } from '../utils/format'

export function IngressoCompartilhadoPage() {
  const { token } = useParams()
  const [ingresso, setIngresso] = useState<IngressoCompartilhado | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!token) {
      return
    }

    ingressosApi
      .compartilhado(token)
      .then(setIngresso)
      .catch((erro) => setError(getErrorMessage(erro)))
      .finally(() => setLoading(false))
  }, [token])

  if (!token) {
    return <ErrorState message="Token nao informado." />
  }

  if (loading) {
    return <LoadingState />
  }

  if (!ingresso) {
    return <ErrorState message={error || 'Ingresso nao encontrado.'} />
  }

  return (
    <main className="ticket-share-page">
      <section className="ticket-card public-ticket">
        <span className="eyebrow">Ingresso compartilhado</span>
        <h1>{ingresso.tituloEvento}</h1>
        <QRCodeSVG value={ingresso.codigo} size={180} />
        <dl className="detail-list">
          <div>
            <dt>Codigo</dt>
            <dd>{ingresso.codigo}</dd>
          </div>
          <div>
            <dt>Status</dt>
            <dd>{statusLabel(ingresso.status)}</dd>
          </div>
          <div>
            <dt>Criado em</dt>
            <dd>{formatDateTime(ingresso.dataCriacao)}</dd>
          </div>
        </dl>
        <Link to={`/eventos/${ingresso.eventoId}`}>Ver evento</Link>
      </section>
    </main>
  )
}
