import {
  type ReactNode,
  useEffect,
  useState,
} from 'react'
import { authApi, clearToken, getToken, setToken } from '../services/api'
import type { UsuarioLogado, UserRole } from '../types'
import { AuthContext } from './authContextValue'

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UsuarioLogado | null>(null)
  const [loading, setLoading] = useState(Boolean(getToken()))

  useEffect(() => {
    if (!getToken()) {
      return
    }

    refreshUser().finally(() => setLoading(false))
  }, [])

  async function login(email: string, senha: string) {
    const response = await authApi.login(email, senha)
    setToken(response.token)
    const usuario = await authApi.me()
    setUser(usuario)
    return usuario
  }

  async function registrar(nome: string, email: string, senha: string) {
    const response = await authApi.registrar(nome, email, senha)
    setToken(response.token)
    const usuario = await authApi.me()
    setUser(usuario)
    return usuario
  }

  async function refreshUser() {
    try {
      const usuario = await authApi.me()
      setUser(usuario)
    } catch {
      clearToken()
      setUser(null)
    }
  }

  function logout() {
    clearToken()
    setUser(null)
  }

  function hasRole(roles: UserRole[]) {
    return Boolean(user && roles.includes(user.nivelAcesso))
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        loading,
        isAuthenticated: Boolean(user),
        login,
        registrar,
        logout,
        refreshUser,
        hasRole,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}
