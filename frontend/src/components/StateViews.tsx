export function LoadingState({ label = 'Carregando...' }: { label?: string }) {
  return <div className="state-view">{label}</div>
}

export function EmptyState({ title, text }: { title: string; text?: string }) {
  return (
    <div className="state-view">
      <strong>{title}</strong>
      {text && <span>{text}</span>}
    </div>
  )
}

export function ErrorState({ message }: { message: string }) {
  return <div className="state-view error">{message}</div>
}
