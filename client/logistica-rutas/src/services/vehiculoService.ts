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

export function listarVehiculos(): Promise<VehiculoResponseDTO[]> {
  return apiFetch<VehiculoResponseDTO[]>('/api/vehiculos')
}

export async function listarVehiculosActivos(): Promise<VehiculoResponseDTO[]> {
  const todos = await apiFetch<VehiculoResponseDTO[]>('/api/vehiculos')
  return todos.filter(v => v.activo)
}

export function crearVehiculo(payload: VehiculoCreatePayload): Promise<VehiculoResponseDTO> {
  return apiFetch<VehiculoResponseDTO>('/api/vehiculos', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function actualizarVehiculo(
  id: number,
  payload: VehiculoUpdatePayload,
): Promise<VehiculoResponseDTO> {
  return apiFetch<VehiculoResponseDTO>(`/api/vehiculos/${id}`, {
    method: 'PATCH',
    body: JSON.stringify(payload),
  })
}

export function darDeBajaVehiculo(id: number): Promise<null> {
  return apiFetch<null>(`/api/vehiculos/${id}`, { method: 'DELETE' })
}

export function listarModelos(): Promise<ModeloDTO[]> {
  return apiFetch<ModeloDTO[]>('/api/vehiculos/modelos')
}

export function listarMarcas(): Promise<MarcaDTO[]> {
  return apiFetch<MarcaDTO[]>('/api/vehiculos/marcas')
}
