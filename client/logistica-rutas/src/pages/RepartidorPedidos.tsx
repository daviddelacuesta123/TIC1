import { useEffect, useState } from 'react';
import './RepartidorPedidos.css';
import { listarRutasRepartidor } from '../services/rutaService';
import { listarPedidosPorRuta, type PedidoResponse } from '../services/pedidoService';

const ESTADO_STYLES: Record<string, { bg: string; color: string; label: string }> = {
  ENTREGADO:  { bg: 'rgba(16,185,129,0.1)',  color: '#10b981', label: 'Entregado' },
  EN_CAMINO:  { bg: 'rgba(59,130,246,0.1)',  color: '#3b82f6', label: 'En camino' },
  PENDIENTE:  { bg: 'rgba(156,163,175,0.1)', color: '#6b7280', label: 'Pendiente' },
  FALLIDO:    { bg: 'rgba(239,68,68,0.1)',   color: '#ef4444', label: 'Fallido' },
  CANCELADO:  { bg: 'rgba(55,65,81,0.1)',    color: '#374151', label: 'Cancelado' },
};

export default function RepartidorPedidos() {
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function cargar() {
      try {
        const rutas = await listarRutasRepartidor();
        if (rutas.length === 0) { setLoading(false); return; }
        const resultados = await Promise.all(rutas.map(r => listarPedidosPorRuta(r.repartidorId)));
        setPedidos(resultados.flat());
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Error al cargar el historial');
      } finally {
        setLoading(false);
      }
    }
    cargar();
  }, []);

  return (
    <div className="rep-pedidos-container">
      <div className="rep-pedidos-header">
        <h1>Historial de Pedidos</h1>
        <p>Listado de los pedidos de tus rutas asignadas</p>
      </div>

      {loading && <p style={{ color: '#6b7280', textAlign: 'center', padding: '2rem' }}>Cargando historial…</p>}

      {error && (
        <div style={{ background: 'rgba(239,68,68,0.1)', border: '1px solid #ef4444', borderRadius: 8, padding: '1rem', color: '#ef4444' }}>
          {error}
        </div>
      )}

      {!loading && !error && pedidos.length === 0 && (
        <div style={{ textAlign: 'center', color: '#6b7280', padding: '3rem' }}>
          No hay entregas registradas para esta ruta
        </div>
      )}

      <div className="pedidos-list">
        {pedidos.map(pedido => {
          const est = ESTADO_STYLES[pedido.estado] ?? ESTADO_STYLES.PENDIENTE;
          return (
            <div key={pedido.id} className="pedido-card">
              <div className="pedido-info">
                <h3>#{pedido.id} — {pedido.destinatario.nombre} {pedido.destinatario.apellido}</h3>
                <p className="pedido-dir">
                  <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                    <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z" />
                    <circle cx="12" cy="10" r="3" />
                  </svg>
                  {pedido.direccion.direccionTexto}, {pedido.direccion.ciudad}
                </p>
              </div>
              <div className="pedido-meta">
                <div className="pedido-meta-item">
                  <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2" />
                    <line x1="16" y1="2" x2="16" y2="6" /><line x1="8" y1="2" x2="8" y2="6" />
                    <line x1="3" y1="10" x2="21" y2="10" />
                  </svg>
                  {new Date(pedido.fechaCreacion).toLocaleString('es-CO')}
                </div>
                <div className="pedido-meta-item">
                  <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                    <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
                  </svg>
                  {pedido.pesoTotal} kg
                </div>
              </div>
              <div
                className="pedido-status"
                style={{ background: est.bg, color: est.color }}
              >
                {est.label}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
