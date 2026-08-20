import { type FormEvent, useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { SeatMap } from '../components/SeatMap'
import { ErrorState, LoadingState } from '../components/StateViews'
import { useAuth } from '../context/useAuth'
import { eventosApi, getErrorMessage, reservasApi } from '../services/api'
import type { EventoDetalhe } from '../types'
import { formatCurrency, formatDateTime } from '../utils/format'

export function EventoDetalhePage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const [evento, setEvento] = useState<EventoDetalhe | null>(null)
  const [quantidade, setQuantidade] = useState(1)
  const [assentoIds, setAssentoIds] = useState<number[]>([])
  const [loading, setLoading] = useState(true)
  const [reserving, setReserving] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!id) {
      return
    }

    eventosApi
      .detalhar(id)
      .then(setEvento)
      .catch((erro) => setError(getErrorMessage(erro)))
      .finally(() => setLoading(false))
  }, [id])

  const disponiveis = useMemo(() => {
    if (!evento) {
      return 0
    }

    return Math.max(evento.capacidade - evento.capacidadeVendida, 0)
  }, [evento])

  async function handleReserva(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!evento) {
      return
    }

    if (!user) {
      navigate('/login')
      return
    }

    setReserving(true)
    setError('')

    try {
      const reserva = await reservasApi.criar({
        eventoId: evento.id,
        quantidade: evento.tipoCapacidade === 'GERAL' ? quantidade : null,
        assentoIds: evento.tipoCapacidade === 'ASSENTOS' ? assentoIds : null,
      })
      navigate(`/pagamento/${reserva.id}`)
    } catch (erro) {
      setError(getErrorMessage(erro))
    } finally {
      setReserving(false)
    }
  }

  if (loading) {
    return <LoadingState />
  }

  if (!evento) {
    return <ErrorState message={error || 'Evento nao encontrado.'} />
  }

  return (
    <main>
      <section className="event-detail-hero">
        <div className="detail-media">
          {evento.urlImagem ? <img src={evento.urlImagem} alt="" /> : <span>{evento.tipo}</span>}
        </div>
        <div className="detail-info">
          <span className="eyebrow">{evento.tipo}</span>
          <h1>{evento.titulo}</h1>
          <p>{evento.descricao || 'Evento publicado na plataforma.'}</p>
          <dl className="detail-list">
            <div>
              <dt>Data</dt>
              <dd>{formatDateTime(evento.dataHora)}</dd>
            </div>
            <div>
              <dt>Local</dt>
              <dd>{evento.nomeLocal}</dd>
            </div>
            <div>
              <dt>Endereco</dt>
              <dd>{evento.enderecoLocal || 'Endereco nao informado'}</dd>
            </div>
            <div>
              <dt>Preco</dt>
              <dd>{formatCurrency(evento.preco)}</dd>
            </div>
          </dl>
        </div>
      </section>

      <section className="checkout-section">
        <div>
          <span className="eyebrow">Reserva</span>
          <h2>Escolha seu ingresso</h2>
          <p>{disponiveis} lugares disponiveis.</p>
        </div>

        <form className="reservation-panel" onSubmit={handleReserva}>
          {evento.tipoCapacidade === 'GERAL' ? (
            <label>
              Quantidade
              <input
                type="number"
                min={1}
                max={disponiveis || 1}
                value={quantidade}
                onChange={(event) => setQuantidade(Number(event.target.value))}
              />
            </label>
          ) : (
            <SeatMap assentos={evento.assentos} selecionados={assentoIds} onChange={setAssentoIds} />
          )}

          {error && <p className="error">{error}</p>}

          {user?.nivelAcesso === 'CLIENTE' ? (
            <button
              type="submit"
              disabled={
                reserving ||
                (evento.tipoCapacidade === 'ASSENTOS' && assentoIds.length === 0) ||
                disponiveis === 0
              }
            >
              {reserving ? 'Reservando...' : 'Reservar agora'}
            </button>
          ) : (
            <Link className="primary-button" to="/login">
              Entrar como cliente
            </Link>
          )}
        </form>
      </section>
    </main>
  )
}
