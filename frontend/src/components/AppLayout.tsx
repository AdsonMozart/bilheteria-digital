import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/useAuth'

export function AppLayout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  function handleLogout() {
    logout()
    navigate('/')
  }

  return (
    <div className="app-layout">
      <header className="site-header">
        <NavLink to="/" className="brand">
          Bilheteria Digital
        </NavLink>

        <nav className="main-nav" aria-label="Principal">
          <NavLink to="/">Eventos</NavLink>
          {user?.nivelAcesso === 'CLIENTE' && <NavLink to="/minhas-reservas">Reservas</NavLink>}
          {user?.nivelAcesso === 'CLIENTE' && <NavLink to="/meus-ingressos">Ingressos</NavLink>}
          {user?.nivelAcesso === 'ORGANIZADOR' && <NavLink to="/organizador">Organizador</NavLink>}
          {user?.nivelAcesso === 'PORTARIA' && <NavLink to="/portaria">Portaria</NavLink>}
        </nav>

        <div className="header-actions">
          {user ? (
            <>
              <NavLink to="/conta" className="user-chip">
                {user.nome}
              </NavLink>
              <button type="button" className="ghost-button" onClick={handleLogout}>
                Sair
              </button>
            </>
          ) : (
            <>
              <NavLink to="/login">Entrar</NavLink>
              <NavLink to="/cadastro" className="primary-link">
                Criar conta
              </NavLink>
            </>
          )}
        </div>
      </header>

      <Outlet />

      <footer className="site-footer">
        <p>Simples para quem compra, objetivo para quem opera.</p>
      </footer>
    </div>
  )
}
