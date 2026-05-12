import { useState, type ChangeEvent, type FormEvent } from 'react'
import type { Page } from '../App'
import { crearProducto } from '../services/productoService'

interface NewProductoProps {
  onNavigate: (page: Page) => void
}

export default function NewProducto({ onNavigate }: NewProductoProps) {
  const [nombre, setNombre] = useState('')
  const [peso, setPeso] = useState('')
  const [volumen, setVolumen] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)
    try {
      await crearProducto({
        nombre: nombre.trim(),
        peso: parseFloat(peso),
        volumen: parseFloat(volumen),
      })
      onNavigate('productos')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al crear el producto')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="form-page-container">
      <div className="page-header">
        <h1>Nuevo Producto</h1>
      </div>

      {error && <div className="error-banner">{error}</div>}

      <form className="create-form" onSubmit={handleSubmit}>
        <div className="form-row">
          <div className="form-col" style={{ flex: 1 }}>
            <label htmlFor="nombre">Nombre del producto *</label>
            <input
              id="nombre" type="text" value={nombre}
              onChange={(e: ChangeEvent<HTMLInputElement>) => setNombre(e.target.value)}
              placeholder="Ej: Paquete pequeño (hasta 1kg)"
              required
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-col">
            <label htmlFor="peso">Peso (kg) *</label>
            <input
              id="peso" type="number" value={peso} min="0" step="0.01"
              onChange={(e: ChangeEvent<HTMLInputElement>) => setPeso(e.target.value)}
              placeholder="Ej: 1.5"
              required
            />
          </div>
          <div className="form-col">
            <label htmlFor="volumen">Volumen (m³) *</label>
            <input
              id="volumen" type="number" value={volumen} min="0" step="0.001"
              onChange={(e: ChangeEvent<HTMLInputElement>) => setVolumen(e.target.value)}
              placeholder="Ej: 0.005"
              required
            />
          </div>
        </div>

        <div className="form-actions">
          <button className="btn btn-primary" type="submit" disabled={submitting}>
            {submitting ? 'Guardando…' : 'Crear producto'}
          </button>
          <button
            className="btn btn-secondary" type="button"
            onClick={() => onNavigate('productos')} disabled={submitting}
          >
            Volver
          </button>
        </div>
      </form>
    </div>
  )
}
