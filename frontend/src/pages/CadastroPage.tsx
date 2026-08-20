import { type FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
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
    <main className="auth-page">
      <section className="auth-panel">
        <span className="eyebrow">Cadastro</span>
        <h1>Crie sua conta</h1>
        <p>O cadastro publico cria um perfil de cliente.</p>

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

          {erro && <p className="error">{erro}</p>}

          <button type="submit" disabled={carregando}>
            {carregando ? 'Cadastrando...' : 'Cadastrar'}
          </button>
        </form>

        <p>
          Ja tem conta? <Link to="/login">Entrar</Link>
        </p>
      </section>
    </main>
  )
}
