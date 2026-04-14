import '../App.css'

interface ShipmentsProps {
  onNavigate: (page: 'new-shipment') => void
}

export default function Shipments({ onNavigate }: ShipmentsProps) {
  const shipments: any[] = []

  return (
    <>
      <div className="page-header">
        <h1>Active Shipments</h1>
        <button className="btn-primary" onClick={() => onNavigate('new-shipment')}>
          + New Shipment
        </button>
      </div>
      
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Tracking ID</th>
              <th>Origin</th>
              <th>Destination</th>
              <th>Status</th>
              <th>Hora estimada</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {shipments.length === 0 ? (
              <tr>
                <td colSpan={6} className="empty-state">
                  No shipments created yet. Push "+ New Shipment" to add one.
                </td>
              </tr>
            ) : (
              shipments.map((shipment) => (
                <tr key={shipment.id}>
                  <td className="tracking-id">{shipment.id}</td>
                  <td>{shipment.origin}</td>
                  <td>{shipment.destination}</td>
                  <td>
                    <span className={`badge badge-${shipment.status.replace(' ', '-').toLowerCase()}`}>
                      {shipment.status}
                    </span>
                  </td>
                  <td>{shipment.date}</td>
                  <td><button className="btn-icon">View</button></td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </>
  )
}
