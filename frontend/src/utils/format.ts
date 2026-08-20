export function formatCurrency(value: number) {
  return new Intl.NumberFormat('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  }).format(Number(value || 0))
}

export function formatDateTime(value?: string) {
  if (!value) {
    return 'Data a definir'
  }

  return new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
    timeStyle: 'short',
  }).format(new Date(value))
}

export function formatDate(value?: string) {
  if (!value) {
    return ''
  }

  return new Intl.DateTimeFormat('pt-BR', {
    dateStyle: 'short',
  }).format(new Date(value))
}

export function statusLabel(status: string) {
  return status.replaceAll('_', ' ').toLowerCase()
}
