import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { ErrorState, LoadingState } from '../components/StateViews'
import { getErrorMessage, pagamentosApi, reservasApi } from '../services/api'
import type { Pagamento, Reserva } from '../types'
import { formatCurrency, formatDateTime, statusLabel } from '../utils/format'

export function PagamentoPage() {
  const { reservaId } = useParams()
  const [reserva, setReserva] = useState<Reserva | null>(null)
  const [pagamento, setPagamento] = useState<Pagamento | null>(null)
  const [loading, setLoading] = useState(true)
  const [processing, setProcessing] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  useEffect(() => {
    if (!reservaId) {
      return
    }

    reservasApi
      .buscar(reservaId)
      .then(setReserva)
      .catch((erro) => setError(getErrorMessage(erro)))
      .finally(() => setLoading(false))
  }, [reservaId])

  async function criarPagamento() {
    if (!reservaId) {
      return null
    }

    if (pagamento) {
      return pagamento
    }

    const criado = await pagamentosApi.criar(reservaId)
    setPagamento(criado)
    return criado
  }

  async function concluir(decisao: 'aprovar' | 'recusar') {
    setProcessing(true)
    setError('')
    setMessage('')

    try {
      const pagamentoAtual = await criarPagamento()
      if (!pagamentoAtual) {
        return
      }

      const atualizado =
        decisao === 'aprovar'
          ? await pagamentosApi.aprovar(pagamentoAtual.id)
          : await pagamentosApi.recusar(pagamentoAtual.id)

      setPagamento(atualizado)
      setMessage(decisao === 'aprovar' ? 'Pagamento aprovado.' : 'Pagamento recusado.')

      if (reservaId) {
        setReserva(await reservasApi.buscar(reservaId))
      }
    } catch (erro) {
      setError(getErrorMessage(erro))
    } finally {
      setProcessing(false)
    }
  }

  if (loading) {
    return <LoadingState />
  }

  if (!reserva) {
    return <ErrorState message={error || 'Reserva nao encontrada.'} />
  }

  return (
    <main className="page-shell narrow-page">
      <section className="payment-card">
        <span className="eyebrow">Pagamento simulado</span>
        <h1>{reserva.tituloEvento}</h1>
        <dl className="detail-list">
          <div>
            <dt>Reserva</dt>
            <dd>#{reserva.id}</dd>
          </div>
          <div>
            <dt>Status</dt>
            <dd>{statusLabel(reserva.status)}</dd>
          </div>
          <div>
            <dt>Validade</dt>
            <dd>{formatDateTime(reserva.validade)}</dd>
          </div>
          <div>
            <dt>Total</dt>
            <dd>{formatCurrency(reserva.valorTotal)}</dd>
          </div>
        </dl>

        {pagamento && (
          <p>
            Pagamento #{pagamento.id}: {statusLabel(pagamento.status)}
          </p>
        )}

        {message && <p className="message">{message}</p>}
        {error && <p className="error">{error}</p>}

        <div className="split-actions">
          <button type="button" onClick={() => concluir('aprovar')} disabled={processing || reserva.status !== 'PENDENTE'}>
            Aprovar pagamento
          </button>
          <button
            type="button"
            className="danger-button"
            onClick={() => concluir('recusar')}
            disabled={processing || reserva.status !== 'PENDENTE'}
          >
            Recusar pagamento
          </button>
        </div>

        <Link to="/meus-ingressos">Ver meus ingressos</Link>
      </section>
    </main>
  )
}
