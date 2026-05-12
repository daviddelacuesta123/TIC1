import { useState, useEffect, type ChangeEvent, type FormEvent } from 'react'
import '../App.css'
import { crearPedido } from '../services/pedidoService'
import { listarProductos, type ProductoResponse } from '../services/productoService'

interface ProductoLinea {
  idProducto: number
  cantidad: number
}

interface NewShipmentProps {
  onNavigate: (page: 'shipments' | 'new-producto') => void
}

export default function NewShipment({ onNavigate }: NewShipmentProps) {
  const [nombre, setNombre] = useState('')
  const [apellido, setApellido] = useState('')
  const [dni, setDni] = useState('')
  const [telefono, setTelefono] = useState('')
  const [correo, setCorreo] = useState('')

  const [direccionTexto, setDireccionTexto] = useState('')
  const [ciudad, setCiudad] = useState('Medellin')
  const [pais, setPais] = useState('Colombia')

  const [catalogoProductos, setCatalogoProductos] = useState<ProductoResponse[]>([])
  const [loadingProductos, setLoadingProductos] = useState(true)
  const [productos, setProductos] = useState<ProductoLinea[]>([])

  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState(false)

  useEffect(() => {
    listarProductos()
      .then(lista => {
        setCatalogoProductos(lista)
        if (lista.length > 0) {
          setProductos([{ idProducto: lista[0].id, cantidad: 1 }])
        }
      })
      .catch(() => setError('No se pudo cargar el catálogo de productos'))
      .finally(() => setLoadingProductos(false))
  }, [])

  // — Validaciones inline —
  const dniValido = /^\d{6,}$/.test(dni)
  const telefonoValido = /^\d{10}$/.test(telefono)
  const correoValido = correo.includes('@') && correo.includes('.')

  const formularioValido =
    nombre.trim() !== '' &&
    apellido.trim() !== '' &&
    dniValido &&
    telefonoValido &&
    correoValido &&
    direccionTexto.trim() !== '' &&
    ciudad.trim() !== '' &&
    productos.length > 0

  // — Resumen peso / volumen —
  const pesoTotal = productos.reduce((acc, p) => {
    const prod = catalogoProductos.find(pr => pr.id === p.idProducto)
    return acc + (prod ? prod.peso * p.cantidad : 0)
  }, 0)
  const volumenTotal = productos.reduce((acc, p) => {
    const prod = catalogoProductos.find(pr => pr.id === p.idProducto)
    return acc + (prod ? prod.volumen * p.cantidad : 0)
  }, 0)

  // — Ids ya usados en la lista —
  const idsUsados = productos.map(p => p.idProducto)

  function agregarProducto() {
    const disponibles = catalogoProductos.filter(p => !idsUsados.includes(p.id))
    if (disponibles.length === 0) return
    setProductos(prev => [...prev, { idProducto: disponibles[0].id, cantidad: 1 }])
  }

  function eliminarProducto(idx: number) {
    setProductos(prev => prev.filter((_, i) => i !== idx))
  }

  function cambiarProducto(idx: number, idProducto: number) {
    setProductos(prev => prev.map((p, i) => i === idx ? { ...p, idProducto } : p))
  }

  function cambiarCantidad(idx: number, cantidad: number) {
    setProductos(prev => prev.map((p, i) => i === idx ? { ...p, cantidad: Math.max(1, cantidad) } : p))
  }

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    if (!formularioValido) return
    setSubmitting(true)
    setError(null)
    try {
      await crearPedido({
        destinatario: { nombre, apellido, dni, telefono, correoElectronico: correo },
        direccion: { direccionTexto, ciudad, pais },
        productos: productos.map(p => ({ idProducto: p.idProducto, cantidad: p.cantidad })),
        pesoTotal,
        volumenTotal,
      })
      setSuccess(true)
      setTimeout(() => onNavigate('shipments'), 1500)
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al crear el pedido')
    } finally {
      setSubmitting(false)
    }
  }

  if (success) {
    return (
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: '60vh' }}>
        <div style={{
          background: 'rgba(16,185,129,0.08)', border: '1px solid rgba(16,185,129,0.3)',
          borderRadius: 12, padding: '2rem 3rem', textAlign: 'center', color: '#065f46',
        }}>
          <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>✓</div>
          <strong>Pedido creado correctamente</strong>
          <p style={{ margin: '0.5rem 0 0', color: '#6b7280', fontSize: '0.9rem' }}>Redirigiendo…</p>
        </div>
      </div>
    )
  }

  return (
    <>
      <div className="page-header">
        <h1>Nuevo Pedido</h1>
      </div>

      {error && (
        <div className="error-banner" style={{ marginBottom: '1rem' }}>
          {error}
          <button
            style={{ marginLeft: '1rem', background: 'none', border: 'none', cursor: 'pointer', fontWeight: 700 }}
            onClick={() => setError(null)}
          >✕</button>
        </div>
      )}

      <div className="form-container">
        <form className="create-form" onSubmit={handleSubmit}>

          {/* ── SECCIÓN 1: Destinatario ── */}
          <div className="form-section-title">
            <h2>Datos del destinatario</h2>
          </div>

          <div className="form-row">
            <div className="form-col">
              <label htmlFor="nombre">Nombre *</label>
              <input
                id="nombre" type="text" value={nombre}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setNombre(e.target.value)}
                placeholder="Nombre del destinatario" required
              />
            </div>
            <div className="form-col">
              <label htmlFor="apellido">Apellido *</label>
              <input
                id="apellido" type="text" value={apellido}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setApellido(e.target.value)}
                placeholder="Apellido del destinatario" required
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-col">
              <label htmlFor="dni">DNI / Cédula *</label>
              <input
                id="dni" type="text" value={dni}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setDni(e.target.value.replace(/\D/g, ''))}
                placeholder="Número de documento" required
                style={dni && !dniValido ? { borderColor: '#ef4444' } : {}}
              />
              {dni && !dniValido && (
                <span style={{ fontSize: '0.75rem', color: '#ef4444' }}>Mínimo 6 dígitos numéricos</span>
              )}
            </div>
            <div className="form-col">
              <label htmlFor="telefono">Teléfono *</label>
              <input
                id="telefono" type="tel" value={telefono}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setTelefono(e.target.value.replace(/\D/g, '').slice(0, 10))}
                placeholder="Ej: 3001234567" required
                style={telefono && !telefonoValido ? { borderColor: '#ef4444' } : {}}
              />
              {telefono && !telefonoValido && (
                <span style={{ fontSize: '0.75rem', color: '#ef4444' }}>Debe ser exactamente 10 dígitos</span>
              )}
            </div>
          </div>

          <div className="form-row">
            <div className="form-col" style={{ flex: 1 }}>
              <label htmlFor="correo">Correo electrónico *</label>
              <input
                id="correo" type="email" value={correo}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setCorreo(e.target.value)}
                placeholder="correo@ejemplo.com" required
              />
            </div>
          </div>

          {/* ── SECCIÓN 2: Dirección ── */}
          <div className="form-section-title" style={{ marginTop: '1.5rem' }}>
            <h2>Dirección de entrega</h2>
          </div>

          <div className="form-row">
            <div className="form-col" style={{ flex: 1 }}>
              <label htmlFor="direccionTexto">Dirección completa *</label>
              <input
                id="direccionTexto" type="text" value={direccionTexto}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setDireccionTexto(e.target.value)}
                placeholder="Ej: Calle 10 # 43-12, El Poblado" required
              />
              <span style={{ fontSize: '0.75rem', color: '#9ca3af', marginTop: 4, display: 'block' }}>
                Incluye barrio o referencia para una geocodificación más precisa
              </span>
            </div>
          </div>

          <div className="form-row">
            <div className="form-col">
              <label htmlFor="ciudad">Ciudad *</label>
              <input
                id="ciudad" type="text" value={ciudad}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setCiudad(e.target.value)}
                required
              />
            </div>
            <div className="form-col">
              <label htmlFor="pais">País</label>
              <input
                id="pais" type="text" value={pais}
                onChange={(e: ChangeEvent<HTMLInputElement>) => setPais(e.target.value)}
              />
            </div>
          </div>

          {/* ── SECCIÓN 3: Productos ── */}
          <div className="form-section-title" style={{ marginTop: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h2>Productos</h2>
          </div>

          {loadingProductos ? (
            <p style={{ color: '#6b7280', fontSize: '0.9rem' }}>Cargando catálogo…</p>
          ) : catalogoProductos.length === 0 ? (
            <div style={{ padding: '1rem', background: '#fef3c7', border: '1px solid #fcd34d', borderRadius: 8, fontSize: '0.875rem', color: '#92400e' }}>
              No hay productos registrados. <button type="button" style={{ background: 'none', border: 'none', color: '#1d4ed8', cursor: 'pointer', textDecoration: 'underline', padding: 0 }} onClick={() => onNavigate('new-producto')}>Crea uno primero</button>.
            </div>
          ) : (
            <>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
                {productos.map((linea, idx) => {
                  const otrosIds = idsUsados.filter((_, i) => i !== idx)
                  return (
                    <div key={idx} style={{
                      display: 'flex', gap: '0.75rem', alignItems: 'center',
                      background: '#f9fafb', border: '1px solid #e5e7eb',
                      borderRadius: 8, padding: '0.75rem',
                    }}>
                      <select
                        value={linea.idProducto}
                        onChange={(e: ChangeEvent<HTMLSelectElement>) => cambiarProducto(idx, Number(e.target.value))}
                        style={{
                          flex: 1, padding: '0.5rem 0.75rem', borderRadius: 8,
                          border: '1px solid #d1d5db', fontSize: '0.9rem',
                          background: 'white', color: '#111827',
                        }}
                      >
                        {catalogoProductos.map(p => (
                          <option key={p.id} value={p.id} disabled={otrosIds.includes(p.id)}>
                            {p.nombre} — {p.peso} kg
                          </option>
                        ))}
                      </select>

                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <label style={{ fontSize: '0.82rem', color: '#6b7280', whiteSpace: 'nowrap' }}>
                          Cant.
                        </label>
                        <input
                          type="number" min={1} value={linea.cantidad}
                          onChange={(e: ChangeEvent<HTMLInputElement>) => cambiarCantidad(idx, Number(e.target.value))}
                          style={{
                            width: 70, padding: '0.5rem', borderRadius: 8,
                            border: '1px solid #d1d5db', fontSize: '0.9rem', textAlign: 'center',
                          }}
                        />
                      </div>

                      <button
                        type="button"
                        onClick={() => eliminarProducto(idx)}
                        disabled={productos.length === 1}
                        style={{
                          background: 'none', border: '1px solid #e5e7eb', borderRadius: 6,
                          padding: '0.4rem 0.6rem', cursor: productos.length === 1 ? 'not-allowed' : 'pointer',
                          color: '#ef4444', fontSize: '0.9rem', opacity: productos.length === 1 ? 0.4 : 1,
                        }}
                        title="Eliminar línea"
                      >✕</button>
                    </div>
                  )
                })}
              </div>
            </>
          )}

          {!loadingProductos && catalogoProductos.length > 0 && productos.length < catalogoProductos.length && (
            <button
              type="button"
              className="btn-secondary"
              onClick={agregarProducto}
              style={{ marginTop: '0.5rem', fontSize: '0.85rem' }}
            >
              + Agregar producto
            </button>
          )}

          <div style={{
            marginTop: '0.75rem', padding: '0.75rem 1rem',
            background: '#f0fbff', border: '1px solid #bae6fd',
            borderRadius: 8, fontSize: '0.85rem', color: '#374151',
            display: 'flex', gap: '2rem',
          }}>
            <span>Peso total estimado: <strong>{pesoTotal.toFixed(1)} kg</strong></span>
            <span>Volumen total estimado: <strong>{volumenTotal.toFixed(3)} m³</strong></span>
          </div>

          {/* ── Botones ── */}
          <div className="form-actions" style={{ marginTop: '1.5rem' }}>
            <button
              type="submit"
              className="btn-primary"
              disabled={!formularioValido || submitting}
              style={{ background: formularioValido && !submitting ? '#10b981' : undefined }}
            >
              {submitting
                ? <span style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                    <span style={{
                      width: 14, height: 14, border: '2px solid rgba(255,255,255,0.4)',
                      borderTopColor: 'white', borderRadius: '50%',
                      animation: 'spin 0.7s linear infinite', display: 'inline-block',
                    }} />
                    Creando…
                  </span>
                : 'Crear pedido'}
            </button>
            <button
              type="button"
              className="btn-secondary"
              onClick={() => onNavigate('shipments')}
              disabled={submitting}
            >
              Cancelar
            </button>
          </div>

        </form>
      </div>

      <style>{`
        @keyframes spin { to { transform: rotate(360deg); } }
      `}</style>
    </>
  )
}
