import { useState, useEffect, useCallback } from 'react'
import './Vehicles.css'
import {
  listarVehiculos,
  actualizarVehiculo,
  darDeBajaVehiculo,
  type VehiculoResponseDTO,
} from '../services/vehiculoService'

const LABEL_PROPULSION: Record<string, string> = {
  TERMICA: 'Térmica',
  ELECTRICA: 'Eléctrica',
  HIBRIDA: 'Híbrida',
}

const BADGE_PROPULSION: Record<string, string> = {
  TERMICA: 'badge-térmica',
  ELECTRICA: 'badge-eléctrica',
  HIBRIDA: 'badge-híbrida',
}

interface VehiclesProps {
  onNavigate: (page: 'new-vehicle') => void
}

interface EditState {
  capacidadPeso: string
  capacidadVolumen: string
  costoPorKm: string
}

export default function Vehicles({ onNavigate }: VehiclesProps) {
  const [vehicles, setVehicles] = useState<VehiculoResponseDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [editState, setEditState] = useState<EditState>({ capacidadPeso: '', capacidadVolumen: '', costoPorKm: '' })
  const [saving, setSaving] = useState(false)
  const [deletingId, setDeletingId] = useState<number | null>(null)

  const fetchVehicles = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      const data = await listarVehiculos()
      setVehicles(data)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar los vehículos')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    fetchVehicles()
  }, [fetchVehicles])

  function startEdit(v: VehiculoResponseDTO) {
    setEditingId(v.id)
    setEditState({
      capacidadPeso: String(v.capacidadPeso),
      capacidadVolumen: String(v.capacidadVolumen),
      costoPorKm: String(v.costoPorKm),
    })
  }

  function cancelEdit() {
    setEditingId(null)
  }

  async function saveEdit(id: number) {
    setSaving(true)
    try {
      const updated = await actualizarVehiculo(id, {
        capacidadPeso: parseFloat(editState.capacidadPeso),
        capacidadVolumen: parseFloat(editState.capacidadVolumen),
        costoPorKm: parseFloat(editState.costoPorKm),
      })
      setVehicles(prev => prev.map(v => (v.id === id ? updated : v)))
      setEditingId(null)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al guardar los cambios')
    } finally {
      setSaving(false)
    }
  }

  async function handleDarDeBaja(id: number) {
    if (!confirm('¿Confirmas dar de baja este vehículo? La operación es lógica y conserva el historial.')) return
    setDeletingId(id)
    try {
      await darDeBajaVehiculo(id)
      setVehicles(prev => prev.filter(v => v.id !== id))
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al dar de baja el vehículo')
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <div className="vehicles-container">
      <div className="vehicles-header">
        <h1>Flota de Vehículos</h1>
        <button className="btn btn-primary" onClick={() => onNavigate('new-vehicle')}>
          + Nuevo vehículo
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

      <div className="vehicles-table-section">
        {loading ? (
          <p className="empty-state">Cargando vehículos…</p>
        ) : (
          <table className="vehicles-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Marca</th>
                <th>Modelo</th>
                <th>Año</th>
                <th>Peso máx. (kg)</th>
                <th>Volumen máx. (m³)</th>
                <th>Costo/km (USD)</th>
                <th>Propulsión</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {vehicles.length === 0 ? (
                <tr>
                  <td colSpan={10} className="empty-state">
                    No hay vehículos activos. Pulsa "+ Nuevo vehículo" para agregar uno.
                  </td>
                </tr>
              ) : (
                vehicles.map(v => {
                  const isEditing = editingId === v.id
                  return (
                    <tr key={v.id}>
                      <td className="placa-cell">#{v.id}</td>
                      <td>{v.marca}</td>
                      <td>{v.modelo}</td>
                      <td>{v.anioFabricacion}</td>

                      {/* Campos editables inline */}
                      <td>
                        {isEditing ? (
                          <input
                            type="number"
                            value={editState.capacidadPeso}
                            min={0.01}
                            step="0.01"
                            onChange={e => setEditState(s => ({ ...s, capacidadPeso: e.target.value }))}
                            style={{ width: '90px' }}
                          />
                        ) : v.capacidadPeso}
                      </td>
                      <td>
                        {isEditing ? (
                          <input
                            type="number"
                            value={editState.capacidadVolumen}
                            min={0.01}
                            step="0.01"
                            onChange={e => setEditState(s => ({ ...s, capacidadVolumen: e.target.value }))}
                            style={{ width: '90px' }}
                          />
                        ) : v.capacidadVolumen}
                      </td>
                      <td>
                        {isEditing ? (
                          <input
                            type="number"
                            value={editState.costoPorKm}
                            min={0.0001}
                            step="0.0001"
                            onChange={e => setEditState(s => ({ ...s, costoPorKm: e.target.value }))}
                            style={{ width: '90px' }}
                          />
                        ) : `$${v.costoPorKm.toFixed(4)}`}
                      </td>

                      <td>
                        <span className={`badge ${BADGE_PROPULSION[v.tipoPropulsion] ?? ''}`}>
                          {LABEL_PROPULSION[v.tipoPropulsion] ?? v.tipoPropulsion}
                        </span>
                      </td>
                      <td>
                        <span className={`badge ${v.activo ? 'badge-libre' : 'badge-ocupado'}`}>
                          {v.activo ? 'Activo' : 'Inactivo'}
                        </span>
                      </td>
                      <td>
                        <div className="actions-cell">
                          {isEditing ? (
                            <>
                              <button
                                className="btn btn-success btn-sm"
                                onClick={() => saveEdit(v.id)}
                                disabled={saving}
                              >
                                {saving ? '…' : 'Guardar'}
                              </button>
                              <button
                                className="btn btn-secondary btn-sm"
                                onClick={cancelEdit}
                                disabled={saving}
                              >
                                Cancelar
                              </button>
                            </>
                          ) : (
                            <>
                              <button
                                className="btn btn-edit btn-sm"
                                onClick={() => startEdit(v)}
                              >
                                Editar
                              </button>
                              <button
                                className="btn btn-delete btn-sm"
                                onClick={() => handleDarDeBaja(v.id)}
                                disabled={deletingId === v.id}
                              >
                                {deletingId === v.id ? '…' : 'Dar de baja'}
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
