import { apiFetch } from './authService'

export interface RepartidorResponseDTO {
  id: number
  idUsuario: number
  dni: string
  nombre: string
  apellido: string
  telefono: string
  correoElectronico: string
  estado: boolean
}

export interface RepartidorCreatePayload {
  idUsuario: number
  dni: string
  nombre: string
  apellido: string
  telefono: string
  correoElectronico: string
}

export interface RegistrarUsuarioResponse {
  id: number
  username: string
}

export interface RepartidorUpdatePayload {
  idUsuario?: number
  dni?: string
  nombre?: string
  apellido?: string
  telefono?: string
  correoElectronico?: string
}

export interface AsignacionVehiculoResponseDTO {
  idRepartidorVehiculo: number
  idRepartidor: number
  idVehiculo: number
  fechaAsignacion: string
}

export function listarRepartidores(): Promise<RepartidorResponseDTO[]> {
  return apiFetch<RepartidorResponseDTO[]>('/api/repartidores')
}

export function obtenerRepartidor(id: number): Promise<RepartidorResponseDTO> {
  return apiFetch<RepartidorResponseDTO>(`/api/repartidores/${id}`)
}

export function registrarUsuarioRepartidor(username: string, password: string): Promise<RegistrarUsuarioResponse> {
  return apiFetch<RegistrarUsuarioResponse>('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username, password, rol: 'REPARTIDOR' }),
  })
}

export function crearRepartidor(payload: RepartidorCreatePayload): Promise<RepartidorResponseDTO> {
  return apiFetch<RepartidorResponseDTO>('/api/repartidores', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function actualizarRepartidor(
  id: number,
  payload: RepartidorUpdatePayload,
): Promise<RepartidorResponseDTO> {
  return apiFetch<RepartidorResponseDTO>(`/api/repartidores/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function borrarRepartidor(id: number): Promise<null> {
  return apiFetch<null>(`/api/repartidores/${id}`, { method: 'DELETE' })
}

export function asignarVehiculo(
  idRepartidor: number,
  idVehiculo: number,
): Promise<AsignacionVehiculoResponseDTO> {
  return apiFetch<AsignacionVehiculoResponseDTO>(`/api/repartidores/${idRepartidor}/vehiculo`, {
    method: 'POST',
    body: JSON.stringify({ idVehiculo }),
  })
}

export async function obtenerVehiculo(id: number): Promise<AsignacionVehiculoResponseDTO | null> {
  const token = localStorage.getItem('auth_token')
  const res = await fetch(`/api/repartidores/${id}/vehiculo`, {
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
  })
  if (res.status === 404) return null
  if (res.status === 401) {
    localStorage.removeItem('auth_token')
    window.dispatchEvent(new CustomEvent('auth:logout'))
    throw new Error('Sesión expirada')
  }
  if (!res.ok) {
    const body = await res.json().catch(() => ({}))
    throw new Error((body as { error?: string }).error ?? `Error ${res.status}`)
  }
  return res.json() as Promise<AsignacionVehiculoResponseDTO>
}
