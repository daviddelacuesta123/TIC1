import { useState, useEffect, type ReactElement } from 'react';
import { useAuth } from '../../context/AuthContext';
import type { Page } from '../../App';
import RepartidorDashboard from '../../pages/RepartidorDashboard';
import RepartidorPedidos from '../../pages/RepartidorPedidos';
import RepartidorMapa from '../../pages/RepartidorMapa';
import './RepartidorLayout.css';

interface Props {
  currentPage: Page;
  onNavigate: (page: Page) => void;
}

const NAV_ITEMS: { page: Page; label: string; icon: ReactElement }[] = [
  {
    page: 'dashboard',
    label: 'Dashboard',
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/>
        <rect x="14" y="14" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/>
      </svg>
    ),
  },
  {
    page: 'repartidor-mapa',
    label: 'Mi Ruta',
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <polygon points="3 6 9 3 15 6 21 3 21 18 15 21 9 18 3 21"/>
        <line x1="9" y1="3" x2="9" y2="18"/><line x1="15" y1="6" x2="15" y2="21"/>
      </svg>
    ),
  },
  {
    page: 'repartidor-pedidos',
    label: 'Historial',
    icon: (
      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
        <polyline points="14 2 14 8 20 8"/>
        <line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/>
      </svg>
    ),
  },
];

const PAGE_TITLES: Partial<Record<Page, string>> = {
  dashboard: 'Dashboard',
  'repartidor-mapa': 'Mi Ruta',
  'repartidor-pedidos': 'Historial',
};

export default function RepartidorLayout({ currentPage, onNavigate }: Props) {
  const { user, logout } = useAuth();
  const [drawerOpen, setDrawerOpen] = useState(false);
  const [time, setTime] = useState(() =>
    new Date().toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' })
  );

  useEffect(() => {
    const t = setInterval(() =>
      setTime(new Date().toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' }))
    , 30000);
    return () => clearInterval(t);
  }, []);

  const navigate = (page: Page) => { onNavigate(page); setDrawerOpen(false); };
  const initials = (user?.username ?? 'R').slice(0, 2).toUpperCase();

  return (
    <div className="rep-bg">
      <div className="rep-frame">

        {/* Status bar */}
        <div className="rep-status-bar">
          <span>{time}</span>
          <div className="rep-status-icons">
            <svg width="13" height="11" viewBox="0 0 13 11" fill="currentColor">
              <rect x="0" y="7" width="2.2" height="4" rx="0.4"/>
              <rect x="3.2" y="5" width="2.2" height="6" rx="0.4"/>
              <rect x="6.4" y="2.5" width="2.2" height="8.5" rx="0.4"/>
              <rect x="9.6" y="0" width="2.2" height="11" rx="0.4"/>
            </svg>
            <svg width="13" height="11" viewBox="0 0 22 18" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round">
              <path d="M1 5C5 1 9 0 11 0s6 1 10 5"/><path d="M4 9c2-2.5 4.5-4 7-4s5 1.5 7 4"/>
              <path d="M7.5 13c1-1.5 2.2-2.2 3.5-2.2s2.5.7 3.5 2.2"/>
              <circle cx="11" cy="17" r="1.5" fill="currentColor"/>
            </svg>
            <svg width="19" height="11" viewBox="0 0 22 12" fill="none">
              <rect x="0.5" y="0.5" width="18" height="11" rx="2.5" stroke="currentColor" strokeWidth="1.2"/>
              <rect x="2" y="2" width="14" height="8" rx="1.5" fill="currentColor"/>
              <path d="M19.5 4v4" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
            </svg>
          </div>
        </div>

        {/* Top bar */}
        <header className="rep-topbar">
          <button className="rep-hamburger" onClick={() => setDrawerOpen(true)} aria-label="Abrir menú">
            <span/><span/><span/>
          </button>
          <div className="rep-topbar-title">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
              <rect width="16" height="16" x="4" y="4" rx="2"/>
              <rect width="7" height="7" x="8" y="8" rx="1"/>
              <path d="M4 12V8"/><path d="M20 12V8"/>
            </svg>
            {PAGE_TITLES[currentPage] ?? 'RouteOptimizer'}
          </div>
          <div className="rep-topbar-avatar">{initials}</div>
        </header>

        {/* Page content */}
        <div className="rep-content">
          {currentPage === 'dashboard'          && <RepartidorDashboard onNavigate={navigate} />}
          {currentPage === 'repartidor-pedidos' && <RepartidorPedidos />}
          {currentPage === 'repartidor-mapa'    && <RepartidorMapa />}
        </div>

        {/* Bottom nav */}
        <nav className="rep-bottom-nav">
          {NAV_ITEMS.map(item => (
            <button
              key={item.page}
              className={`rep-bottom-btn ${currentPage === item.page ? 'active' : ''}`}
              onClick={() => navigate(item.page)}
            >
              {item.icon}
              <span>{item.label}</span>
            </button>
          ))}
        </nav>

        {/* Overlay */}
        <div className={`rep-overlay ${drawerOpen ? 'visible' : ''}`} onClick={() => setDrawerOpen(false)} />

        {/* Drawer */}
        <aside className={`rep-drawer ${drawerOpen ? 'open' : ''}`}>
          <div className="rep-drawer-header">
            <div className="rep-drawer-avatar">{initials}</div>
            <div>
              <div className="rep-drawer-username">{user?.username}</div>
              <div className="rep-drawer-role">Repartidor</div>
            </div>
            <button className="rep-drawer-close" onClick={() => setDrawerOpen(false)}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>

          <nav className="rep-drawer-nav">
            {NAV_ITEMS.map(item => (
              <button
                key={item.page}
                className={`rep-drawer-item ${currentPage === item.page ? 'active' : ''}`}
                onClick={() => navigate(item.page)}
              >
                <span className="rep-drawer-item-icon">{item.icon}</span>
                <span>{item.label}</span>
                <svg className="rep-drawer-chevron" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                  <polyline points="9 18 15 12 9 6"/>
                </svg>
              </button>
            ))}
          </nav>

          <div className="rep-drawer-footer">
            <button className="rep-drawer-logout" onClick={logout}>
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
                <polyline points="16 17 21 12 16 7"/>
                <line x1="21" y1="12" x2="9" y2="12"/>
              </svg>
              Cerrar sesión
            </button>
          </div>
        </aside>

      </div>
    </div>
  );
}
