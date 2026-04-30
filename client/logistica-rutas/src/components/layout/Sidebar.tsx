import type { Page } from '../../App'
import { useAuth } from '../../context/AuthContext'
interface SidebarProps {
  currentPage: Page
  onNavigate: (page: Page) => void
}

export default function Sidebar({ currentPage, onNavigate }: SidebarProps) {
  const { user } = useAuth()
  const isRepartidor = user?.rol === 'REPARTIDOR'

  return (
    <nav className="sidebar">
      <div className="sidebar-brand" style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '2rem' }}>
        <svg
            className="brand-icon"
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
            style={{ width: '28px', height: '28px', color: 'var(--accent-color)' }}
        >
            <rect width="16" height="16" x="4" y="4" rx="2" />
            <rect width="7" height="7" x="8" y="8" rx="1" />
            <path d="M4 12V8" />
            <path d="M20 12V8" />
        </svg>
        <span style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--accent-color)', letterSpacing: '-0.5px' }}>RouteOptimizer</span>
      </div>
      <ul>
        <li className={currentPage === 'dashboard' ? 'active' : ''} onClick={() => onNavigate('dashboard')}>Dashboard</li>
        
        {isRepartidor && (
          <li className={currentPage === 'repartidor-pedidos' ? 'active' : ''} onClick={() => onNavigate('repartidor-pedidos')}>Historial de Pedidos</li>
        )}
        
        {!isRepartidor && (
          <>
            <li className={currentPage === 'shipments' ? 'active' : ''} onClick={() => onNavigate('shipments')}>Shipments</li>
            <li className={currentPage === 'routes' ? 'active' : ''} onClick={() => onNavigate('routes')}>Routes Map</li>
            <li className={currentPage === 'vehicles' ? 'active' : ''} onClick={() => onNavigate('vehicles')}>Vehicles</li>
            <li className={currentPage === 'repartidores' ? 'active' : ''} onClick={() => onNavigate('repartidores')}>Repartidores</li>
          </>
        )}
      </ul>
    </nav>
  )
}
