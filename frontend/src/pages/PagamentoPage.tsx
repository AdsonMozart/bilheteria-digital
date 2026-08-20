import { PaymentElement, useElements, useStripe, Elements } from '@stripe/react-stripe-js'
import { loadStripe, type StripeElementsOptions } from '@stripe/stripe-js'
import { type FormEvent, useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { ErrorState, LoadingState } from '../components/StateViews'
import { getErrorMessage, pagamentosApi, reservasApi } from '../services/api'
import type { PaymentIntent, Reserva } from '../types'
import { formatCurrency, formatDateTime, statusLabel } from '../utils/format'

const stripePublishableKey = import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY as string | undefined
const stripePublishableKeyValida =
  Boolean(stripePublishableKey) && (stripePublishableKey!.startsWith('pk_test_') || stripePublishableKey!.startsWith('pk_live_'))
const stripePromise = stripePublishableKeyValida && stripePublishableKey ? loadStripe(stripePublishableKey) : null

export function PagamentoPage() {
  const { reservaId } = useParams()
  const [reserva, setReserva] = useState<Reserva | null>(null)
  const [paymentIntent, setPaymentIntent] = useState<PaymentIntent | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    if (!reservaId) {
      return
    }

    async function carregarPagamento() {
      setLoading(true)
      setError('')

      try {
        const reservaAtual = await reservasApi.buscar(reservaId!)
        setReserva(reservaAtual)

        if (reservaAtual.status === 'PENDENTE') {
          setPaymentIntent(await pagamentosApi.paymentIntent(reservaId!))
        }
      } catch (erro) {
        setError(getErrorMessage(erro))
      } finally {
        setLoading(false)
      }
    }

    carregarPagamento()
  }, [reservaId])

  const stripeOptions = useMemo<StripeElementsOptions | null>(() => {
    if (!paymentIntent?.clientSecret) {
      return null
    }

    return {
      clientSecret: paymentIntent.clientSecret,
      appearance: {
        theme: 'night',
        variables: {
          colorPrimary: '#ff9c1a',
          colorBackground: '#0e1118',
          colorText: '#f4f4f5',
          colorDanger: '#fb7185',
          borderRadius: '12px',
          fontFamily: 'Manrope, system-ui, sans-serif',
        },
      },
    }
  }, [paymentIntent])

  async function refreshReserva() {
    if (!reservaId) {
      return
    }

    setReserva(await reservasApi.buscar(reservaId))
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
        <span className="eyebrow">Pagamento Stripe</span>
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

        {error && <p className="error">{error}</p>}

        {reserva.status === 'PENDENTE' && !stripePromise && (
          <p className="error">
            Configure VITE_STRIPE_PUBLISHABLE_KEY no .env do frontend com uma chave publicavel iniciada por pk_test_ ou
            pk_live_.
          </p>
        )}

        {reserva.status === 'PENDENTE' && stripePromise && stripeOptions && (
          <Elements stripe={stripePromise} options={stripeOptions}>
            <StripePaymentForm onRefreshReserva={refreshReserva} />
          </Elements>
        )}

        {reserva.status !== 'PENDENTE' && (
          <div className="state-view">
            <strong>Pagamento processado pela Stripe.</strong>
            <span>A reserva esta com status {statusLabel(reserva.status)}.</span>
          </div>
        )}

        <Link to="/meus-ingressos">Ver meus ingressos</Link>
      </section>
    </main>
  )
}

type StripePaymentFormProps = {
  onRefreshReserva: () => Promise<void>
}

function StripePaymentForm({ onRefreshReserva }: StripePaymentFormProps) {
  const stripe = useStripe()
  const elements = useElements()
  const [processing, setProcessing] = useState(false)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    if (!stripe || !elements) {
      return
    }

    setProcessing(true)
    setError('')
    setMessage('')

    const { error: stripeError } = await stripe.confirmPayment({
      elements,
      confirmParams: {
        return_url: `${window.location.origin}/meus-ingressos`,
      },
      redirect: 'if_required',
    })

    if (stripeError) {
      setError(stripeError.message || 'Pagamento recusado pela Stripe.')
      setProcessing(false)
      return
    }

    setMessage('Pagamento enviado para a Stripe. A confirmacao final sera aplicada pelo webhook.')

    try {
      await onRefreshReserva()
    } catch {
      // O webhook pode concluir alguns segundos depois; a tela de ingressos reflete o estado final.
    } finally {
      setProcessing(false)
    }
  }

  return (
    <form className="stripe-payment-form" onSubmit={handleSubmit}>
      <PaymentElement />

      {message && <p className="message">{message}</p>}
      {error && <p className="error">{error}</p>}

      <button type="submit" disabled={!stripe || !elements || processing}>
        {processing ? 'Processando na Stripe...' : 'Pagar com Stripe'}
      </button>
    </form>
  )
}
