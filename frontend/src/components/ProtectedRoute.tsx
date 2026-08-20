import { Navigate } from 'react-router-dom'
import type { ReactElement } from 'react'
import { useAuth } from '../context/useAuth'
import type { UserRole } from '../types'

export function ProtectedRoute({
  children,
  roles,
}: {
  children: ReactElement
  roles?: UserRole[]
}) {
  const { user, loading } = useAuth()

  if (loading) {
    return <main className="page-shell">Carregando sessao...</main>
  }

  if (!user) {
    return <Navigate to="/login" replace />
  }

  if (roles && !roles.includes(user.nivelAcesso)) {
    return <Navigate to="/" replace />
  }

  return children
}
