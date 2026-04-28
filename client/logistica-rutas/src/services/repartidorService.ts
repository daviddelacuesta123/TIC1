export interface RepartidorResponseDTO {
  id: number
  idUsuario: number
  dni: string
  nombre: string
  apellido: string
  telefono: string
  correoElectronico: string
  estado: string
}

export interface RepartidorCreatePayload {
  idUsuario: number
  dni: string
  nombre: string
  apellido: string
  telefono: string
  correoElectronico: string
  estado: string
}

export interface RepartidorUpdatePayload {
  idUsuario?: number
  dni?: string
  nombre?: string
  apellido?: string
  telefono?: string
  correoElectronico?: string
  estado?: string
}

const STORAGE_KEY = 'repartidores_logistica'

function getPersistedRepartidores(): RepartidorResponseDTO[] {
  const raw = localStorage.getItem(STORAGE_KEY)
  if (!raw) {
    return []
  }
  try {
    return JSON.parse(raw) as RepartidorResponseDTO[]
  } catch {
    localStorage.removeItem(STORAGE_KEY)
    return []
  }
}

function persistRepartidores(value: RepartidorResponseDTO[]) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(value))
}

function nextId(current: RepartidorResponseDTO[]) {
  return current.reduce((maxId, repartidor) => Math.max(maxId, repartidor.id), 0) + 1
}

export async function listarRepartidores(): Promise<RepartidorResponseDTO[]> {
  return Promise.resolve(getPersistedRepartidores())
}

export async function crearRepartidor(
  payload: RepartidorCreatePayload,
): Promise<RepartidorResponseDTO> {
  const current = getPersistedRepartidores()
  const nuevo: RepartidorResponseDTO = {
    id: nextId(current),
    ...payload,
  }
  persistRepartidores([...current, nuevo])
  return Promise.resolve(nuevo)
}

export async function actualizarRepartidor(
  id: number,
  payload: RepartidorUpdatePayload,
): Promise<RepartidorResponseDTO> {
  const current = getPersistedRepartidores()
  const index = current.findIndex(r => r.id === id)
  if (index === -1) {
    return Promise.reject(new Error('Repartidor no encontrado'))
  }
  const actualizado = { ...current[index], ...payload }
  current[index] = actualizado
  persistRepartidores(current)
  return Promise.resolve(actualizado)
}

export async function borrarRepartidor(id: number): Promise<void> {
  const current = getPersistedRepartidores()
  const actualizado = current.filter(r => r.id !== id)
  persistRepartidores(actualizado)
  return Promise.resolve()
}
