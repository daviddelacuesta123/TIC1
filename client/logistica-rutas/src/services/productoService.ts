import { apiFetch } from './authService'

export interface ProductoResponse {
  id: number
  nombre: string
  peso: number
  volumen: number
}

export interface ProductoCreatePayload {
  nombre: string
  peso: number
  volumen: number
}

export function listarProductos(): Promise<ProductoResponse[]> {
  return apiFetch<ProductoResponse[]>('/api/productos')
}

export function crearProducto(payload: ProductoCreatePayload): Promise<ProductoResponse> {
  return apiFetch<ProductoResponse>('/api/productos', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function eliminarProducto(id: number): Promise<null> {
  return apiFetch<null>(`/api/productos/${id}`, { method: 'DELETE' })
}
