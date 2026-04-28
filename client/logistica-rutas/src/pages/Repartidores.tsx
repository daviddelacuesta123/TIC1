import { useCallback, useEffect, useState } from 'react'
import './Repartidores.css'
import {
  borrarRepartidor,
  listarRepartidores,
  actualizarRepartidor,
  type RepartidorResponseDTO,
} from '../services/repartidorService'

interface RepartidoresProps {
  onNavigate: (page: 'new-repartidor') => void
}

interface EditState {
  idUsuario: string
  dni: string
  nombre: string
  apellido: string
  telefono: string
  correoElectronico: string
  estado: string
}

const ESTADOS = ['ACTIVO', 'INACTIVO']

export default function Repartidores({ onNavigate }: RepartidoresProps) {
  const [repartidores, setRepartidores] = useState<RepartidorResponseDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [editState, setEditState] = useState<EditState>({
    idUsuario: '',
    dni: '',
    nombre: '',
    apellido: '',
    telefono: '',
    correoElectronico: '',
    estado: 'ACTIVO',
  })
  const [saving, setSaving] = useState(false)
  const [deletingId, setDeletingId] = useState<number | null>(null)

  const fetchRepartidores = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await listarRepartidores()
      setRepartidores(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar los repartidores')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchRepartidores()
  }, [fetchRepartidores])

  function startEdit(r: RepartidorResponseDTO) {
    setEditingId(r.id)
    setEditState({
      idUsuario: String(r.idUsuario),
      dni: r.dni,
      nombre: r.nombre,
      apellido: r.apellido,
      telefono: r.telefono,
      correoElectronico: r.correoElectronico,
      estado: r.estado,
    })
  }

  function cancelEdit() {
    setEditingId(null)
  }

  async function saveEdit(id: number) {
    setSaving(true)
    try {
      const updated = await actualizarRepartidor(id, {
        idUsuario: parseInt(editState.idUsuario, 10),
        dni: editState.dni,
        nombre: editState.nombre,
        apellido: editState.apellido,
        telefono: editState.telefono,
        correoElectronico: editState.correoElectronico,
        estado: editState.estado,
      })
      setRepartidores(prev => prev.map(r => (r.id === id ? updated : r)))
      setEditingId(null)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al guardar el repartidor')
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(id: number) {
    if (!confirm('¿Deseas eliminar este repartidor? Esta acción solo actualiza la UI local.')) {
      return
    }
    setDeletingId(id)
    try {
      await borrarRepartidor(id)
      setRepartidores(prev => prev.filter(r => r.id !== id))
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al eliminar el repartidor')
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <div className="repartidores-container">
      <div className="repartidores-header">
        <h1>Gestión de Repartidores</h1>
        <button className="btn btn-primary" onClick={() => onNavigate('new-repartidor')}>
          + Nuevo repartidor
        </button>
      </div>

      {error && (
        <div className="error-banner" style={{ marginBottom: '1rem' }}>
          {error}
          <button
            style={{ marginLeft: '1rem', background: 'none', border: 'none', cursor: 'pointer', fontWeight: 700 }}
            onClick={() => setError(null)}
          >
            ✕
          </button>
        </div>
      )}

      <div className="repartidores-table-section">
        {loading ? (
          <p className="empty-state">Cargando repartidores…</p>
        ) : (
          <table className="repartidores-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>ID Usuario</th>
                <th>DNI</th>
                <th>Nombre</th>
                <th>Apellido</th>
                <th>Teléfono</th>
                <th>Correo</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {repartidores.length === 0 ? (
                <tr>
                  <td colSpan={9} className="empty-state">
                    No hay repartidores registrados. Pulsa "+ Nuevo repartidor" para agregar uno.
                  </td>
                </tr>
              ) : (
                repartidores.map(r => {
                  const isEditing = editingId === r.id
                  return (
                    <tr key={r.id}>
                      <td>#{r.id}</td>
                      <td>
                        {isEditing ? (
                          <input
                            type="number"
                            value={editState.idUsuario}
                            min={1}
                            onChange={e => setEditState(s => ({ ...s, idUsuario: e.target.value }))}
                          />
                        ) : (
                          r.idUsuario
                        )}
                      </td>
                      <td>
                        {isEditing ? (
                          <input
                            value={editState.dni}
                            onChange={e => setEditState(s => ({ ...s, dni: e.target.value }))}
                          />
                        ) : (
                          r.dni
                        )}
                      </td>
                      <td>
                        {isEditing ? (
                          <input
                            value={editState.nombre}
                            onChange={e => setEditState(s => ({ ...s, nombre: e.target.value }))}
                          />
                        ) : (
                          r.nombre
                        )}
                      </td>
                      <td>
                        {isEditing ? (
                          <input
                            value={editState.apellido}
                            onChange={e => setEditState(s => ({ ...s, apellido: e.target.value }))}
                          />
                        ) : (
                          r.apellido
                        )}
                      </td>
                      <td>
                        {isEditing ? (
                          <input
                            value={editState.telefono}
                            onChange={e => setEditState(s => ({ ...s, telefono: e.target.value }))}
                          />
                        ) : (
                          r.telefono
                        )}
                      </td>
                      <td>
                        {isEditing ? (
                          <input
                            type="email"
                            value={editState.correoElectronico}
                            onChange={e => setEditState(s => ({ ...s, correoElectronico: e.target.value }))}
                          />
                        ) : (
                          r.correoElectronico
                        )}
                      </td>
                      <td>
                        {isEditing ? (
                          <select
                            value={editState.estado}
                            onChange={e => setEditState(s => ({ ...s, estado: e.target.value }))}
                          >
                            {ESTADOS.map(estado => (
                              <option key={estado} value={estado}>
                                {estado}
                              </option>
                            ))}
                          </select>
                        ) : (
                          <span className={`badge ${r.estado === 'ACTIVO' ? 'badge-activo' : 'badge-inactivo'}`}>
                            {r.estado}
                          </span>
                        )}
                      </td>
                      <td>
                        <div className="actions-cell">
                          {isEditing ? (
                            <>
                              <button
                                className="btn btn-success btn-sm"
                                onClick={() => saveEdit(r.id)}
                                disabled={saving}
                              >
                                {saving ? 'Guardando…' : 'Guardar'}
                              </button>
                              <button className="btn btn-secondary btn-sm" onClick={cancelEdit} disabled={saving}>
                                Cancelar
                              </button>
                            </>
                          ) : (
                            <>
                              <button className="btn btn-edit btn-sm" onClick={() => startEdit(r)}>
                                Editar
                              </button>
                              <button
                                className="btn btn-delete btn-sm"
                                onClick={() => handleDelete(r.id)}
                                disabled={deletingId === r.id}
                              >
                                {deletingId === r.id ? 'Eliminando…' : 'Eliminar'}
                              </button>
                            </>
                          )}
                        </div>
                      </td>
                    </tr>
                  )
                })
              )}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}
