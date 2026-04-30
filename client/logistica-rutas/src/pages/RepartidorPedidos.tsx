import './RepartidorPedidos.css';

const mockPedidos = [
  { id: 'ORD-7829', direccion: 'Calle 123 #45-67, Norte', fecha: '2026-04-30 10:15 AM', tiempo: '22 min', estado: 'Entregado' },
  { id: 'ORD-7830', direccion: 'Cra 15 #80-21, Centro', fecha: '2026-04-30 10:45 AM', tiempo: '28 min', estado: 'Entregado' },
  { id: 'ORD-7831', direccion: 'Av Siempre Viva 742', fecha: '2026-04-30 11:20 AM', tiempo: '18 min', estado: 'Entregado' },
  { id: 'ORD-7832', direccion: 'Calle 45 #12-34, Sur', fecha: '2026-04-29 03:10 PM', tiempo: '25 min', estado: 'Entregado' },
  { id: 'ORD-7833', direccion: 'Cra 7 #22-10, Centro', fecha: '2026-04-29 04:30 PM', tiempo: '30 min', estado: 'Entregado' },
];

export default function RepartidorPedidos() {
  return (
    <div className="rep-pedidos-container">
      <div className="rep-pedidos-header">
        <h1>Historial de Pedidos</h1>
        <p>Listado de los pedidos que has entregado recientemente</p>
      </div>

      <div className="pedidos-list">
        {mockPedidos.map((pedido) => (
          <div key={pedido.id} className="pedido-card">
            <div className="pedido-info">
              <h3>{pedido.id}</h3>
              <p className="pedido-dir">
                <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                  <path d="M21 10c0 7-9 13-9 13s-9-6-9-13a9 9 0 0 1 18 0z"></path>
                  <circle cx="12" cy="10" r="3"></circle>
                </svg>
                {pedido.direccion}
              </p>
            </div>
            <div className="pedido-meta">
              <div className="pedido-meta-item">
                <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                  <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                  <line x1="16" y1="2" x2="16" y2="6"></line>
                  <line x1="8" y1="2" x2="8" y2="6"></line>
                  <line x1="3" y1="10" x2="21" y2="10"></line>
                </svg>
                {pedido.fecha}
              </div>
              <div className="pedido-meta-item">
                <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                  <circle cx="12" cy="12" r="10"></circle>
                  <polyline points="12 6 12 12 16 14"></polyline>
                </svg>
                {pedido.tiempo}
              </div>
            </div>
            <div className="pedido-status status-entregado">
              {pedido.estado}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
