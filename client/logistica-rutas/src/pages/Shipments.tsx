import { useCallback, useEffect, useState } from 'react'
import '../App.css'
import { listarPedidos, geocodificarPedido, type PedidoResponse } from '../services/pedidoService'

interface ShipmentsProps {
  onNavigate: (page: 'new-shipment') => void
}

const ESTADO_CLASS: Record<string, string> = {
  PENDIENTE:  'badge-pendiente',
  EN_CAMINO:  'badge-en-transito',
  ENTREGADO:  'badge-entregado',
  FALLIDO:    'badge-fallido',
  CANCELADO:  'badge-fallido',
}

export default function Shipments({ onNavigate }: ShipmentsProps) {
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [geocodingId, setGeocodingId] = useState<number | null>(null)

  const fetchPedidos = useCallback(async () => {
    setLoading(true)
    setError(null)
    try {
      setPedidos(await listarPedidos())
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar los pedidos')
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { fetchPedidos() }, [fetchPedidos])

  async function handleGeocodificar(id: number) {
    setGeocodingId(id)
    try {
      const actualizado = await geocodificarPedido(id)
      setPedidos(prev => prev.map(p => p.id === id ? actualizado : p))
    } catch (err) {
      setError(err instanceof Error ? err.message : `Error al geocodificar pedido #${id}`)
    } finally {
      setGeocodingId(null)
    }
  }

  return (
    <>
      <div className="page-header">
        <h1>Pedidos</h1>
        <button className="btn-primary" onClick={() => onNavigate('new-shipment')}>
          + Nuevo pedido
        </button>
      </div>

      {error && (
        <div className="error-banner" style={{ marginBottom: '1rem' }}>
          {error}
          <button style={{ marginLeft: '1rem', background: 'none', border: 'none', cursor: 'pointer', fontWeight: 700 }} onClick={() => setError(null)}>✕</button>
        </div>
      )}

      <div className="table-container">
        {loading ? (
          <p className="empty-state">Cargando pedidos…</p>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Destinatario</th>
                <th>Dirección</th>
                <th>Peso (kg)</th>
                <th>Estado</th>
                <th>Geocodificado</th>
                <th>Fecha</th>
              </tr>
            </thead>
            <tbody>
              {pedidos.length === 0 ? (
                <tr><td colSpan={7} className="empty-state">No hay pedidos registrados.</td></tr>
              ) : (
                pedidos.map(p => {
                  const geocodificado = p.direccion.latitud != null && p.direccion.latitud !== 0
                  return (
                    <tr key={p.id}>
                      <td className="tracking-id">#{p.id}</td>
                      <td>{p.destinatario.nombre} {p.destinatario.apellido}</td>
                      <td style={{ maxWidth: 220, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                        {p.direccion.direccionTexto}, {p.direccion.ciudad}
                      </td>
                      <td>{p.pesoTotal?.toFixed(2)}</td>
                      <td>
                        <span className={`badge ${ESTADO_CLASS[p.estado] ?? 'badge-pendiente'}`}>
                          {p.estado}
                        </span>
                      </td>
                      <td style={{ textAlign: 'center' }}>
                        {geocodificado ? (
                          <span style={{ color: '#059669', fontWeight: 600 }}>✓</span>
                        ) : (
                          <button
                            onClick={() => handleGeocodificar(p.id)}
                            disabled={geocodingId === p.id}
                            style={{
                              fontSize: '0.75rem', padding: '0.25rem 0.6rem',
                              background: '#fef3c7', border: '1px solid #fcd34d',
                              borderRadius: 6, cursor: geocodingId === p.id ? 'wait' : 'pointer',
                              color: '#92400e', fontWeight: 500,
                              whiteSpace: 'nowrap',
                            }}
                          >
                            {geocodingId === p.id ? 'Geocodificando…' : 'Geocodificar'}
                          </button>
                        )}
                      </td>
                      <td>{p.fechaCreacion ? new Date(p.fechaCreacion).toLocaleDateString('es-CO') : '—'}</td>
                    </tr>
                  )
                })
              )}
            </tbody>
          </table>
        )}
      </div>
    </>
  )
}
