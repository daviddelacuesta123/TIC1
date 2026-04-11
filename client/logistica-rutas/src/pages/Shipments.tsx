import '../App.css'

const MOCK_SHIPMENTS: any[] = []

export default function Shipments() {
  return (
    <>
      <div className="page-header">
        <h1>Active Shipments</h1>
        <button className="btn-primary">+ New Shipment</button>
      </div>
      
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Tracking ID</th>
              <th>Origin</th>
              <th>Destination</th>
              <th>Status</th>
              <th>Est. Date</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {MOCK_SHIPMENTS.map((shipment) => (
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
            ))}
          </tbody>
        </table>
      </div>
    </>
  )
}
