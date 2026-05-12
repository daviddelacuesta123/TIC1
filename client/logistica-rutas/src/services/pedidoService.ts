import { apiFetch } from './authService'

export interface PedidoResponse {
  id: number
  destinatario: {
    nombre: string
    apellido: string
    telefono: string
  }
  direccion: {
    direccionTexto: string
    ciudad: string
    latitud: number | null
    longitud: number | null
  }
  estado: 'PENDIENTE' | 'EN_CAMINO' | 'ENTREGADO' | 'FALLIDO' | 'CANCELADO'
  pesoTotal: number
  volumenTotal: number
  fechaCreacion: string
}

export interface NuevoPedidoPayload {
  destinatario: {
    nombre: string
    apellido: string
    dni: string
    telefono: string
    correoElectronico: string
  }
  direccion: {
    direccionTexto: string
    ciudad: string
    pais: string
  }
  productos: { idProducto: number; cantidad: number }[]
  pesoTotal: number
  volumenTotal: number
}

export interface PedidoUpdatePayload {
  estado?: string
  pesoTotal?: number
  volumenTotal?: number
}

export function listarPedidos(): Promise<PedidoResponse[]> {
  return apiFetch<PedidoResponse[]>('/api/pedidos')
}

export function obtenerPedido(id: number): Promise<PedidoResponse> {
  return apiFetch<PedidoResponse>(`/api/pedidos/${id}`)
}

export function listarPedidosPorRuta(idRuta: number | string): Promise<PedidoResponse[]> {
  return apiFetch<PedidoResponse[]>(`/api/pedidos/ruta/${idRuta}`)
}

export function geocodificarPedido(id: number): Promise<PedidoResponse> {
  return apiFetch<PedidoResponse>(`/api/pedidos/${id}/geocodificar`, { method: 'POST' })
}

export function crearPedido(payload: NuevoPedidoPayload): Promise<PedidoResponse> {
  return apiFetch<PedidoResponse>('/api/pedidos', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function actualizarPedido(id: number, payload: PedidoUpdatePayload): Promise<PedidoResponse> {
  return apiFetch<PedidoResponse>(`/api/pedidos/${id}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function eliminarPedido(id: number): Promise<null> {
  return apiFetch<null>(`/api/pedidos/${id}`, { method: 'DELETE' })
}
