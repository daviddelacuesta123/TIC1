import { apiFetch } from './authService'

export type TipoPropulsion = 'TERMICA' | 'ELECTRICA' | 'HIBRIDA'
export type TipoCombustible = 'GASOLINA' | 'DIESEL' | 'GAS_NATURAL'

export interface MarcaDTO {
  id: number
  nombre: string
}

export interface ModeloDTO {
  id: number
  nombre: string
  marca: string
  idMarca: number
  idTipoVehiculo: number
}

export interface VehiculoResponseDTO {
  id: number
  modelo: string
  marca: string
  idModelo: number
  anioFabricacion: number
  capacidadPeso: number
  capacidadVolumen: number
  costoPorKm: number
  tipoPropulsion: TipoPropulsion
  propulsion: Record<string, unknown>
  activo: boolean
}

interface PropulsionTermicaPayload {
  consumoKmLitro: number
  tipoCombustible: TipoCombustible
}

interface PropulsionElectricaPayload {
  kwhPorKm: number
  autonomiaKm: number
  tiempoCargaHoras: number
}

interface PropulsionHibridaPayload {
  consumoKmLitro: number
  tipoCombustible: TipoCombustible
  kwhPorKm: number
  autonomiaKm: number
  tiempoCargaHoras: number
}

export interface VehiculoCreatePayload {
  idModelo: number
  anioFabricacion: number
  capacidadPeso: number
  capacidadVolumen: number
  costoPorKm: number
  tipoPropulsion: TipoPropulsion
  propulsion: PropulsionTermicaPayload | PropulsionElectricaPayload | PropulsionHibridaPayload
}

export interface VehiculoUpdatePayload {
  capacidadPeso?: number
  capacidadVolumen?: number
  costoPorKm?: number
}

async function handleResponse<T>(res: Response): Promise<T> {
  if (!res.ok) {
    const body = await res.json().catch(() => ({}))
    throw new Error(body.message ?? `Error ${res.status}`)
  }
  return res.json()
}

export async function listarVehiculos(): Promise<VehiculoResponseDTO[]> {
  const res = await apiFetch('/api/vehiculos')
  return handleResponse<VehiculoResponseDTO[]>(res)
}

export async function crearVehiculo(payload: VehiculoCreatePayload): Promise<VehiculoResponseDTO> {
  const res = await apiFetch('/api/vehiculos', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
  return handleResponse<VehiculoResponseDTO>(res)
}

export async function actualizarVehiculo(
  id: number,
  payload: VehiculoUpdatePayload,
): Promise<VehiculoResponseDTO> {
  const res = await apiFetch(`/api/vehiculos/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(payload),
  })
  return handleResponse<VehiculoResponseDTO>(res)
}

export async function darDeBajaVehiculo(id: number): Promise<void> {
  const res = await apiFetch(`/api/vehiculos/${id}`, { method: 'DELETE' })
  if (!res.ok) {
    const body = await res.json().catch(() => ({}))
    throw new Error(body.message ?? `Error ${res.status}`)
  }
}

export async function listarModelos(): Promise<ModeloDTO[]> {
  const res = await apiFetch('/api/vehiculos/modelos')
  return handleResponse<ModeloDTO[]>(res)
}

export async function listarMarcas(): Promise<MarcaDTO[]> {
  const res = await apiFetch('/api/vehiculos/marcas')
  return handleResponse<MarcaDTO[]>(res)
}
