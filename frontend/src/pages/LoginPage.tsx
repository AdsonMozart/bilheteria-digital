import { type FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'
import { getErrorMessage } from '../services/api'
import type { UserRole } from '../types'

const quickUsers = [
  { label: 'Organizador', email: 'organizador@teste.com' },
  { label: 'Cliente', email: 'cliente1@teste.com' },
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
    <main className="auth-page">
      <section className="auth-panel">
        <span className="eyebrow">Acesso</span>
        <h1>Entre na sua conta</h1>
        <p>Use seu perfil para comprar, organizar ou validar ingressos.</p>

        <form onSubmit={handleSubmit} className="stack-form">
          <label>
            Email
            <input value={email} onChange={(event) => setEmail(event.target.value)} type="email" required />
          </label>

          <label>
            Senha
            <input value={senha} onChange={(event) => setSenha(event.target.value)} type="password" required />
          </label>

          {erro && <p className="error">{erro}</p>}

          <button type="submit" disabled={carregando}>
            {carregando ? 'Entrando...' : 'Entrar'}
          </button>
        </form>

        <div className="quick-login">
          {quickUsers.map((user) => (
            <button type="button" className="ghost-button" key={user.email} onClick={() => fillUser(user.email)}>
              {user.label}
            </button>
          ))}
        </div>

        <p>
          Novo por aqui? <Link to="/cadastro">Criar conta de cliente</Link>
        </p>
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
