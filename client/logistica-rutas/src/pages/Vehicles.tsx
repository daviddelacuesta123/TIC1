import { useState } from 'react'
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

/**
 * Componente principal para la gestión CRUD de vehículos
 * Permite crear, leer, actualizar y eliminar vehículos
 */
export default function Vehicles() {
  // Estado que almacena la lista de vehículos
  const [vehicles] = useState<Vehicle[]>([])


  return (
    <>
      <div className="page-header">
        <h1>Vehicles</h1>
        <button className="btn-primary">+ New Vehicle</button>
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
            {vehicles.map((vehicle) => (
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
            ))}
          </tbody>
        </table>
      </div>
    </>
  )
}
