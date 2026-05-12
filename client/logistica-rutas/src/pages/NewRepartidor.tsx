import { useState, type ChangeEvent, type FormEvent } from 'react'
import './Repartidores.css'
import '../App.css'
import { crearRepartidor, registrarUsuarioRepartidor } from '../services/repartidorService'

interface NewRepartidorProps {
  onNavigate: (page: 'repartidores') => void
}

interface FormState {
  username: string
  password: string
  dni: string
  nombre: string
  apellido: string
  telefono: string
  correoElectronico: string
}

export default function NewRepartidor({ onNavigate }: NewRepartidorProps) {
  const [form, setForm] = useState<FormState>({
    username: '',
    password: '',
    dni: '',
    nombre: '',
    apellido: '',
    telefono: '',
    correoElectronico: '',
  })
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  function handleChange(e: ChangeEvent<HTMLInputElement>) {
    const { name, value } = e.target
    setForm(prev => ({ ...prev, [name]: value }))
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)

    try {
      // 1. Crear cuenta de usuario con rol REPARTIDOR
      const usuario = await registrarUsuarioRepartidor(form.username, form.password)

      // 2. Crear el repartidor vinculado a esa cuenta
      await crearRepartidor({
        idUsuario: usuario.id,
        dni: form.dni,
        nombre: form.nombre,
        apellido: form.apellido,
        telefono: form.telefono,
        correoElectronico: form.correoElectronico,
      })

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
            <label htmlFor="username">Usuario *</label>
            <input
              id="username" name="username" type="text"
              value={form.username} onChange={handleChange}
              placeholder="Nombre de usuario para iniciar sesión"
              required
            />
          </div>
          <div className="form-col">
            <label htmlFor="password">Contraseña *</label>
            <input
              id="password" name="password" type="password"
              value={form.password} onChange={handleChange}
              placeholder="Contraseña de acceso"
              required minLength={6}
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
              id="nombre" name="nombre" value={form.nombre}
              onChange={handleChange} required
              placeholder="Nombre del repartidor"
            />
          </div>
          <div className="form-col">
            <label htmlFor="apellido">Apellido *</label>
            <input
              id="apellido" name="apellido" value={form.apellido}
              onChange={handleChange} required
              placeholder="Apellido del repartidor"
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-col">
            <label htmlFor="dni">DNI *</label>
            <input
              id="dni" name="dni" value={form.dni}
              onChange={handleChange} required
              placeholder="Número de documento"
            />
          </div>
          <div className="form-col">
            <label htmlFor="telefono">Teléfono *</label>
            <input
              id="telefono" name="telefono" value={form.telefono}
              onChange={handleChange} required
              placeholder="Ej: 3001234567"
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-col" style={{ flex: 1 }}>
            <label htmlFor="correoElectronico">Correo electrónico *</label>
            <input
              id="correoElectronico" name="correoElectronico" type="email"
              value={form.correoElectronico} onChange={handleChange}
              required placeholder="correo@ejemplo.com"
            />
          </div>
        </div>

        <div className="form-actions">
          <button className="btn btn-primary" type="submit" disabled={submitting}>
            {submitting ? 'Guardando…' : 'Crear repartidor'}
          </button>
          <button
            className="btn btn-secondary" type="button"
            onClick={() => onNavigate('repartidores')} disabled={submitting}
          >
            Volver
          </button>
        </div>
      </form>
    </div>
  )
}
