import { apiFetch } from './authService'

export function geocodificar(
  direccion: string,
  ciudad: string,
): Promise<{ latitud: number; longitud: number }> {
  return apiFetch<{ latitud: number; longitud: number }>('/api/geo/geocodificar', {
    method: 'POST',
    body: JSON.stringify({ direccion, ciudad }),
  })
}

export function geocodificarLote(
  direcciones: { id: string; direccion: string; ciudad: string }[],
): Promise<Map<string, { latitud: number; longitud: number }>> {
  const body: Record<string, string> = {}
  for (const d of direcciones) {
    body[d.id] = `${d.direccion}, ${d.ciudad}`
  }
  return apiFetch<Record<string, { latitud: number; longitud: number }>>(
    '/api/geo/geocodificar/lote',
    { method: 'POST', body: JSON.stringify(body) },
  ).then(res => new Map(Object.entries(res)))
}
