import { Html5Qrcode } from 'html5-qrcode'
import { type FormEvent, useEffect, useRef, useState } from 'react'
import { ErrorState, LoadingState } from '../components/StateViews'
import { eventosApi, getErrorMessage, portariaApi } from '../services/api'
import type { EventoResumo, ResultadoPortaria } from '../types'
import { formatDateTime } from '../utils/format'

export function PortariaPage() {
  const [eventos, setEventos] = useState<EventoResumo[]>([])
  const [eventoId, setEventoId] = useState('')
  const [codigo, setCodigo] = useState('')
  const [resultado, setResultado] = useState<ResultadoPortaria | null>(null)
  const [loading, setLoading] = useState(true)
  const [validating, setValidating] = useState(false)
  const [scanning, setScanning] = useState(false)
  const [error, setError] = useState('')
  const scannerRef = useRef<Html5Qrcode | null>(null)

  useEffect(() => {
    eventosApi
      .listar()
      .then((lista) => {
        setEventos(lista)
        if (lista[0]) {
          setEventoId(String(lista[0].id))
        }
      })
      .catch((erro) => setError(getErrorMessage(erro)))
      .finally(() => setLoading(false))

    return () => {
      stopScanner()
    }
  }, [])

  async function validar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    await validarCodigo(codigo)
  }

  async function validarCodigo(valor: string) {
    if (!eventoId || !valor) {
      return
    }

    setValidating(true)
    setError('')
    setResultado(null)

    try {
      setResultado(await portariaApi.validar(Number(eventoId), valor))
    } catch (erro) {
      setError(getErrorMessage(erro))
    } finally {
      setValidating(false)
    }
  }

  async function startScanner() {
    setError('')
    setResultado(null)

    try {
      const scanner = new Html5Qrcode('qr-reader')
      scannerRef.current = scanner
      await scanner.start(
        { facingMode: 'environment' },
        { fps: 10, qrbox: { width: 260, height: 260 } },
        (decodedText) => {
          setCodigo(decodedText)
          validarCodigo(decodedText)
          stopScanner()
        },
        () => undefined,
      )
      setScanning(true)
    } catch (erro) {
      setError(getErrorMessage(erro))
      setScanning(false)
    }
  }

  async function stopScanner() {
    const scanner = scannerRef.current
    scannerRef.current = null

    if (!scanner) {
      return
    }

    try {
      await scanner.stop()
      scanner.clear()
    } catch {
      scanner.clear()
    } finally {
      setScanning(false)
    }
  }

  return (
    <main className="page-shell portaria-page">
      <div className="section-heading">
        <div>
          <span className="eyebrow">Portaria</span>
          <h1>Validar ingresso</h1>
        </div>
      </div>

      {loading && <LoadingState />}
      {error && <ErrorState message={error} />}

      {!loading && (
        <section className="gate-layout">
          <form className="gate-panel" onSubmit={validar}>
            <label>
              Evento
              <select value={eventoId} onChange={(event) => setEventoId(event.target.value)} required>
                {eventos.map((evento) => (
                  <option value={evento.id} key={evento.id}>
                    {evento.titulo} - {formatDateTime(evento.dataHora)}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Codigo do ingresso
              <textarea value={codigo} onChange={(event) => setCodigo(event.target.value)} required />
            </label>
            <button type="submit" disabled={validating}>
              {validating ? 'Validando...' : 'Validar codigo'}
            </button>
          </form>

          <section className="camera-panel">
            <div id="qr-reader" />
            <div className="split-actions">
              <button type="button" onClick={startScanner} disabled={scanning}>
                Abrir camera
              </button>
              <button type="button" className="ghost-button" onClick={stopScanner} disabled={!scanning}>
                Parar camera
              </button>
            </div>
          </section>

          {resultado && (
            <section className={`validation-result result-${resultado.resultado.toLowerCase()}`}>
              <span className="eyebrow">Resultado</span>
              <h2>{resultado.resultado}</h2>
              <p>{resultado.mensagem}</p>
              {resultado.ingressoId && <span>Ingresso #{resultado.ingressoId}</span>}
            </section>
          )}
        </section>
      )}
    </main>
  )
}
