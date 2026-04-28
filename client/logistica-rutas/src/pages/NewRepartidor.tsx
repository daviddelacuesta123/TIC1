import { useState, type ChangeEvent, type FormEvent } from 'react'
import './Repartidores.css'
import '../App.css'
import {
  crearRepartidor,
  type RepartidorCreatePayload,
} from '../services/repartidorService'

interface NewRepartidorProps {
  onNavigate: (page: 'repartidores') => void
}

const ESTADOS = ['ACTIVO', 'INACTIVO'] as const

type RepartidorEstado = (typeof ESTADOS)[number]

interface FormState {
  idUsuario: string
  dni: string
  nombre: string
  apellido: string
  telefono: string
  correoElectronico: string
  estado: RepartidorEstado
}

export default function NewRepartidor({ onNavigate }: NewRepartidorProps) {
  const [form, setForm] = useState<FormState>({
    idUsuario: '',
    dni: '',
    nombre: '',
    apellido: '',
    telefono: '',
    correoElectronico: '',
    estado: 'ACTIVO',
  })
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  function handleChange(e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)

    try {
      const payload: RepartidorCreatePayload = {
        idUsuario: parseInt(form.idUsuario, 10),
        dni: form.dni,
        nombre: form.nombre,
        apellido: form.apellido,
        telefono: form.telefono,
        correoElectronico: form.correoElectronico,
        estado: form.estado,
      }
      await crearRepartidor(payload)
      onNavigate('repartidores')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al crear el repartidor')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <div className="form-page-container">
      <div className="page-header">
        <h1>Nuevo Repartidor</h1>
      </div>

      {error && <div className="error-banner">{error}</div>}

      <form className="create-form" onSubmit={handleSubmit}>
        <div className="form-section-title">
          <h2>Datos de cuenta</h2>
        </div>

        <div className="form-row">
          <div className="form-col">
            <label htmlFor="idUsuario">ID Usuario *</label>
            <input
              id="idUsuario"
              name="idUsuario"
              type="number"
              min={1}
              value={form.idUsuario}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-col">
            <label htmlFor="dni">DNI *</label>
            <input
              id="dni"
              name="dni"
              value={form.dni}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        <div className="form-section-title">
          <h2>Información personal</h2>
        </div>

        <div className="form-row">
          <div className="form-col">
            <label htmlFor="nombre">Nombre *</label>
            <input
              id="nombre"
              name="nombre"
              value={form.nombre}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-col">
            <label htmlFor="apellido">Apellido *</label>
            <input
              id="apellido"
              name="apellido"
              value={form.apellido}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-col">
            <label htmlFor="telefono">Teléfono</label>
            <input
              id="telefono"
              name="telefono"
              value={form.telefono}
              onChange={handleChange}
            />
          </div>

          <div className="form-col">
            <label htmlFor="correoElectronico">Correo electrónico *</label>
            <input
              id="correoElectronico"
              name="correoElectronico"
              type="email"
              value={form.correoElectronico}
              onChange={handleChange}
              required
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-col">
            <label htmlFor="estado">Estado</label>
            <select id="estado" name="estado" value={form.estado} onChange={handleChange}>
              {ESTADOS.map(estado => (
                <option key={estado} value={estado}>
                  {estado}
                </option>
              ))}
            </select>
          </div>
        </div>

        <div className="form-actions">
          <button className="btn btn-primary" type="submit" disabled={submitting}>
            {submitting ? 'Guardando…' : 'Crear repartidor'}
          </button>
          <button
            className="btn btn-secondary"
            type="button"
            onClick={() => onNavigate('repartidores')}
            disabled={submitting}
          >
            Volver
          </button>
        </div>
      </form>
    </div>
  )
}
