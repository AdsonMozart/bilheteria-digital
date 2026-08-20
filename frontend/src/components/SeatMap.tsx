import type { Assento } from '../types'

type SeatMapProps = {
  assentos: Assento[]
  selecionados: number[]
  onChange: (ids: number[]) => void
}

export function SeatMap({ assentos, selecionados, onChange }: SeatMapProps) {
  const fileiras = Array.from(new Set(assentos.map((assento) => assento.fileira))).sort()

  function toggleAssento(assento: Assento) {
    if (assento.status !== 'DISPONIVEL') {
      return
    }

    if (selecionados.includes(assento.id)) {
      onChange(selecionados.filter((id) => id !== assento.id))
      return
    }

    onChange([...selecionados, assento.id])
  }

  return (
    <div className="seat-map">
      <div className="screen">Tela / Palco</div>
      {fileiras.map((fileira) => (
        <div className="seat-row" key={fileira}>
          <span className="seat-row-label">{fileira}</span>
          <div className="seat-row-grid">
            {assentos
              .filter((assento) => assento.fileira === fileira)
              .sort((a, b) => a.numero - b.numero)
              .map((assento) => {
                const selected = selecionados.includes(assento.id)
                return (
                  <button
                    type="button"
                    key={assento.id}
                    className={`seat seat-${assento.status.toLowerCase()} ${selected ? 'selected' : ''}`}
                    onClick={() => toggleAssento(assento)}
                    disabled={assento.status !== 'DISPONIVEL'}
                    title={`${assento.fileira}${assento.numero} - ${assento.status}`}
                  >
                    {assento.numero}
                  </button>
                )
              })}
          </div>
        </div>
      ))}
      <div className="seat-legend">
        <span>Disponivel</span>
        <span>Selecionado</span>
        <span>Indisponivel</span>
      </div>
    </div>
  )
}
