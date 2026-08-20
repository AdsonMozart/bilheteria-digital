import { type FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import mozarticketsLogo from '../assets/mozartickets_logo.png'
import { useAuth } from '../context/useAuth'
import { getErrorMessage } from '../services/api'
import type { UserRole } from '../types'

const quickUsers = [
  { label: 'Organizador', email: 'organizador@teste.com' },
  { label: 'Cliente 1', email: 'cliente1@teste.com' },
  { label: 'Cliente 2', email: 'cliente2@teste.com' },
  { label: 'Portaria', email: 'portaria@teste.com' },
]

export function LoginPage() {
  const navigate = useNavigate()
  const { login } = useAuth()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [carregando, setCarregando] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setErro('')
    setCarregando(true)

    try {
      const usuario = await login(email, senha)
      navigate(nextRoute(usuario.nivelAcesso))
    } catch (error) {
      setErro(getErrorMessage(error))
    } finally {
      setCarregando(false)
    }
  }

  function fillUser(emailValue: string) {
    setEmail(emailValue)
    setSenha('123456')
  }

  return (
    <main className="auth-page marquise-login">
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

      <section className="login-stage">
        <aside className="login-poster">
          <Link to="/" className="login-brand">
            <img src={mozarticketsLogo} alt="" aria-hidden="true" />
            <span>Bilheteria Digital</span>
          </Link>

          <div>
            <span className="eyebrow">Acesso a sessao</span>
            <h1>Entrar na bilheteria</h1>
            <p>Compre ingressos, gerencie eventos ou valide entradas usando os perfis reais do sistema.</p>
          </div>

          <div className="login-capabilities">
            <span>Eventos publicados</span>
            <span>Reservas</span>
            <span>QR Code</span>
            <span>Portaria</span>
          </div>
        </aside>

        <section className="auth-panel login-ticket-card">
          <div className="ticket-notch ticket-notch-left" aria-hidden="true" />
          <div className="ticket-notch ticket-notch-right" aria-hidden="true" />
          <span className="ticket-code">BD-ACESSO-2026</span>
          <h2>Faça seu login</h2>
          <p>Use seu e-mail e senha para continuar.</p>

          <form onSubmit={handleSubmit} className="stack-form">
            <label>
              Email
              <input value={email} onChange={(event) => setEmail(event.target.value)} type="email" required />
            </label>

            <label>
              Senha
              <input value={senha} onChange={(event) => setSenha(event.target.value)} type="password" required />
            </label>

            {erro && <p className="error auth-error">{erro}</p>}

            <button type="submit" className="marquise-submit" disabled={carregando}>
              {carregando ? 'Validando ingresso...' : 'Entrar'}
            </button>
          </form>

          <div className="quick-login-panel">
            <span>Perfis de teste</span>
            <div className="quick-login">
              {quickUsers.map((user) => (
                <button type="button" className="ghost-button" key={user.email} onClick={() => fillUser(user.email)}>
                  {user.label}
                </button>
              ))}
            </div>
          </div>

          <p className="auth-link-line">
            Novo por aqui? <Link to="/cadastro">Criar conta de cliente</Link>
          </p>
        </section>
      </section>
    </main>
  )
}

function nextRoute(role: UserRole) {
  if (role === 'ORGANIZADOR') {
    return '/organizador'
  }

  if (role === 'PORTARIA') {
    return '/portaria'
  }

  return '/'
}
