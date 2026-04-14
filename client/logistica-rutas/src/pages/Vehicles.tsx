import '../App.css'

/**
 * Interfaz que define la estructura de datos de un vehículo
 * Contiene información sobre placa, capacidades, costo y disponibilidad
 */
interface Vehicle {
  id: string
  placa: string
  modelo: string
  marca: string
  capacidadKg: number
  capacidadM3: number
  costoPorKm: number
  propulsion: string
  estado: 'ocupado' | 'libre'
}

interface VehiclesProps {
  onNavigate: (page: 'new-vehicle') => void
}

/**
 * Página Vehicles que muestra la tabla vacía y un botón para crear un vehículo
 */
export default function Vehicles({ onNavigate }: VehiclesProps) {
  const vehicles: Vehicle[] = []

  return (
    <>
      <div className="page-header">
        <h1>Vehicles</h1>
        <button className="btn-primary" onClick={() => onNavigate('new-vehicle')}>
          + New Vehicle
        </button>
      </div>
      
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Placa</th>
              <th>Marca</th>
              <th>Modelo</th>
              <th>Capacidad (kg)</th>
              <th>Capacidad (m³)</th>
              <th>Costo/Km</th>
              <th>Propulsión</th>
              <th>Estado</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            {vehicles.length === 0 ? (
              <tr>
                <td colSpan={9} className="empty-state">
                  No vehicles created yet. Push "+ New Vehicle" to add one.
                </td>
              </tr>
            ) : (
              vehicles.map((vehicle) => (
                <tr key={vehicle.id}>
                  <td className="tracking-id">{vehicle.placa}</td>
                  <td>{vehicle.marca}</td>
                  <td>{vehicle.modelo}</td>
                  <td>{vehicle.capacidadKg}</td>
                  <td>{vehicle.capacidadM3}</td>
                  <td>${vehicle.costoPorKm.toFixed(2)}</td>
                  <td>
                    <span className={`badge badge-${vehicle.propulsion.toLowerCase()}`}>
                      {vehicle.propulsion}
                    </span>
                  </td>
                  <td>
                    <span className={`badge badge-${vehicle.estado}`}>
                      {vehicle.estado.charAt(0).toUpperCase() + vehicle.estado.slice(1)}
                    </span>
                  </td>
                  <td><button className="btn-icon">Edit</button></td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </>
  )
}
