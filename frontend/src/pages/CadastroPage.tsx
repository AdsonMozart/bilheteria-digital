import { type FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import mozarticketsLogo from '../assets/mozartickets_logo.png'
import { useAuth } from '../context/useAuth'
import { getErrorMessage } from '../services/api'

export function CadastroPage() {
  const navigate = useNavigate()
  const { registrar } = useAuth()
  const [nome, setNome] = useState('')
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setErro('')
    setCarregando(true)

    try {
      await registrar(nome, email, senha)
      navigate('/')
    } catch (error) {
      setErro(getErrorMessage(error))
    } finally {
      setCarregando(false)
    }
  }

  return (
    <main className="auth-page marquise-login register-marquee">
      <div className="auth-grain" aria-hidden="true" />
      <div className="auth-spotlight auth-spotlight-one" aria-hidden="true" />
      <div className="auth-spotlight auth-spotlight-two" aria-hidden="true" />
      <div className="floating-tickets" aria-hidden="true">
        <span />
        <span />
        <span />
        <span />
        <span />
        <span />
        <span />
        <span />
      </div>

      <section className="login-stage register-stage">
        <aside className="login-poster">
          <Link to="/" className="login-brand">
            <img src={mozarticketsLogo} alt="" aria-hidden="true" />
            <span>Bilheteria Digital</span>
          </Link>

          <div>
            <span className="eyebrow">Novo cliente</span>
            <h1>Emitir meu acesso</h1>
            <p>
              O cadastro publico cria uma conta de cliente para reservar eventos, pagar e receber
              ingressos com QR Code.
            </p>
          </div>

          <div className="login-capabilities">
            <span>Conta cliente</span>
            <span>Reservas</span>
            <span>Pagamento simulado</span>
            <span>Meus ingressos</span>
          </div>
        </aside>

        <section className="auth-panel login-ticket-card register-ticket-card">
          <div className="ticket-notch ticket-notch-left" aria-hidden="true" />
          <div className="ticket-notch ticket-notch-right" aria-hidden="true" />
          <span className="ticket-code">BD-REGISTRO-2026</span>
          <h2>Criar cadastro</h2>
          <p>Informe seus dados para entrar como cliente.</p>

          <form onSubmit={handleSubmit} className="stack-form">
            <label>
              Nome
              <input value={nome} onChange={(event) => setNome(event.target.value)} required />
            </label>

            <label>
              Email
              <input value={email} onChange={(event) => setEmail(event.target.value)} type="email" required />
            </label>

            <label>
              Senha
              <input
                value={senha}
                onChange={(event) => setSenha(event.target.value)}
                type="password"
                minLength={6}
                required
              />
            </label>

            {erro && <p className="error auth-error">{erro}</p>}

            <button type="submit" className="marquise-submit" disabled={carregando}>
              {carregando ? 'Emitindo acesso...' : 'Criar conta'}
            </button>
          </form>

          <p className="auth-link-line">
            Ja tem conta? <Link to="/login">Voltar para login</Link>
          </p>
        </section>
      </section>
    </main>
  )
}
