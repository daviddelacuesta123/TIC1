import { useState, useEffect } from 'react';
import type { Page } from '../../App';
import './RepartidorDashboard.css';

interface Props {
  onNavigate: (page: Page) => void;
}

export default function RepartidorDashboard({ onNavigate }: Props) {
  const [currentDate, setCurrentDate] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setCurrentDate(new Date()), 60000);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="rep-dashboard-container">
      <div className="rep-dashboard-header">
        <div>
          <h1>Panel de Repartidor</h1>
          <p>Tus métricas de rendimiento y estado actual</p>
        </div>
        <div className="rep-dashboard-date">
          <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
          {currentDate.toLocaleDateString('es-ES', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
        </div>
      </div>

      <div className="rep-metrics-grid">
        <div className="rep-card clickable-card" onClick={() => onNavigate('repartidor-pedidos')}>
          <div className="rep-card-header">
            <h3>Pedidos Realizados</h3>
            <div className="rep-icon icon-success">
               <svg width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                 <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                 <polyline points="22 4 12 14.01 9 11.01"></polyline>
               </svg>
            </div>
          </div>
          <div className="rep-card-value">45</div>
          <p className="rep-card-subtitle">En lo que va de la semana</p>
        </div>

        <div className="rep-card">
          <div className="rep-card-header">
            <h3>Tiempo Promedio por Pedido</h3>
            <div className="rep-icon icon-warning">
               <svg width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                 <circle cx="12" cy="12" r="10"></circle>
                 <polyline points="12 6 12 12 16 14"></polyline>
               </svg>
            </div>
          </div>
          <div className="rep-card-value">24 <span className="rep-value-unit">min</span></div>
          <p className="rep-card-subtitle">-3 min respecto al promedio general</p>
        </div>
        
        <div className="rep-card">
          <div className="rep-card-header">
            <h3>Ruta Actual</h3>
            <div className="rep-icon icon-info">
               <svg width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                 <polygon points="3 6 9 3 15 6 21 3 21 18 15 21 9 18 3 21"></polygon>
                 <line x1="9" y1="3" x2="9" y2="18"></line>
                 <line x1="15" y1="6" x2="15" y2="21"></line>
               </svg>
            </div>
          </div>
          <div className="rep-card-value">Zona Norte</div>
          <p className="rep-card-subtitle">8 paradas restantes hoy</p>
        </div>
      </div>
    </div>
  );
}
