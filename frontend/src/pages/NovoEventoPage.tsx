import { type FormEvent, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { EmptyState } from '../components/StateViews'
import { catalogoApi, getErrorMessage, organizadorApi } from '../services/api'
import type { CatalogoItem, EventoFormData } from '../types'

const initialForm: EventoFormData = {
  origemExterna: 'MANUAL',
  idExterno: null,
  titulo: '',
  tipo: 'SHOW',
  descricao: '',
  urlImagem: '',
  nomeLocal: '',
  enderecoLocal: '',
  dataHora: '',
  tipoCapacidade: 'GERAL',
  preco: 0,
  capacidade: 100,
}

export function NovoEventoPage() {
  const navigate = useNavigate()
  const [form, setForm] = useState<EventoFormData>(initialForm)
  const [origemBusca, setOrigemBusca] = useState<'TMDB' | 'TICKETMASTER'>('TMDB')
  const [termoBusca, setTermoBusca] = useState('')
  const [catalogo, setCatalogo] = useState<CatalogoItem[]>([])
  const [loadingCatalogo, setLoadingCatalogo] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [catalogError, setCatalogError] = useState('')

  function update<K extends keyof EventoFormData>(key: K, value: EventoFormData[K]) {
    setForm((current) => ({ ...current, [key]: value }))
  }

  async function buscarCatalogo(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setCatalogError('')
    setLoadingCatalogo(true)

    try {
      setCatalogo(await catalogoApi.buscar(origemBusca, termoBusca))
    } catch (erro) {
      setCatalogError(getErrorMessage(erro))
      setCatalogo([])
    } finally {
      setLoadingCatalogo(false)
    }
  }

  function aplicarItem(item: CatalogoItem) {
    setForm((current) => ({
      ...current,
      origemExterna: item.origem,
      idExterno: item.idExterno,
      titulo: item.titulo,
      tipo: item.tipo,
      descricao: item.descricao || '',
      urlImagem: item.urlImagem || '',
    }))
  }

  async function salvar(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setSaving(true)

    try {
      const payload: EventoFormData = {
        ...form,
        idExterno: form.idExterno || null,
        descricao: form.descricao || null,
        urlImagem: form.urlImagem || null,
        enderecoLocal: form.enderecoLocal || null,
        preco: Number(form.preco),
        capacidade: Number(form.capacidade),
      }

      await organizadorApi.criarEvento(payload)
      navigate('/organizador')
    } catch (erro) {
      setError(getErrorMessage(erro))
    } finally {
      setSaving(false)
    }
  }

  return (
    <main className="page-shell organizer-create">
      <div className="section-heading">
        <div>
          <span className="eyebrow">Organizador</span>
          <h1>Criar evento</h1>
        </div>
      </div>

      <section className="catalog-layout">
        <aside className="catalog-panel">
          <h2>Catalogo externo</h2>
          <form className="stack-form" onSubmit={buscarCatalogo}>
            <label>
              Origem
              <select value={origemBusca} onChange={(event) => setOrigemBusca(event.target.value as 'TMDB' | 'TICKETMASTER')}>
                <option value="TMDB">TMDb</option>
                <option value="TICKETMASTER">Ticketmaster</option>
              </select>
            </label>
            <label>
              Busca
              <input value={termoBusca} onChange={(event) => setTermoBusca(event.target.value)} placeholder="Matrix, Metallica..." required />
            </label>
            <button type="submit" disabled={loadingCatalogo}>
              {loadingCatalogo ? 'Buscando...' : 'Buscar no catalogo'}
            </button>
          </form>

          {catalogError && <p className="error">{catalogError}</p>}
          {!loadingCatalogo && catalogo.length === 0 && !catalogError && (
            <EmptyState title="Sem itens carregados" text="Busque um show ou filme para preencher o evento." />
          )}

          <div className="catalog-results">
            {catalogo.map((item) => (
              <article className="catalog-item" key={`${item.origem}-${item.idExterno}`}>
                {item.urlImagem && <img src={item.urlImagem} alt="" />}
                <div>
                  <span className="eyebrow">{item.origem}</span>
                  <h3>{item.titulo}</h3>
                  <p>{item.descricao}</p>
                  <button type="button" onClick={() => aplicarItem(item)}>
                    Usar dados
                  </button>
                </div>
              </article>
            ))}
          </div>
        </aside>

        <form className="event-form" onSubmit={salvar}>
          <h2>Dados do evento</h2>
          <label>
            Titulo
            <input value={form.titulo} onChange={(event) => update('titulo', event.target.value)} required />
          </label>
          <label>
            Tipo
            <select value={form.tipo} onChange={(event) => update('tipo', event.target.value as 'SHOW' | 'FILME')}>
              <option value="SHOW">Show</option>
              <option value="FILME">Filme</option>
            </select>
          </label>
          <label>
            Origem
            <select
              value={form.origemExterna}
              onChange={(event) => update('origemExterna', event.target.value as EventoFormData['origemExterna'])}
            >
              <option value="MANUAL">Manual</option>
              <option value="TMDB">TMDb</option>
              <option value="TICKETMASTER">Ticketmaster</option>
            </select>
          </label>
          <label>
            Id externo
            <input value={form.idExterno || ''} onChange={(event) => update('idExterno', event.target.value)} />
          </label>
          <label className="full-field">
            Descricao
            <textarea value={form.descricao || ''} onChange={(event) => update('descricao', event.target.value)} />
          </label>
          <label className="full-field">
            URL da imagem
            <input value={form.urlImagem || ''} onChange={(event) => update('urlImagem', event.target.value)} />
          </label>
          <label>
            Local
            <input value={form.nomeLocal} onChange={(event) => update('nomeLocal', event.target.value)} required />
          </label>
          <label>
            Endereco
            <input value={form.enderecoLocal || ''} onChange={(event) => update('enderecoLocal', event.target.value)} />
          </label>
          <label>
            Data e hora
            <input value={form.dataHora} onChange={(event) => update('dataHora', event.target.value)} type="datetime-local" required />
          </label>
          <label>
            Tipo de capacidade
            <select
              value={form.tipoCapacidade}
              onChange={(event) => update('tipoCapacidade', event.target.value as 'GERAL' | 'ASSENTOS')}
            >
              <option value="GERAL">Geral</option>
              <option value="ASSENTOS">Assentos</option>
            </select>
          </label>
          <label>
            Preco
            <input
              value={form.preco}
              onChange={(event) => update('preco', Number(event.target.value))}
              type="number"
              min="0.01"
              step="0.01"
              required
            />
          </label>
          <label>
            Capacidade
            <input
              value={form.capacidade}
              onChange={(event) => update('capacidade', Number(event.target.value))}
              type="number"
              min="1"
              required
            />
          </label>

          {error && <p className="error full-field">{error}</p>}

          <button type="submit" disabled={saving}>
            {saving ? 'Salvando...' : 'Criar evento'}
          </button>
        </form>
      </section>
    </main>
  )
}
