import '../App.css'

export default function RouteMap() {
  return (
    <>
      <h1>Route Overview</h1>
      <p className="subtitle">Interactive logistical map simulation</p>
      
      <div className="map-container">
        <div className="map-mockup">
          <div className="map-overlay">
            <div className="map-node node-active" style={{ top: '20%', left: '30%' }}>
              <span className="node-label">Madrid Hub</span>
            </div>
            <div className="map-node" style={{ top: '40%', left: '60%' }}>
              <span className="node-label">Paris Hub</span>
            </div>
            <div className="map-node node-delayed" style={{ top: '70%', left: '45%' }}>
              <span className="node-label">Barcelona Hub</span>
            </div>
            
            <svg className="map-lines" width="100%" height="100%">
              <line x1="30%" y1="20%" x2="60%" y2="40%" stroke="var(--accent-color)" strokeWidth="3" strokeDasharray="5,5" className="animated-line" />
              <line x1="30%" y1="20%" x2="45%" y2="70%" stroke="var(--danger-color)" strokeWidth="3" />
            </svg>
          </div>
        </div>
      </div>
    </>
  )
}
