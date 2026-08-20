import { QRCodeSVG } from 'qrcode.react'
import { useEffect, useMemo, useState } from 'react'
import { EmptyState, ErrorState, LoadingState } from '../components/StateViews'
import { getErrorMessage, ingressosApi } from '../services/api'
import type { Ingresso } from '../types'
import { formatDateTime, statusLabel } from '../utils/format'

export function MeusIngressosPage() {
  const [ingressos, setIngressos] = useState<Ingresso[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const origin = useMemo(() => window.location.origin, [])

  useEffect(() => {
    ingressosApi
      .meus()
      .then(setIngressos)
      .catch((erro) => setError(getErrorMessage(erro)))
      .finally(() => setLoading(false))
  }, [])

  async function copyLink(token: string) {
    await navigator.clipboard.writeText(`${origin}/ingressos/compartilhado/${token}`)
  }

  return (
    <main className="page-shell">
      <div className="section-heading">
        <div>
          <span className="eyebrow">Cliente</span>
          <h1>Meus ingressos</h1>
        </div>
      </div>

      {loading && <LoadingState />}
      {error && <ErrorState message={error} />}
      {!loading && !error && ingressos.length === 0 && (
        <EmptyState title="Nenhum ingresso emitido" text="Finalize um pagamento aprovado para gerar ingressos." />
      )}

      <div className="ticket-grid">
        {ingressos.map((ingresso) => {
          const shareUrl = `${origin}/ingressos/compartilhado/${ingresso.tokenCompartilhamento}`

          return (
            <article className="ticket-card" key={ingresso.id}>
              <div>
                <span className="eyebrow">Ingresso #{ingresso.id}</span>
                <h2>{ingresso.tituloEvento}</h2>
                <p>{statusLabel(ingresso.status)}</p>
              </div>
              <QRCodeSVG value={ingresso.assinaturaQr} size={180} />
              <dl className="detail-list">
                <div>
                  <dt>Codigo</dt>
                  <dd>{ingresso.codigo}</dd>
                </div>
                <div>
                  <dt>Reserva</dt>
                  <dd>#{ingresso.reservaId}</dd>
                </div>
                <div>
                  <dt>Criado em</dt>
                  <dd>{formatDateTime(ingresso.dataCriacao)}</dd>
                </div>
              </dl>
              <div className="ticket-actions">
                <a href={shareUrl} target="_blank" rel="noreferrer">
                  Abrir link publico
                </a>
                <button type="button" className="ghost-button" onClick={() => copyLink(ingresso.tokenCompartilhamento)}>
                  Copiar link
                </button>
              </div>
            </article>
          )
        })}
      </div>
    </main>
  )
}
