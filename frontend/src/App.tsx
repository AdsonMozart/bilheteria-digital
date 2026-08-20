import { Navigate, Route, Routes } from 'react-router-dom'
import { AppLayout } from './components/AppLayout'
import { ProtectedRoute } from './components/ProtectedRoute'
import { AuthProvider } from './context/AuthContext'
import { CadastroPage } from './pages/CadastroPage'
import { ContaPage } from './pages/ContaPage'
import { EventoDetalhePage } from './pages/EventoDetalhePage'
import { HomePage } from './pages/HomePage'
import { IngressoCompartilhadoPage } from './pages/IngressoCompartilhadoPage'
import { LoginPage } from './pages/LoginPage'
import { MeusIngressosPage } from './pages/MeusIngressosPage'
import { MinhasReservasPage } from './pages/MinhasReservasPage'
import { NovoEventoPage } from './pages/NovoEventoPage'
import { OrganizadorPage } from './pages/OrganizadorPage'
import { PagamentoPage } from './pages/PagamentoPage'
import { PortariaPage } from './pages/PortariaPage'
import './App.css'

function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route element={<AppLayout />}>
          <Route path="/" element={<HomePage />} />
          <Route path="/eventos/:id" element={<EventoDetalhePage />} />
          <Route path="/ingressos/compartilhado/:token" element={<IngressoCompartilhadoPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/cadastro" element={<CadastroPage />} />

          <Route
            path="/minhas-reservas"
            element={
              <ProtectedRoute roles={['CLIENTE']}>
                <MinhasReservasPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/meus-ingressos"
            element={
              <ProtectedRoute roles={['CLIENTE']}>
                <MeusIngressosPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/pagamento/:reservaId"
            element={
              <ProtectedRoute roles={['CLIENTE']}>
                <PagamentoPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/organizador"
            element={
              <ProtectedRoute roles={['ORGANIZADOR']}>
                <OrganizadorPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/organizador/novo"
            element={
              <ProtectedRoute roles={['ORGANIZADOR']}>
                <NovoEventoPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/portaria"
            element={
              <ProtectedRoute roles={['PORTARIA']}>
                <PortariaPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/conta"
            element={
              <ProtectedRoute>
                <ContaPage />
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </AuthProvider>
  )
}

export default App
