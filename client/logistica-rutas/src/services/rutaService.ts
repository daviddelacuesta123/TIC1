import { apiFetch } from './authService'

export interface PuntoRutaResponse {
  orden: number
  id: string
  etiqueta: string
  latitud: number
  longitud: number
  etaAcumuladoMinutos: number
  distanciaAcumuladaKm: number
}

export interface RutaPorRepartidorResponse {
  repartidorId: string
  repartidorNombre: string
  numeroParadas: number
  distanciaTotal: number
  tiempoEstimadoMinutos: number
  costoEstimado: number
  clasificacionCosto: 'EFICIENTE' | 'ACEPTABLE' | 'ALTO'
  cargaUtilizadaPct: number
  mejoraPorcentaje: number
  fuenteDistancias: 'OSRM_VIAL' | 'HAVERSINE_FALLBACK'
  puntos: PuntoRutaResponse[]
  geometria?: [number, number][] // [longitude, latitude] pairs from OSRM GeoJSON
}

export interface SesionDespachoResponse {
  id: string
  estado: string
  totalPedidos: number
  totalRepartidores: number
  kmTotales: number
  costoTotalEstimado: number
  mejoraPorcentajePromedio: number
  fechaCreacion: string
  rutas: RutaPorRepartidorResponse[]
}

export interface SesionDespachoRequest {
  deposito: { latitud: number; longitud: number }
  pedidos: Array<{
    id: string
    etiqueta: string
    latitud: number
    longitud: number
    direccion?: string
    ciudad?: string
  }>
  repartidores: Array<{
    id: string
    nombre: string
    capacidadPesoKg: number
    capacidadVolumenM3: number
  }>
  estrategia: 'GEOGRAFICA_BALANCEADA'
}

export function crearSesionDespacho(request: SesionDespachoRequest): Promise<SesionDespachoResponse> {
  return apiFetch<SesionDespachoResponse>('/api/sesiones-despacho', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function listarSesiones(estado?: string): Promise<SesionDespachoResponse[]> {
  const qs = estado ? `?estado=${estado}` : ''
  return apiFetch<SesionDespachoResponse[]>(`/api/sesiones-despacho${qs}`)
}

export function despacharSesion(idSesion: string): Promise<SesionDespachoResponse> {
  return apiFetch<SesionDespachoResponse>(`/api/sesiones-despacho/${idSesion}/despachar`, { method: 'POST' })
}

export function cancelarSesion(idSesion: string): Promise<null> {
  return apiFetch<null>(`/api/sesiones-despacho/${idSesion}/cancelar`, { method: 'POST' })
}

export function obtenerRuta(idRuta: number): Promise<RutaPorRepartidorResponse> {
  return apiFetch<RutaPorRepartidorResponse>(`/api/rutas/${idRuta}`)
}

export function listarRutasRepartidor(): Promise<RutaPorRepartidorResponse[]> {
  return apiFetch<RutaPorRepartidorResponse[]>('/api/rutas')
}
