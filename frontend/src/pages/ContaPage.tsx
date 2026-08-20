import { useAuth } from '../context/useAuth'

export function ContaPage() {
  const { user } = useAuth()

  return (
    <main className="page-shell narrow-page">
      <section className="account-card">
        <span className="eyebrow">Conta</span>
        <h1>{user?.nome}</h1>
        <dl className="detail-list">
          <div>
            <dt>Email</dt>
            <dd>{user?.email}</dd>
          </div>
          <div>
            <dt>Perfil</dt>
            <dd>{user?.nivelAcesso}</dd>
          </div>
        </dl>
      </section>
    </main>
  )
}
