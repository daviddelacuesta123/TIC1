import { useCallback, useEffect, useRef, useState } from 'react'
import './Repartidores.css'
import {
  borrarRepartidor,
  listarRepartidores,
  actualizarRepartidor,
  asignarVehiculo,
  obtenerVehiculo,
  type RepartidorResponseDTO,
  type AsignacionVehiculoResponseDTO,
} from '../services/repartidorService'
import {
  listarVehiculosActivos,
  type VehiculoResponseDTO,
} from '../services/vehiculoService'

interface RepartidoresProps {
  onNavigate: (page: 'new-repartidor') => void
}

interface EditState {
  dni: string
  nombre: string
  apellido: string
  telefono: string
  correoElectronico: string
}

export default function Repartidores({ onNavigate }: RepartidoresProps) {
  const [repartidores, setRepartidores] = useState<RepartidorResponseDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [editState, setEditState] = useState<EditState>({ dni: '', nombre: '', apellido: '', telefono: '', correoElectronico: '' })
  const [saving, setSaving] = useState(false)
  const [deletingId, setDeletingId] = useState<number | null>(null)
  const [successMsg, setSuccessMsg] = useState<string | null>(null)
  const [openMenuId, setOpenMenuId] = useState<number | null>(null)
  const [menuPos, setMenuPos] = useState<{ top: number; left: number } | null>(null)
  const menuRef = useRef<HTMLDivElement | null>(null)

  // — Modal asignación —
  const [modalRepartidor, setModalRepartidor] = useState<RepartidorResponseDTO | null>(null)
  const [vehiculoActual, setVehiculoActual] = useState<AsignacionVehiculoResponseDTO | null>(null)
  const [vehiculosDisponibles, setVehiculosDisponibles] = useState<VehiculoResponseDTO[]>([])
  const [loadingModal, setLoadingModal] = useState(false)
  const [selectedVehiculoId, setSelectedVehiculoId] = useState<number | ''>('')
  const [asignando, setAsignando] = useState(false)
  const [modalError, setModalError] = useState<string | null>(null)
  const overlayRef = useRef<HTMLDivElement>(null)

  const fetchRepartidores = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setRepartidores(await listarRepartidores())
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar los repartidores')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { fetchRepartidores() }, [fetchRepartidores])

  // Cerrar menú al hacer click fuera
  useEffect(() => {
    function onClickOutside(e: MouseEvent) {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setOpenMenuId(null)
        setMenuPos(null)
      }
    }
    if (openMenuId !== null) document.addEventListener('mousedown', onClickOutside)
    return () => document.removeEventListener('mousedown', onClickOutside)
  }, [openMenuId])

  // Cerrar modal con Escape
  useEffect(() => {
    if (!modalRepartidor) return
    function onKey(e: KeyboardEvent) { if (e.key === 'Escape') closeModal() }
    document.addEventListener('keydown', onKey)
    return () => document.removeEventListener('keydown', onKey)
  }, [modalRepartidor])

  async function openModal(r: RepartidorResponseDTO) {
    setOpenMenuId(null)
    setModalRepartidor(r)
    setSelectedVehiculoId('')
    setModalError(null)
    setLoadingModal(true)
    setVehiculoActual(null)
    setVehiculosDisponibles([])
    const [vehiculoRes, disponiblesRes] = await Promise.allSettled([
      obtenerVehiculo(r.id),
      listarVehiculosActivos(),
    ])
    if (vehiculoRes.status === 'fulfilled') setVehiculoActual(vehiculoRes.value)
    if (disponiblesRes.status === 'fulfilled') setVehiculosDisponibles(disponiblesRes.value)
    else setModalError(disponiblesRes.reason instanceof Error ? disponiblesRes.reason.message : 'Error al cargar vehículos')
    setLoadingModal(false)
  }

  function closeModal() {
    if (asignando) return
    setModalRepartidor(null)
    setModalError(null)
    setSelectedVehiculoId('')
  }

  async function handleAsignar() {
    if (!modalRepartidor || selectedVehiculoId === '') return
    setAsignando(true)
    setModalError(null)
    try {
      await asignarVehiculo(modalRepartidor.id, selectedVehiculoId as number)
      setSuccessMsg('Vehículo asignado correctamente')
      closeModal()
      setTimeout(() => setSuccessMsg(null), 4000)
    } catch (err) {
      setModalError(err instanceof Error ? err.message : 'Error al asignar el vehículo')
    } finally {
      setAsignando(false)
    }
  }

  function startEdit(r: RepartidorResponseDTO) {
    setOpenMenuId(null)
    setEditingId(r.id)
    setEditState({ dni: r.dni, nombre: r.nombre, apellido: r.apellido, telefono: r.telefono, correoElectronico: r.correoElectronico })
  }

  function cancelEdit() { setEditingId(null) }

  async function saveEdit(id: number) {
    setSaving(true)
    try {
      const updated = await actualizarRepartidor(id, {
        idUsuario: repartidores.find(r => r.id === id)?.idUsuario,
        ...editState,
      })
      setRepartidores(prev => prev.map(r => r.id === id ? updated : r))
      setEditingId(null)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al guardar el repartidor')
    } finally {
      setSaving(false)
    }
  }

  async function handleDelete(id: number) {
    setOpenMenuId(null)
    if (!confirm('¿Eliminar este repartidor?')) return
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

  const nombreVehiculoActual = vehiculoActual
    ? (() => {
        const v = vehiculosDisponibles.find(v => v.id === vehiculoActual.idVehiculo)
        return v ? `${v.marca} ${v.modelo}` : `Vehículo #${vehiculoActual.idVehiculo}`
      })()
    : null

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
          <button style={{ marginLeft: '1rem', background: 'none', border: 'none', cursor: 'pointer', fontWeight: 700 }} onClick={() => setError(null)}>✕</button>
        </div>
      )}

      {successMsg && (
        <div style={{ marginBottom: '1rem', padding: '0.75rem 1rem', background: 'rgba(16,185,129,0.1)', border: '1px solid rgba(16,185,129,0.3)', borderRadius: 8, color: '#065f46', fontWeight: 500 }}>
          {successMsg}
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
                <th>DNI</th>
                <th>Nombre</th>
                <th>Apellido</th>
                <th>Teléfono</th>
                <th>Correo</th>
                <th>Estado</th>
                <th style={{ width: 60 }}></th>
              </tr>
            </thead>
            <tbody>
              {repartidores.length === 0 ? (
                <tr><td colSpan={8} className="empty-state">No hay repartidores registrados.</td></tr>
              ) : (
                repartidores.map(r => {
                  const isEditing = editingId === r.id
                  const isMenuOpen = openMenuId === r.id
                  return (
                    <tr key={r.id}>
                      <td>#{r.id}</td>
                      <td>
                        {isEditing
                          ? <input value={editState.dni} onChange={e => setEditState(s => ({ ...s, dni: e.target.value }))} />
                          : r.dni}
                      </td>
                      <td>
                        {isEditing
                          ? <input value={editState.nombre} onChange={e => setEditState(s => ({ ...s, nombre: e.target.value }))} />
                          : r.nombre}
                      </td>
                      <td>
                        {isEditing
                          ? <input value={editState.apellido} onChange={e => setEditState(s => ({ ...s, apellido: e.target.value }))} />
                          : r.apellido}
                      </td>
                      <td>
                        {isEditing
                          ? <input value={editState.telefono} onChange={e => setEditState(s => ({ ...s, telefono: e.target.value }))} />
                          : r.telefono}
                      </td>
                      <td>
                        {isEditing
                          ? <input type="email" value={editState.correoElectronico} onChange={e => setEditState(s => ({ ...s, correoElectronico: e.target.value }))} />
                          : r.correoElectronico}
                      </td>
                      <td>
                        <span className={`badge ${r.estado ? 'badge-activo' : 'badge-inactivo'}`}>
                          {r.estado ? 'Activo' : 'Inactivo'}
                        </span>
                      </td>
                      <td style={{ textAlign: 'center' }}>
                        {isEditing ? (
                          <div style={{ display: 'flex', gap: 4, justifyContent: 'center' }}>
                            <button className="btn btn-success btn-sm" onClick={() => saveEdit(r.id)} disabled={saving}>
                              {saving ? '…' : '✓'}
                            </button>
                            <button className="btn btn-secondary btn-sm" onClick={cancelEdit} disabled={saving}>✕</button>
                          </div>
                        ) : (
                          <>
                            <button
                              title="Acciones"
                              onClick={(e) => {
                                if (isMenuOpen) { setOpenMenuId(null); setMenuPos(null); return; }
                                const rect = (e.currentTarget as HTMLButtonElement).getBoundingClientRect();
                                setMenuPos({ top: rect.bottom + 4, left: rect.right - 160 });
                                setOpenMenuId(r.id);
                              }}
                              style={{
                                background: 'none', border: '1px solid #e5e7eb', borderRadius: 6,
                                padding: '4px 8px', cursor: 'pointer', fontSize: '1rem',
                                color: '#374151', lineHeight: 1,
                              }}
                            >
                              ⋮
                            </button>
                            {isMenuOpen && menuPos && (
                              <div
                                ref={menuRef}
                                style={{
                                  position: 'fixed', top: menuPos.top, left: menuPos.left, zIndex: 9999,
                                  background: 'white', border: '1px solid #e5e7eb',
                                  borderRadius: 8, boxShadow: '0 4px 16px rgba(0,0,0,0.12)',
                                  minWidth: 160, padding: '0.25rem 0',
                                }}
                              >
                                <button
                                  onClick={() => { startEdit(r); setOpenMenuId(null); setMenuPos(null); }}
                                  style={menuItemStyle}
                                >
                                  ✏️ Editar
                                </button>
                                {r.estado && (
                                  <button
                                    onClick={() => { openModal(r); setOpenMenuId(null); setMenuPos(null); }}
                                    style={menuItemStyle}
                                  >
                                    🚗 Asignar vehículo
                                  </button>
                                )}
                                <div style={{ borderTop: '1px solid #f3f4f6', margin: '0.25rem 0' }} />
                                <button
                                  onClick={() => { handleDelete(r.id); setOpenMenuId(null); setMenuPos(null); }}
                                  disabled={deletingId === r.id}
                                  style={{ ...menuItemStyle, color: '#ef4444' }}
                                >
                                  🗑️ {deletingId === r.id ? 'Eliminando…' : 'Eliminar'}
                                </button>
                              </div>
                            )}
                          </>
                        )}
                      </td>
                    </tr>
                  )
                })
              )}
            </tbody>
          </table>
        )}
      </div>

      {/* ── Modal asignación de vehículo ── */}
      {modalRepartidor && (
        <div
          ref={overlayRef}
          onClick={e => { if (e.target === overlayRef.current) closeModal() }}
          style={{ position: 'fixed', inset: 0, zIndex: 1000, background: 'rgba(0,0,0,0.45)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}
        >
          <div style={{ background: 'white', borderRadius: 12, padding: '2rem', width: '100%', maxWidth: 480, boxShadow: '0 20px 60px rgba(0,0,0,0.2)' }}>
            <h2 style={{ margin: '0 0 1.25rem', fontSize: '1.15rem', color: '#111827' }}>
              Asignar vehículo a {modalRepartidor.nombre} {modalRepartidor.apellido}
            </h2>

            {loadingModal ? (
              <p style={{ color: '#6b7280', textAlign: 'center', padding: '1.5rem 0' }}>Cargando datos…</p>
            ) : (
              <>
                {vehiculoActual && (
                  <div style={{ marginBottom: '1rem', padding: '0.875rem 1rem', background: '#eff6ff', border: '1px solid #bfdbfe', borderRadius: 8, fontSize: '0.875rem', color: '#1e40af' }}>
                    <strong>Ya tiene asignado: {nombreVehiculoActual}</strong>
                    {' '}(el {new Date(vehiculoActual.fechaAsignacion).toLocaleDateString('es-CO')})
                    <br /><span style={{ color: '#3b82f6' }}>Selecciona otro para reasignar.</span>
                  </div>
                )}
                {modalError && (
                  <div style={{ marginBottom: '1rem', padding: '0.75rem 1rem', background: '#fef2f2', border: '1px solid #fecaca', borderRadius: 8, fontSize: '0.875rem', color: '#991b1b' }}>
                    {modalError}
                  </div>
                )}
                {vehiculosDisponibles.length === 0 ? (
                  <p style={{ color: '#6b7280', fontSize: '0.875rem', margin: '0 0 1rem' }}>No hay vehículos activos disponibles.</p>
                ) : (
                  <div style={{ marginBottom: '1.25rem' }}>
                    <label style={{ display: 'block', fontSize: '0.875rem', fontWeight: 600, marginBottom: '0.4rem', color: '#374151' }}>Vehículo</label>
                    <select
                      value={selectedVehiculoId}
                      onChange={e => setSelectedVehiculoId(e.target.value === '' ? '' : Number(e.target.value))}
                      style={{ width: '100%', padding: '0.6rem 0.75rem', borderRadius: 8, border: '1px solid #d1d5db', fontSize: '0.9rem', background: 'white', color: '#111827' }}
                    >
                      <option value="">Selecciona un vehículo</option>
                      {vehiculosDisponibles.map(v => (
                        <option key={v.id} value={v.id}>
                          {v.marca} {v.modelo} — {v.tipoPropulsion} — Cap: {v.capacidadPeso}kg / {v.capacidadVolumen}m³
                        </option>
                      ))}
                    </select>
                  </div>
                )}
                <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end' }}>
                  <button className="btn btn-secondary" onClick={closeModal} disabled={asignando} style={{ minWidth: 90 }}>Cancelar</button>
                  <button className="btn btn-primary" onClick={handleAsignar} disabled={selectedVehiculoId === '' || asignando} style={{ minWidth: 90 }}>
                    {asignando
                      ? <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem' }}>
                          <span style={{ width: 12, height: 12, border: '2px solid rgba(255,255,255,0.4)', borderTopColor: 'white', borderRadius: '50%', animation: 'spin 0.7s linear infinite', display: 'inline-block' }} />
                          Asignando…
                        </span>
                      : 'Asignar'}
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}

      <style>{`@keyframes spin { to { transform: rotate(360deg); } }`}</style>
    </div>
  )
}

const menuItemStyle: React.CSSProperties = {
  display: 'block', width: '100%', textAlign: 'left',
  padding: '0.5rem 1rem', background: 'none', border: 'none',
  cursor: 'pointer', fontSize: '0.875rem', color: '#374151',
  whiteSpace: 'nowrap',
}
