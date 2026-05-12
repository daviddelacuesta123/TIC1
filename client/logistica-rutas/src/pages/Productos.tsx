import { useState, useEffect } from 'react'
import type { Page } from '../App'
import { listarProductos, eliminarProducto, type ProductoResponse } from '../services/productoService'

interface ProductosProps {
  onNavigate: (page: Page) => void
}

export default function Productos({ onNavigate }: ProductosProps) {
  const [productos, setProductos] = useState<ProductoResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    listarProductos()
      .then(setProductos)
      .catch(e => setError(e instanceof Error ? e.message : 'Error al cargar productos'))
      .finally(() => setLoading(false))
  }, [])

  async function handleEliminar(id: number) {
    if (!confirm('¿Eliminar este producto?')) return
    try {
      await eliminarProducto(id)
      setProductos(prev => prev.filter(p => p.id !== id))
    } catch (e) {
      alert(e instanceof Error ? e.message : 'Error al eliminar')
    }
  }

  return (
    <div className="page-container">
      <div className="page-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>Productos</h1>
        <button className="btn btn-primary" onClick={() => onNavigate('new-producto')}>
          + Nuevo producto
        </button>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {loading ? (
        <p style={{ color: '#6b7280', padding: '2rem' }}>Cargando productos…</p>
      ) : productos.length === 0 ? (
        <div style={{
          textAlign: 'center', padding: '3rem', color: '#9ca3af',
          background: '#f9fafb', borderRadius: 12, border: '1px dashed #e5e7eb',
        }}>
          <p style={{ fontSize: '1.1rem', marginBottom: '0.5rem' }}>No hay productos registrados</p>
          <p style={{ fontSize: '0.875rem' }}>Crea el primero para poder usarlos en los pedidos</p>
        </div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Nombre</th>
                <th>Peso (kg)</th>
                <th>Volumen (m³)</th>
                <th>Acciones</th>
              </tr>
            </thead>
            <tbody>
              {productos.map(p => (
                <tr key={p.id}>
                  <td>{p.id}</td>
                  <td>{p.nombre}</td>
                  <td>{p.peso.toFixed(2)}</td>
                  <td>{p.volumen.toFixed(4)}</td>
                  <td>
                    <button
                      className="btn btn-secondary"
                      style={{ fontSize: '0.8rem', padding: '0.3rem 0.75rem', color: '#ef4444', borderColor: '#fca5a5' }}
                      onClick={() => handleEliminar(p.id)}
                    >
                      Eliminar
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  )
}
