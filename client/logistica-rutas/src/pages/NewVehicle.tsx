import { useState } from 'react'
import '../App.css'

/**
 * Interfaz que define la estructura de datos de un vehículo
 * Contiene información sobre placa, capacidades, costo y disponibilidad
 */
interface VehicleForm {
  placa: string
  modelo: string
  marca: string
  capacidadKg: string
  capacidadM3: string
  costoPorKm: string
  propulsion: string
  estado: 'ocupado' | 'libre'
}

type Page = 'dashboard' | 'shipments' | 'routes' | 'new-route' | 'vehicles' | 'new-vehicle'

interface NewVehicleProps {
  onNavigate: (page: Page) => void
}

/**
 * Componente para crear un nuevo vehículo
 * Proporciona un formulario para ingresar los datos del vehículo
 */
export default function NewVehicle({ onNavigate }: NewVehicleProps) {
  // Estado para los datos del formulario
  const [formData, setFormData] = useState<VehicleForm>({
    placa: '',
    modelo: '',
    marca: '',
    capacidadKg: '',
    capacidadM3: '',
    costoPorKm: '',
    propulsion: 'Térmica',
    estado: 'libre',
  })

  /**
   * Maneja los cambios en los campos del formulario
   */
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }))
  }

  /**
   * Maneja el envío del formulario
   */
  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()
    
    // Validar que los campos requeridos estén llenos
    if (!formData.placa || !formData.marca || !formData.modelo) {
      alert('Por favor completa los campos requeridos')
      return
    }
    
    // Aquí se enviaría la data al backend
    console.log('Crear vehículo:', formData)
    
    // Volver a la página de vehículos
    onNavigate('vehicles')
  }

  /**
   * Cancela la creación y vuelve a la página de vehículos
   */
  const handleCancel = () => {
    onNavigate('vehicles')
  }

  return (
    <>
      <div className="page-header">
        <h1>Create New Vehicle</h1>
      </div>

      <div className="form-container">
        <form onSubmit={handleSubmit} className="create-form">
          <div className="form-section-title">
            <h2>Vehicle Information</h2>
          </div>

          {/* Row 1: Placa y Marca */}
          <div className="form-row">
            <div className="form-col">
              <label htmlFor="placa">Placa *</label>
              <input
                type="text"
                id="placa"
                name="placa"
                value={formData.placa}
                onChange={handleInputChange}
                placeholder="ABC-123"
                required
              />
            </div>

            <div className="form-col">
              <label htmlFor="marca">Marca *</label>
              <input
                type="text"
                id="marca"
                name="marca"
                value={formData.marca}
                onChange={handleInputChange}
                placeholder="Toyota"
                required
              />
            </div>
          </div>

          {/* Row 2: Modelo */}
          <div className="form-row">
            <div className="form-col">
              <label htmlFor="modelo">Modelo *</label>
              <input
                type="text"
                id="modelo"
                name="modelo"
                value={formData.modelo}
                onChange={handleInputChange}
                placeholder="Yaris"
                required
              />
            </div>
          </div>

          <div className="form-section-title">
            <h2>Capacity Information</h2>
          </div>

          {/* Row 3: Capacidad Kg y M3 */}
          <div className="form-row">
            <div className="form-col">
              <label htmlFor="capacidadKg">Capacidad (kg)</label>
              <input
                type="number"
                id="capacidadKg"
                name="capacidadKg"
                value={formData.capacidadKg}
                onChange={handleInputChange}
                placeholder="500"
              />
            </div>

            <div className="form-col">
              <label htmlFor="capacidadM3">Capacidad (m³)</label>
              <input
                type="number"
                id="capacidadM3"
                name="capacidadM3"
                value={formData.capacidadM3}
                onChange={handleInputChange}
                placeholder="2.5"
                step="0.1"
              />
            </div>
          </div>

          <div className="form-section-title">
            <h2>Additional Information</h2>
          </div>

          {/* Row 4: Costo por Km */}
          <div className="form-row">
            <div className="form-col">
              <label htmlFor="costoPorKm">Costo por Km</label>
              <input
                type="number"
                id="costoPorKm"
                name="costoPorKm"
                value={formData.costoPorKm}
                onChange={handleInputChange}
                placeholder="1.50"
                step="0.01"
              />
            </div>
          </div>

          {/* Row 5: Propulsión y Estado */}
          <div className="form-row">
            <div className="form-col">
              <label htmlFor="propulsion">Tipo de Propulsión</label>
              <select
                id="propulsion"
                name="propulsion"
                value={formData.propulsion}
                onChange={handleInputChange}
              >
                <option>Térmica</option>
                <option>Eléctrica</option>
                <option>Híbrida</option>
              </select>
            </div>

            <div className="form-col">
              <label htmlFor="estado">Estado</label>
              <select
                id="estado"
                name="estado"
                value={formData.estado}
                onChange={handleInputChange}
              >
                <option value="libre">Libre</option>
                <option value="ocupado">Ocupado</option>
              </select>
            </div>
          </div>

          {/* Actions */}
          <div className="form-actions">
            <button type="submit" className="btn-primary">Create Vehicle</button>
            <button type="button" className="btn-secondary" onClick={handleCancel}>Cancel</button>
          </div>
        </form>
      </div>
    </>
  )
}
