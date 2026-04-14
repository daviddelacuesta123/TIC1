import { useState } from 'react'
import '../App.css'

interface ShipmentForm {
  trackingId: string
  origin: string
  destination: string
  status: string
  estimatedTime: string
}

type Page = 'dashboard' | 'shipments' | 'routes' | 'new-route' | 'vehicles' | 'new-vehicle' | 'new-shipment'

interface NewShipmentProps {
  onNavigate: (page: Page) => void
}

export default function NewShipment({ onNavigate }: NewShipmentProps) {
  const [formData, setFormData] = useState<ShipmentForm>({
    trackingId: '',
    origin: '',
    destination: '',
    status: 'Pending',
    estimatedTime: '',
  })

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }))
  }

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault()

    if (!formData.trackingId || !formData.origin || !formData.destination) {
      alert('Por favor completa los campos requeridos')
      return
    }

    console.log('Crear shipment:', formData)
    onNavigate('shipments')
  }

  const handleCancel = () => {
    onNavigate('shipments')
  }

  return (
    <>
      <div className="page-header">
        <h1>Create New Shipment</h1>
      </div>

      <div className="form-container">
        <form onSubmit={handleSubmit} className="create-form">
          <div className="form-section-title">
            <h2>Shipment Details</h2>
          </div>

          <div className="form-row">
            <div className="form-col">
              <label htmlFor="trackingId">Tracking ID *</label>
              <input
                type="text"
                id="trackingId"
                name="trackingId"
                value={formData.trackingId}
                onChange={handleInputChange}
                placeholder="SH-001"
                required
              />
            </div>

            <div className="form-col">
              <label htmlFor="origin">Origin *</label>
              <input
                type="text"
                id="origin"
                name="origin"
                value={formData.origin}
                onChange={handleInputChange}
                placeholder="Bogotá"
                required
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-col">
              <label htmlFor="destination">Destination *</label>
              <input
                type="text"
                id="destination"
                name="destination"
                value={formData.destination}
                onChange={handleInputChange}
                placeholder="Medellín"
                required
              />
            </div>

            <div className="form-col">
              <label htmlFor="estimatedTime">Estimated Time</label>
              <input
                type="text"
                id="estimatedTime"
                name="estimatedTime"
                value={formData.estimatedTime}
                onChange={handleInputChange}
                placeholder="12:30 PM"
              />
            </div>
          </div>

          <div className="form-row">
            <div className="form-col">
              <label htmlFor="status">Status</label>
              <select id="status" name="status" value={formData.status} onChange={handleInputChange}>
                <option>Pending</option>
                <option>In Transit</option>
                <option>Delivered</option>
              </select>
            </div>
          </div>

          <div className="form-actions">
            <button type="submit" className="btn-primary">Create Shipment</button>
            <button type="button" className="btn-secondary" onClick={handleCancel}>Cancel</button>
          </div>
        </form>
      </div>
    </>
  )
}
