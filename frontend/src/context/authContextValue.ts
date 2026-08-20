import { createContext } from 'react'
import type { UsuarioLogado, UserRole } from '../types'

export type AuthContextValue = {
  user: UsuarioLogado | null
  loading: boolean
  isAuthenticated: boolean
  login: (email: string, senha: string) => Promise<UsuarioLogado>
  registrar: (nome: string, email: string, senha: string) => Promise<UsuarioLogado>
  logout: () => void
  refreshUser: () => Promise<void>
  hasRole: (roles: UserRole[]) => boolean
}

export const AuthContext = createContext<AuthContextValue | null>(null)
