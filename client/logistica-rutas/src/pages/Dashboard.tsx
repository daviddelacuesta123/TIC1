import '../App.css'

export default function Dashboard() {
  return (
    <>
      <h1>Dashboard Overview</h1>
      <p className="subtitle">Real-time metrics for current logistics operations</p>
      
      <div className="dashboard-grid">
        <div className="stat-card">
          <h3 className="stat-title">Total Shipments</h3>
          <p className="stat-number">0</p>
          <div className="stat-trend neutral">Sin cambios</div>
        </div>
        <div className="stat-card">
          <h3 className="stat-title">Active Routes</h3>
          <p className="stat-number active-stat">0</p>
          <div className="stat-trend neutral">Sin cambios</div>
        </div>
        <div className="stat-card">
          <h3 className="stat-title">Delayed</h3>
          <p className="stat-number delayed-stat">0</p>
          <div className="stat-trend neutral">Sin cambios</div>
        </div>
      </div>

      <div className="dashboard-content-split">
        <div className="recent-activity">
          <h2>Recent Activity</h2>
          <ul className="activity-list">
            <li>No hay actividad reciente.</li>
          </ul>
        </div>
      </div>
    </>
  )
}
