import { useState, useEffect } from 'react';
import './Dashboard.css';
import { obtenerDashboard, type DashboardResponseDTO } from '../services/dashboardService';

const EMPTY: DashboardResponseDTO = {
  pulso: { entregasCompletadas: 0, entregasFallidas: 0, tasaExito: 0, rutasActivas: 0, pedidosPendientes: 0 },
  costos: { costoTotalEstimado: 0, kmTotales: 0, kmPromedioRuta: 0, ahorroKm: 0 },
  flota: { vehiculosEnRuta: 0, vehiculosDisponibles: 0, porcentajeUtilizacion: 0 },
};

export default function Dashboard() {
  const [currentDate, setCurrentDate] = useState(new Date());
  const [data, setData] = useState<DashboardResponseDTO>(EMPTY);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const timer = setInterval(() => setCurrentDate(new Date()), 60000);
    return () => clearInterval(timer);
  }, []);

  useEffect(() => {
    obtenerDashboard()
      .then(setData)
      .catch(err => setError(err instanceof Error ? err.message : 'Error al cargar el dashboard'));
  }, []);

  const { pulso, costos, flota } = data;
  const totalVehiculos = flota.vehiculosEnRuta + flota.vehiculosDisponibles;
  const kmQuotaPct = costos.kmTotales > 0 ? Math.min(100, (costos.kmTotales / 600) * 100) : 0;

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <div>
          <h1>Logistics Dashboard</h1>
          <p>Real-time metrics for current operations</p>
        </div>
        <div className="dashboard-date">
          <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
            <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
            <line x1="16" y1="2" x2="16" y2="6"></line>
            <line x1="8" y1="2" x2="8" y2="6"></line>
            <line x1="3" y1="10" x2="21" y2="10"></line>
          </svg>
          {currentDate.toLocaleDateString('es-ES', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}
        </div>
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

      <h2 className="section-title">
        <svg className="section-icon" width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
          <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
        </svg>
        Pulso Operacional
      </h2>
      <div className="pulse-grid">
        <div className="dash-card card-success">
          <div className="card-header">
            <span className="card-title">Tasa de Éxito</span>
            <div className="card-icon icon-success">
              <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
              </svg>
            </div>
          </div>
          <div className="card-value">{pulso.tasaExito.toFixed(1)}<span className="card-value-small">%</span></div>
          <div className="card-trend trend-up">
            {pulso.entregasCompletadas} entregas completadas
          </div>
        </div>

        <div className="dash-card card-info">
          <div className="card-header">
            <span className="card-title">Rutas Activas</span>
            <div className="card-icon icon-info">
              <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <polygon points="3 6 9 3 15 6 21 3 21 18 15 21 9 18 3 21"></polygon>
                <line x1="9" y1="3" x2="9" y2="18"></line>
                <line x1="15" y1="6" x2="15" y2="21"></line>
              </svg>
            </div>
          </div>
          <div className="card-value">{pulso.rutasActivas}</div>
          <div className="card-trend trend-up">
            Rutas en curso hoy
          </div>
        </div>

        <div className="dash-card card-purple">
          <div className="card-header">
            <span className="card-title">Entregas</span>
            <div className="card-icon icon-info" style={{ background: 'rgba(139, 92, 246, 0.1)', color: '#8b5cf6' }}>
              <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect>
                <line x1="8" y1="21" x2="16" y2="21"></line>
                <line x1="12" y1="17" x2="12" y2="21"></line>
              </svg>
            </div>
          </div>
          <div className="card-value">
            {pulso.entregasCompletadas}{' '}
            <span className="card-value-small">/ {pulso.entregasCompletadas + pulso.entregasFallidas}</span>
          </div>
          <div className="card-trend trend-down">
            <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="8" x2="12" y2="12"></line>
              <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
            {pulso.entregasFallidas} fallidas hoy
          </div>
        </div>

        <div className="dash-card card-warning">
          <div className="card-header">
            <span className="card-title">Pedidos Pendientes</span>
            <div className="card-icon icon-warning">
              <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <circle cx="12" cy="12" r="10"></circle>
                <polyline points="12 6 12 12 16 14"></polyline>
              </svg>
            </div>
          </div>
          <div className="card-value">{pulso.pedidosPendientes}</div>
          <div className="card-trend trend-down">
            Sin asignar a ruta
          </div>
        </div>
      </div>

      <div className="costs-fleet-grid">
        <div>
          <h2 className="section-title">
            <svg className="section-icon" width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <line x1="12" y1="1" x2="12" y2="23"></line>
              <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
            </svg>
            Costos y Eficiencia
          </h2>
          <div className="dash-card card-info" style={{ height: 'calc(100% - 4.5rem)' }}>
            <div className="metrics-row">
              <div className="metric-col">
                <span className="metric-label">Costo Total Estimado</span>
                <span className="metric-val" style={{ fontSize: '1.75rem' }}>
                  ${costos.costoTotalEstimado.toFixed(2)}
                </span>
              </div>
              <div className="metric-col" style={{ textAlign: 'right' }}>
                <span className="metric-label">Km Recorridos</span>
                <span className="metric-val" style={{ fontSize: '1.75rem', color: '#38bdf8' }}>
                  {costos.kmTotales.toFixed(1)} km
                </span>
              </div>
            </div>

            <div className="progress-container">
              <div className="progress-header">
                <span>Distancia Recorrida</span>
                <span>{kmQuotaPct.toFixed(0)}% del cupo diario</span>
              </div>
              <div className="progress-track">
                <div className="progress-fill fill-info" style={{ width: `${kmQuotaPct}%` }}></div>
              </div>
            </div>

            <div className="savings-banner">
              <div className="savings-info">
                <h4>Optimización de Rutas</h4>
                <p>Km ahorrados vs. planificación manual</p>
              </div>
              <div className="savings-value">
                -{costos.ahorroKm.toFixed(1)} km
                <div style={{ fontSize: '0.875rem', fontWeight: 500, textAlign: 'right', marginTop: '0.25rem' }}>
                  Prom. {costos.kmPromedioRuta.toFixed(1)} km/ruta
                </div>
              </div>
            </div>
          </div>
        </div>

        <div>
          <h2 className="section-title">
            <svg className="section-icon" width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <rect x="1" y="3" width="15" height="13"></rect>
              <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
              <circle cx="5.5" cy="18.5" r="2.5"></circle>
              <circle cx="18.5" cy="18.5" r="2.5"></circle>
            </svg>
            Estado de Flota
          </h2>
          <div className="dash-card card-purple" style={{ height: 'calc(100% - 4.5rem)' }}>
            <div className="progress-container" style={{ marginTop: 0, marginBottom: '1.5rem' }}>
              <div className="progress-header">
                <span>Utilización de Flota</span>
                <span>{flota.porcentajeUtilizacion.toFixed(0)}% Activa</span>
              </div>
              <div className="progress-track">
                <div className="progress-fill fill-success" style={{ width: `${flota.porcentajeUtilizacion}%` }}></div>
              </div>
            </div>

            <div className="fleet-list">
              <div className="fleet-item">
                <div className="fleet-driver">
                  <div className="driver-avatar">
                    <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <rect x="1" y="3" width="15" height="13"></rect>
                      <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
                      <circle cx="5.5" cy="18.5" r="2.5"></circle>
                      <circle cx="18.5" cy="18.5" r="2.5"></circle>
                    </svg>
                  </div>
                  <div className="driver-info">
                    <h4>Vehículos en Ruta</h4>
                    <p>{flota.vehiculosEnRuta} de {totalVehiculos} unidades</p>
                  </div>
                </div>
                <div className="status-badge status-active">{flota.vehiculosEnRuta}</div>
              </div>

              <div className="fleet-item">
                <div className="fleet-driver">
                  <div className="driver-avatar" style={{ background: 'linear-gradient(135deg, #10b981, #34d399)' }}>
                    <svg width="18" height="18" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                      <polyline points="22 4 12 14.01 9 11.01"></polyline>
                    </svg>
                  </div>
                  <div className="driver-info">
                    <h4>Vehículos Disponibles</h4>
                    <p>Listos para asignar</p>
                  </div>
                </div>
                <div className="status-badge status-idle">{flota.vehiculosDisponibles}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
