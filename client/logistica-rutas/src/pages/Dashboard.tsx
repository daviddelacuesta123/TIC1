import { useState, useEffect } from 'react';
import './Dashboard.css';

export default function Dashboard() {
  const [currentDate, setCurrentDate] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setCurrentDate(new Date()), 60000);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="dashboard-container">
      {/* Header */}
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

      {/* 1. Operational Pulse */}
      <h2 className="section-title">
        <svg className="section-icon" width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
          <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
        </svg>
        Operational Pulse
      </h2>
      <div className="pulse-grid">
        <div className="dash-card card-success">
          <div className="card-header">
            <span className="card-title">Success Rate</span>
            <div className="card-icon icon-success">
              <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
              </svg>
            </div>
          </div>
          <div className="card-value">97.8<span className="card-value-small">%</span></div>
          <div className="card-trend trend-up">
            <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <line x1="12" y1="19" x2="12" y2="5"></line>
              <polyline points="5 12 12 5 19 12"></polyline>
            </svg>
            +1.2% from yesterday
          </div>
        </div>

        <div className="dash-card card-info">
          <div className="card-header">
            <span className="card-title">Active Routes</span>
            <div className="card-icon icon-info">
              <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <polygon points="3 6 9 3 15 6 21 3 21 18 15 21 9 18 3 21"></polygon>
                <line x1="9" y1="3" x2="9" y2="18"></line>
                <line x1="15" y1="6" x2="15" y2="21"></line>
              </svg>
            </div>
          </div>
          <div className="card-value">12</div>
          <div className="card-trend trend-up">
            4 routes in transit, 8 near destination
          </div>
        </div>

        <div className="dash-card card-purple">
          <div className="card-header">
            <span className="card-title">Deliveries</span>
            <div className="card-icon icon-info" style={{background: 'rgba(139, 92, 246, 0.1)', color: '#8b5cf6'}}>
              <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect>
                <line x1="8" y1="21" x2="16" y2="21"></line>
                <line x1="12" y1="17" x2="12" y2="21"></line>
              </svg>
            </div>
          </div>
          <div className="card-value">142 <span className="card-value-small">/ 145</span></div>
          <div className="card-trend trend-down">
            <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="8" x2="12" y2="12"></line>
              <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
            3 failed deliveries today
          </div>
        </div>

        <div className="dash-card card-warning">
          <div className="card-header">
            <span className="card-title">Pending Orders</span>
            <div className="card-icon icon-warning">
              <svg width="20" height="20" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                <circle cx="12" cy="12" r="10"></circle>
                <polyline points="12 6 12 12 16 14"></polyline>
              </svg>
            </div>
          </div>
          <div className="card-value">28</div>
          <div className="card-trend trend-down">
            Needs assignment for afternoon shift
          </div>
        </div>
      </div>

      <div className="costs-fleet-grid">
        {/* 2. Costs and Efficiency */}
        <div>
          <h2 className="section-title">
            <svg className="section-icon" width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <line x1="12" y1="1" x2="12" y2="23"></line>
              <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
            </svg>
            Costs & Efficiency
          </h2>
          <div className="dash-card card-info" style={{ height: 'calc(100% - 4.5rem)' }}>
            <div className="metrics-row">
              <div className="metric-col">
                <span className="metric-label">Total Daily Costs</span>
                <span className="metric-val" style={{fontSize: '1.75rem'}}>$1,250.00</span>
              </div>
              <div className="metric-col" style={{textAlign: 'right'}}>
                <span className="metric-label">Distance Covered</span>
                <span className="metric-val" style={{fontSize: '1.75rem', color: '#38bdf8'}}>450 km</span>
              </div>
            </div>
            
            <div className="progress-container">
              <div className="progress-header">
                <span>Distance Traveled</span>
                <span>75% of Daily Quota</span>
              </div>
              <div className="progress-track">
                <div className="progress-fill fill-info" style={{ width: '75%' }}></div>
              </div>
            </div>

            <div className="savings-banner">
              <div className="savings-info">
                <h4>Smart Route Optimization</h4>
                <p>Algorithm vs Manual Planning Savings</p>
              </div>
              <div className="savings-value">
                -$187.50
                <div style={{fontSize: '0.875rem', fontWeight: 500, textAlign: 'right', marginTop: '0.25rem'}}>
                  15% Saved
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* 3. Fleet Status */}
        <div>
          <h2 className="section-title">
            <svg className="section-icon" width="24" height="24" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
              <rect x="1" y="3" width="15" height="13"></rect>
              <polygon points="16 8 20 8 23 11 23 16 16 16 16 8"></polygon>
              <circle cx="5.5" cy="18.5" r="2.5"></circle>
              <circle cx="18.5" cy="18.5" r="2.5"></circle>
            </svg>
            Fleet Status
          </h2>
          <div className="dash-card card-purple" style={{ height: 'calc(100% - 4.5rem)' }}>
            <div className="progress-container" style={{marginTop: 0, marginBottom: '1.5rem'}}>
              <div className="progress-header">
                <span>Fleet Utilization</span>
                <span>85% Active</span>
              </div>
              <div className="progress-track">
                <div className="progress-fill fill-success" style={{ width: '85%' }}></div>
              </div>
            </div>

            <div className="fleet-list">
              <div className="fleet-item">
                <div className="fleet-driver">
                  <div className="driver-avatar">CM</div>
                  <div className="driver-info">
                    <h4>Carlos Mendoza</h4>
                    <p>Route: Norte · Van 01</p>
                  </div>
                </div>
                <div className="status-badge status-active">Active</div>
              </div>

              <div className="fleet-item">
                <div className="fleet-driver">
                  <div className="driver-avatar" style={{background: 'linear-gradient(135deg, #10b981, #34d399)'}}>MG</div>
                  <div className="driver-info">
                    <h4>María González</h4>
                    <p>Route: Sur · Truck 03</p>
                  </div>
                </div>
                <div className="status-badge status-active">Active</div>
              </div>

              <div className="fleet-item">
                <div className="fleet-driver">
                  <div className="driver-avatar" style={{background: 'linear-gradient(135deg, #f59e0b, #fbbf24)'}}>JR</div>
                  <div className="driver-info">
                    <h4>Juan Ríos</h4>
                    <p>Available for assignment</p>
                  </div>
                </div>
                <div className="status-badge status-idle">Idle</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
