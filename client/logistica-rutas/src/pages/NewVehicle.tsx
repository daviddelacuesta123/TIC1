import { useState, useEffect } from 'react'
import '../App.css'
import {
  crearVehiculo,
  listarModelos,
  type ModeloDTO,
  type TipoPropulsion,
  type TipoCombustible,
  type VehiculoCreatePayload,
} from '../services/vehiculoService'

type Page = 'dashboard' | 'shipments' | 'routes' | 'new-route' | 'vehicles' | 'new-vehicle'

interface NewVehicleProps {
  onNavigate: (page: Page) => void
}

interface BaseForm {
  idModelo: string
  anioFabricacion: string
  capacidadPeso: string
  capacidadVolumen: string
  costoPorKm: string
  tipoPropulsion: TipoPropulsion
}

interface TermicaForm {
  consumoKmLitro: string
  tipoCombustible: TipoCombustible
}

interface ElectricaForm {
  kwhPorKm: string
  autonomiaKm: string
  tiempoCargaHoras: string
}

interface HibridaForm extends TermicaForm, ElectricaForm {}

export default function NewVehicle({ onNavigate }: NewVehicleProps) {
  const [modelos, setModelos] = useState<ModeloDTO[]>([])
  const [loadingModelos, setLoadingModelos] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const [base, setBase] = useState<BaseForm>({
    idModelo: '',
    anioFabricacion: '',
    capacidadPeso: '',
    capacidadVolumen: '',
    costoPorKm: '',
    tipoPropulsion: 'TERMICA',
  })

  const [termica, setTermica] = useState<TermicaForm>({
    consumoKmLitro: '',
    tipoCombustible: 'GASOLINA',
  })

  const [electrica, setElectrica] = useState<ElectricaForm>({
    kwhPorKm: '',
    autonomiaKm: '',
    tiempoCargaHoras: '',
  })

  const [hibrida, setHibrida] = useState<HibridaForm>({
    consumoKmLitro: '',
    tipoCombustible: 'GASOLINA',
    kwhPorKm: '',
    autonomiaKm: '',
    tiempoCargaHoras: '',
  })

  useEffect(() => {
    listarModelos()
      .then(setModelos)
      .catch(() => setError('No se pudieron cargar los modelos de vehículo.'))
      .finally(() => setLoadingModelos(false))
  }, [])

  function handleBaseChange(e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) {
    const { name, value } = e.target
    setBase(prev => ({ ...prev, [name]: value }))
  }

  function handleTermicaChange(e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) {
    const { name, value } = e.target
    setTermica(prev => ({ ...prev, [name]: value }))
  }

  function handleElectricaChange(e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) {
    const { name, value } = e.target
    setElectrica(prev => ({ ...prev, [name]: value }))
  }

  function handleHibridaChange(e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) {
    const { name, value } = e.target
    setHibrida(prev => ({ ...prev, [name]: value }))
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setError(null)
    setSubmitting(true)

    try {
      let propulsion: VehiculoCreatePayload['propulsion']

      if (base.tipoPropulsion === 'TERMICA') {
        propulsion = {
          consumoKmLitro: parseFloat(termica.consumoKmLitro),
          tipoCombustible: termica.tipoCombustible,
        }
      } else if (base.tipoPropulsion === 'ELECTRICA') {
        propulsion = {
          kwhPorKm: parseFloat(electrica.kwhPorKm),
          autonomiaKm: parseFloat(electrica.autonomiaKm),
          tiempoCargaHoras: parseFloat(electrica.tiempoCargaHoras),
        }
      } else {
        propulsion = {
          consumoKmLitro: parseFloat(hibrida.consumoKmLitro),
          tipoCombustible: hibrida.tipoCombustible,
          kwhPorKm: parseFloat(hibrida.kwhPorKm),
          autonomiaKm: parseFloat(hibrida.autonomiaKm),
          tiempoCargaHoras: parseFloat(hibrida.tiempoCargaHoras),
        }
      }

      const payload: VehiculoCreatePayload = {
        idModelo: parseInt(base.idModelo),
        anioFabricacion: parseInt(base.anioFabricacion),
        capacidadPeso: parseFloat(base.capacidadPeso),
        capacidadVolumen: parseFloat(base.capacidadVolumen),
        costoPorKm: parseFloat(base.costoPorKm),
        tipoPropulsion: base.tipoPropulsion,
        propulsion,
      }

      await crearVehiculo(payload)
      onNavigate('vehicles')
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al crear el vehículo')
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <>
      <div className="page-header">
        <h1>Crear Vehículo</h1>
      </div>

      <div className="form-container">
        <form onSubmit={handleSubmit} className="create-form">

          {error && (
            <div className="error-banner">{error}</div>
          )}

          <div className="form-section-title">
            <h2>Datos Generales</h2>
          </div>

          <div className="form-row">
            <div className="form-col">
              <label htmlFor="idModelo">Modelo *</label>
              {loadingModelos ? (
                <select disabled><option>Cargando modelos…</option></select>
              ) : (
                <select
                  id="idModelo"
                  name="idModelo"
                  value={base.idModelo}
                  onChange={handleBaseChange}
                  required
                >
                  <option value="">Seleccionar modelo</option>
                  {modelos.map(m => (
                    <option key={m.id} value={m.id}>
                      {m.marca} — {m.nombre}
                    </option>
                  ))}
                </select>
              )}
            </div>

            <div className="form-col">
              <label htmlFor="anioFabricacion">Año de fabricación *</label>
              <input
                type="number"
                id="anioFabricacion"
                name="anioFabricacion"
                value={base.anioFabricacion}
                onChange={handleBaseChange}
                placeholder="2022"
                min={1990}
                max={2100}
                required
              />
            </div>
          </div>

          <div className="form-section-title">
            <h2>Capacidades</h2>
          </div>

          <div className="form-row">
            <div className="form-col">
              <label htmlFor="capacidadPeso">Capacidad de peso (kg) *</label>
              <input
                type="number"
                id="capacidadPeso"
                name="capacidadPeso"
                value={base.capacidadPeso}
                onChange={handleBaseChange}
                placeholder="500"
                min={0.01}
                step="0.01"
                required
              />
            </div>

            <div className="form-col">
              <label htmlFor="capacidadVolumen">Capacidad de volumen (m³) *</label>
              <input
                type="number"
                id="capacidadVolumen"
                name="capacidadVolumen"
                value={base.capacidadVolumen}
                onChange={handleBaseChange}
                placeholder="2.5"
                min={0.01}
                step="0.01"
                required
              />
            </div>

            <div className="form-col">
              <label htmlFor="costoPorKm">Costo por km (USD) *</label>
              <input
                type="number"
                id="costoPorKm"
                name="costoPorKm"
                value={base.costoPorKm}
                onChange={handleBaseChange}
                placeholder="1.50"
                min={0.0001}
                step="0.0001"
                required
              />
            </div>
          </div>

          <div className="form-section-title">
            <h2>Propulsión</h2>
          </div>

          <div className="form-row">
            <div className="form-col">
              <label htmlFor="tipoPropulsion">Tipo de propulsión *</label>
              <select
                id="tipoPropulsion"
                name="tipoPropulsion"
                value={base.tipoPropulsion}
                onChange={handleBaseChange}
              >
                <option value="TERMICA">Térmica</option>
                <option value="ELECTRICA">Eléctrica</option>
                <option value="HIBRIDA">Híbrida</option>
              </select>
            </div>
          </div>

          {/* Campos dinámicos según tipo de propulsión */}
          {base.tipoPropulsion === 'TERMICA' && (
            <div className="form-row">
              <div className="form-col">
                <label htmlFor="consumoKmLitro">Consumo (km/litro) *</label>
                <input
                  type="number"
                  id="consumoKmLitro"
                  name="consumoKmLitro"
                  value={termica.consumoKmLitro}
                  onChange={handleTermicaChange}
                  placeholder="12.5"
                  min={0.01}
                  step="0.01"
                  required
                />
              </div>
              <div className="form-col">
                <label htmlFor="tipoCombustible">Combustible *</label>
                <select
                  id="tipoCombustible"
                  name="tipoCombustible"
                  value={termica.tipoCombustible}
                  onChange={handleTermicaChange}
                >
                  <option value="GASOLINA">Gasolina</option>
                  <option value="DIESEL">Diésel</option>
                  <option value="GAS_NATURAL">Gas natural</option>
                </select>
              </div>
            </div>
          )}

          {base.tipoPropulsion === 'ELECTRICA' && (
            <div className="form-row">
              <div className="form-col">
                <label htmlFor="kwhPorKm">Consumo (kWh/km) *</label>
                <input
                  type="number"
                  id="kwhPorKm"
                  name="kwhPorKm"
                  value={electrica.kwhPorKm}
                  onChange={handleElectricaChange}
                  placeholder="0.18"
                  min={0.001}
                  step="0.001"
                  required
                />
              </div>
              <div className="form-col">
                <label htmlFor="autonomiaKm">Autonomía (km) *</label>
                <input
                  type="number"
                  id="autonomiaKm"
                  name="autonomiaKm"
                  value={electrica.autonomiaKm}
                  onChange={handleElectricaChange}
                  placeholder="300"
                  min={0.01}
                  step="0.01"
                  required
                />
              </div>
              <div className="form-col">
                <label htmlFor="tiempoCargaHoras">Tiempo de carga (h) *</label>
                <input
                  type="number"
                  id="tiempoCargaHoras"
                  name="tiempoCargaHoras"
                  value={electrica.tiempoCargaHoras}
                  onChange={handleElectricaChange}
                  placeholder="6"
                  min={0.01}
                  step="0.01"
                  required
                />
              </div>
            </div>
          )}

          {base.tipoPropulsion === 'HIBRIDA' && (
            <>
              <div className="form-row">
                <div className="form-col">
                  <label htmlFor="h-consumoKmLitro">Consumo térmico (km/litro) *</label>
                  <input
                    type="number"
                    id="h-consumoKmLitro"
                    name="consumoKmLitro"
                    value={hibrida.consumoKmLitro}
                    onChange={handleHibridaChange}
                    placeholder="18"
                    min={0.01}
                    step="0.01"
                    required
                  />
                </div>
                <div className="form-col">
                  <label htmlFor="h-tipoCombustible">Combustible *</label>
                  <select
                    id="h-tipoCombustible"
                    name="tipoCombustible"
                    value={hibrida.tipoCombustible}
                    onChange={handleHibridaChange}
                  >
                    <option value="GASOLINA">Gasolina</option>
                    <option value="DIESEL">Diésel</option>
                    <option value="GAS_NATURAL">Gas natural</option>
                  </select>
                </div>
              </div>
              <div className="form-row">
                <div className="form-col">
                  <label htmlFor="h-kwhPorKm">Consumo eléctrico (kWh/km) *</label>
                  <input
                    type="number"
                    id="h-kwhPorKm"
                    name="kwhPorKm"
                    value={hibrida.kwhPorKm}
                    onChange={handleHibridaChange}
                    placeholder="0.12"
                    min={0.001}
                    step="0.001"
                    required
                  />
                </div>
                <div className="form-col">
                  <label htmlFor="h-autonomiaKm">Autonomía eléctrica (km) *</label>
                  <input
                    type="number"
                    id="h-autonomiaKm"
                    name="autonomiaKm"
                    value={hibrida.autonomiaKm}
                    onChange={handleHibridaChange}
                    placeholder="60"
                    min={0.01}
                    step="0.01"
                    required
                  />
                </div>
                <div className="form-col">
                  <label htmlFor="h-tiempoCargaHoras">Tiempo de carga (h) *</label>
                  <input
                    type="number"
                    id="h-tiempoCargaHoras"
                    name="tiempoCargaHoras"
                    value={hibrida.tiempoCargaHoras}
                    onChange={handleHibridaChange}
                    placeholder="3"
                    min={0.01}
                    step="0.01"
                    required
                  />
                </div>
              </div>
            </>
          )}

          <div className="form-actions">
            <button type="submit" className="btn-primary" disabled={submitting}>
              {submitting ? 'Creando…' : 'Crear vehículo'}
            </button>
            <button
              type="button"
              className="btn-secondary"
              onClick={() => onNavigate('vehicles')}
              disabled={submitting}
            >
              Cancelar
            </button>
          </div>
        </form>
      </div>
    </>
  )
}
