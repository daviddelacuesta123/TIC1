import { apiFetch } from './authService'

export interface DashboardResponseDTO {
  pulso: {
    entregasCompletadas: number
    entregasFallidas: number
    tasaExito: number
    rutasActivas: number
    pedidosPendientes: number
  }
  costos: {
    costoTotalEstimado: number
    kmTotales: number
    kmPromedioRuta: number
    ahorroKm: number
  }
  flota: {
    vehiculosEnRuta: number
    vehiculosDisponibles: number
    porcentajeUtilizacion: number
  }
}

export function obtenerDashboard(): Promise<DashboardResponseDTO> {
  return apiFetch<DashboardResponseDTO>('/api/dashboard')
}
