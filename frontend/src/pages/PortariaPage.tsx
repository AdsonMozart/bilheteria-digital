import { Html5Qrcode } from 'html5-qrcode'
import { type FormEvent, useEffect, useRef, useState } from 'react'
import { ErrorState, LoadingState } from '../components/StateViews'
import { eventosApi, getErrorMessage, portariaApi } from '../services/api'
import type { EventoResumo, ResultadoPortaria } from '../types'
import { formatDateTime } from '../utils/format'

type CameraDevice = {
  id: string
  label: string
}

export function PortariaPage() {
  const [eventos, setEventos] = useState<EventoResumo[]>([])
  const [eventoId, setEventoId] = useState('')
  const [codigo, setCodigo] = useState('')
  const [resultado, setResultado] = useState<ResultadoPortaria | null>(null)
  const [loading, setLoading] = useState(true)
  const [loadingCameras, setLoadingCameras] = useState(false)
  const [validating, setValidating] = useState(false)
  const [scanning, setScanning] = useState(false)
  const [error, setError] = useState('')
  const [cameraStatus, setCameraStatus] = useState('Camera pronta para leitura.')
  const [cameras, setCameras] = useState<CameraDevice[]>([])
  const [cameraId, setCameraId] = useState('')
  const scannerRef = useRef<Html5Qrcode | null>(null)
  const scanLockedRef = useRef(false)

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
      void stopScanner()
    }
  }, [])

  useEffect(() => {
    void carregarCameras()
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

  async function carregarCameras() {
    if (!navigator.mediaDevices?.getUserMedia) {
      setCameraStatus('Este navegador nao oferece acesso direto a camera.')
      return
    }

    setLoadingCameras(true)

    try {
      const lista = await Html5Qrcode.getCameras()
      setCameras(lista)

      if (lista.length > 0) {
        const cameraTraseira = lista.find((camera) => /back|rear|environment|traseira/i.test(camera.label))
        setCameraId((atual) => atual || cameraTraseira?.id || lista[0].id)
        setCameraStatus('Selecione a camera e inicie a leitura.')
      } else {
        setCameraStatus('Nenhuma camera foi encontrada neste dispositivo.')
      }
    } catch (erro) {
      setCameraStatus(mensagemErroCamera(erro))
    } finally {
      setLoadingCameras(false)
    }
  }

  async function startScanner() {
    if (scanning) {
      return
    }

    setError('')
    setResultado(null)
    setCameraStatus('Solicitando permissao da camera...')
    scanLockedRef.current = false

    try {
      await stopScanner()

      const scanner = new Html5Qrcode('qr-reader')
      scannerRef.current = scanner
      await scanner.start(
        cameraId || { facingMode: 'environment' },
        { fps: 10, qrbox: { width: 260, height: 260 }, aspectRatio: 1 },
        (decodedText) => {
          if (scanLockedRef.current) {
            return
          }

          scanLockedRef.current = true
          setCodigo(decodedText)
          setCameraStatus('QR Code lido. Validando ingresso...')
          void validarCodigo(decodedText).finally(() => {
            void stopScanner()
          })
        },
        () => undefined,
      )
      setScanning(true)
      setCameraStatus('Camera ativa. Aponte para o QR Code do ingresso.')
    } catch (erro) {
      setError(mensagemErroCamera(erro))
      setCameraStatus('Nao foi possivel iniciar a camera.')
      setScanning(false)
      scanLockedRef.current = false
    }
  }

  async function stopScanner() {
    const scanner = scannerRef.current
    scannerRef.current = null

    if (!scanner) {
      setScanning(false)
      return
    }

    try {
      await scanner.stop()
      scanner.clear()
    } catch {
      try {
        scanner.clear()
      } catch {
        // Scanner may already be cleared by the browser after permission or track errors.
      }
    } finally {
      setScanning(false)
      scanLockedRef.current = false
      setCameraStatus('Camera parada.')
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
            <div className="camera-toolbar">
              <label>
                Camera
                <select
                  value={cameraId}
                  onChange={(event) => setCameraId(event.target.value)}
                  disabled={scanning || loadingCameras || cameras.length === 0}
                >
                  {cameras.length === 0 && <option value="">Camera padrao do dispositivo</option>}
                  {cameras.map((camera, index) => (
                    <option value={camera.id} key={camera.id}>
                      {camera.label || `Camera ${index + 1}`}
                    </option>
                  ))}
                </select>
              </label>
              <button type="button" className="ghost-button" onClick={carregarCameras} disabled={scanning || loadingCameras}>
                {loadingCameras ? 'Buscando...' : 'Atualizar cameras'}
              </button>
            </div>

            <div className={scanning ? 'qr-reader-wrap qr-reader-active' : 'qr-reader-wrap'}>
              <div id="qr-reader" />
              {!scanning && (
                <div className="qr-reader-placeholder">
                  <span>QR</span>
                  <p>{cameraStatus}</p>
                </div>
              )}
            </div>

            <p className={scanning ? 'camera-status camera-status-active' : 'camera-status'}>{cameraStatus}</p>

            <div className="split-actions">
              <button type="button" onClick={startScanner} disabled={scanning || validating || !eventoId}>
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

function mensagemErroCamera(error: unknown) {
  if (error instanceof DOMException) {
    if (error.name === 'NotAllowedError') {
      return 'Permissao de camera negada. Libere o acesso no navegador para ler QR Codes.'
    }

    if (error.name === 'NotFoundError') {
      return 'Nenhuma camera foi encontrada neste dispositivo.'
    }

    if (error.name === 'NotReadableError') {
      return 'A camera esta em uso por outro aplicativo ou nao pode ser acessada agora.'
    }
  }

  return getErrorMessage(error)
}
