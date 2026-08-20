import { NavLink, Outlet, useLocation, useNavigate } from 'react-router-dom'
import mozarticketsLogo from '../assets/mozartickets_logo.png'
import { useAuth } from '../context/useAuth'

export function AppLayout() {
  const { user, logout } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()
  const isAuthRoute = location.pathname === '/login' || location.pathname === '/cadastro'

  function handleLogout() {
    logout()
    navigate('/')
  }

  return (
    <div className={isAuthRoute ? 'app-layout auth-layout' : 'app-layout'}>
      {!isAuthRoute && (
        <header className="site-header">
          <NavLink to="/" className="brand">
            <img src={mozarticketsLogo} alt="" aria-hidden="true" />
            <span>MozarTickets</span>
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
      )}

      <Outlet />

      {!isAuthRoute && (
        <footer className="site-footer">
          <img src={mozarticketsLogo} alt="" aria-hidden="true" />
          <p>MozarTickets - bilheteria digital para eventos que nao param.</p>
        </footer>
      )}
    </div>
  )
}
