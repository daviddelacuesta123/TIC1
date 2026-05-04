// ──────────────────────────────────────────────
// Shared route data – used by Admin and Repartidor views
// ──────────────────────────────────────────────

export interface Stop {
  name: string;
  lat: number;
  lng: number;
}

export interface Route {
  id: string;
  name: string;
  driver: string;
  /** Username (JWT sub) of the assigned driver */
  driverUsername: string;
  stops: number;
  distance: string;
  eta: string;
  status: 'active' | 'pending' | 'completed';
  color: string;
  coordinates: Stop[];
}

export const ROUTES: Route[] = [
  {
    id: 'r1',
    name: 'Ruta Norte',
    driver: 'Carlos Mendoza',
    driverUsername: 'carlos',
    stops: 6,
    distance: '24.3 km',
    eta: '2h 15m',
    status: 'active',
    color: '#09b4db',
    coordinates: [
      { name: 'Depósito Central', lat: 6.2476, lng: -75.5709 },
      { name: 'Bello Centro', lat: 6.3354, lng: -75.5587 },
      { name: 'Niquia', lat: 6.3577, lng: -75.5441 },
      { name: 'Hatillo', lat: 6.3221, lng: -75.5665 },
      { name: 'La Madera', lat: 6.3089, lng: -75.5621 },
      { name: 'Depósito Central', lat: 6.2476, lng: -75.5709 },
    ],
  },
  {
    id: 'r2',
    name: 'Ruta Sur',
    driver: 'María González',
    driverUsername: 'maria',
    stops: 5,
    distance: '18.7 km',
    eta: '1h 45m',
    status: 'active',
    color: '#0ebb8d',
    coordinates: [
      { name: 'Depósito Central', lat: 6.2476, lng: -75.5709 },
      { name: 'Envigado', lat: 6.1716, lng: -75.5847 },
      { name: 'Sabaneta', lat: 6.1514, lng: -75.6155 },
      { name: 'La Estrella', lat: 6.1558, lng: -75.6451 },
      { name: 'Itagüí', lat: 6.1843, lng: -75.5991 },
      { name: 'Depósito Central', lat: 6.2476, lng: -75.5709 },
    ],
  },
  {
    id: 'r3',
    name: 'Ruta Centro',
    driver: 'Juan Ríos',
    driverUsername: 'juan',
    stops: 4,
    distance: '11.2 km',
    eta: '1h 10m',
    status: 'pending',
    color: '#f59e0b',
    coordinates: [
      { name: 'Depósito Central', lat: 6.2476, lng: -75.5709 },
      { name: 'El Poblado', lat: 6.2087, lng: -75.5671 },
      { name: 'Laureles', lat: 6.2421, lng: -75.5978 },
      { name: 'Estadio', lat: 6.2568, lng: -75.5897 },
      { name: 'Depósito Central', lat: 6.2476, lng: -75.5709 },
    ],
  },
  {
    id: 'r4',
    name: 'Ruta Oeste',
    driver: 'Ana Castillo',
    driverUsername: 'ana',
    stops: 5,
    distance: '20.5 km',
    eta: '2h 00m',
    status: 'completed',
    color: '#8b5cf6',
    coordinates: [
      { name: 'Depósito Central', lat: 6.2476, lng: -75.5709 },
      { name: 'Belén', lat: 6.2286, lng: -75.6130 },
      { name: 'San Javier', lat: 6.2513, lng: -75.6199 },
      { name: 'Robledo', lat: 6.2821, lng: -75.5967 },
      { name: 'Castilla', lat: 6.2958, lng: -75.5742 },
      { name: 'Depósito Central', lat: 6.2476, lng: -75.5709 },
    ],
  },
  {
    id: 'r5',
    name: 'Ruta Demo',
    driver: 'Repartidor Demo',
    driverUsername: 'repartidor_demo',
    stops: 4,
    distance: '15.8 km',
    eta: '1h 30m',
    status: 'active',
    color: '#ec4899',
    coordinates: [
      { name: 'Depósito Central', lat: 6.2476, lng: -75.5709 },
      { name: 'Aranjuez', lat: 6.2763, lng: -75.5618 },
      { name: 'Manrique', lat: 6.2878, lng: -75.5537 },
      { name: 'Villa del Socorro', lat: 6.2991, lng: -75.5513 },
      { name: 'Depósito Central', lat: 6.2476, lng: -75.5709 },
    ],
  },
];

export const STATUS_LABELS: Record<string, string> = {
  active: 'Activa',
  pending: 'Pendiente',
  completed: 'Completada',
};
