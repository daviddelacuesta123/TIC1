import { useEffect, useRef, useState } from 'react';
import { ROUTES, STATUS_LABELS } from '../data/routes';
import './RouteMap.css';

// ──────────────────────────────────────────────
// Leaflet dynamic loader
// ──────────────────────────────────────────────
async function loadLeaflet() {
  // Load CSS
  if (!document.getElementById('leaflet-css')) {
    const link = document.createElement('link');
    link.id = 'leaflet-css';
    link.rel = 'stylesheet';
    link.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css';
    document.head.appendChild(link);
  }
  if (!document.getElementById('leaflet-routing-css')) {
    const link = document.createElement('link');
    link.id = 'leaflet-routing-css';
    link.rel = 'stylesheet';
    link.href = 'https://unpkg.com/leaflet-routing-machine@3.2.12/dist/leaflet-routing-machine.css';
    document.head.appendChild(link);
  }

  // Load JS
  if (!(window as unknown as Record<string, unknown>)['L']) {
    await new Promise<void>((resolve, reject) => {
      const script = document.createElement('script');
      script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Leaflet load failed'));
      document.head.appendChild(script);
    });
  }

  const L = (window as unknown as Record<string, unknown>)['L'] as LeafletType;
  
  if (!L.Routing) {
    await new Promise<void>((resolve, reject) => {
      const script = document.createElement('script');
      script.src = 'https://unpkg.com/leaflet-routing-machine@3.2.12/dist/leaflet-routing-machine.js';
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Leaflet Routing load failed'));
      document.head.appendChild(script);
    });
  }

  return L;
}

// Minimal Leaflet types
interface LatLng { lat: number; lng: number; }
interface LeafletMap {
  remove(): void;
  setView(center: [number, number], zoom: number): LeafletMap;
  fitBounds(bounds: unknown, opts?: unknown): void;
  addLayer(layer: unknown): void;
  removeLayer(layer: unknown): void;
  removeControl(control: unknown): void;
}
interface LeafletRoutingControl {
  addTo(m: LeafletMap): unknown;
}
interface LeafletType {
  map(el: HTMLElement, opts?: unknown): LeafletMap;
  tileLayer(url: string, opts?: unknown): unknown;
  polyline(coords: [number, number][], opts?: unknown): { addTo(m: LeafletMap): unknown; getBounds(): unknown };
  circleMarker(latlng: LatLng | [number, number], opts?: unknown): { addTo(m: LeafletMap): unknown; bindPopup(s: string): unknown };
  latLngBounds(coords: [number, number][]): unknown;
  latLng(lat: number, lng: number): LatLng;
  Routing: {
    control(opts: unknown): LeafletRoutingControl;
  };
}

// ──────────────────────────────────────────────
// Component
// ──────────────────────────────────────────────
interface RouteMapProps {
  onNavigate?: (page: 'dashboard' | 'shipments' | 'routes' | 'new-route') => void;
}

export default function RouteMap({ onNavigate }: RouteMapProps) {
  const mapRef = useRef<HTMLDivElement>(null);
  const leafletMapRef = useRef<LeafletMap | null>(null);
  const layersRef = useRef<unknown[]>([]);
  const controlsRef = useRef<unknown[]>([]);
  const [selectedRoute, setSelectedRoute] = useState<string>('r1');
  const [leaflet, setLeaflet] = useState<LeafletType | null>(null);
  const [mapReady, setMapReady] = useState(false);

  // Load Leaflet once
  useEffect(() => {
    loadLeaflet()
      .then((L) => setLeaflet(L))
      .catch((err) => console.error('Leaflet failed to load:', err));
  }, []);

  // Initialize map once Leaflet is loaded
  useEffect(() => {
    if (!leaflet || !mapRef.current || leafletMapRef.current) return;

    const map = leaflet.map(mapRef.current, {
      center: [6.2476, -75.5709] as [number, number],
      zoom: 12,
    });

    leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      maxZoom: 19,
    } as unknown)
    ;(map as unknown as { addLayer(l: unknown): void }).addLayer(
      leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 19,
      } as unknown)
    );

    leafletMapRef.current = map;
    setMapReady(true);

    return () => {
      map.remove();
      leafletMapRef.current = null;
    };
  }, [leaflet]);

  // Draw routes on map
  useEffect(() => {
    if (!leaflet || !leafletMapRef.current || !mapReady) return;

    const map = leafletMapRef.current;

    // Remove previous layers
    layersRef.current.forEach((layer) => {
      (map as unknown as { removeLayer(l: unknown): void }).removeLayer(layer);
    });
    layersRef.current = [];

    controlsRef.current.forEach((control) => {
      if (typeof map.removeControl === 'function') {
        map.removeControl(control);
      }
    });
    controlsRef.current = [];

    const route = ROUTES.find((r) => r.id === selectedRoute);
    if (!route) return;

    const coords = route.coordinates.map((s) => [s.lat, s.lng] as [number, number]);
    const waypoints = route.coordinates.map(s => leaflet.latLng(s.lat, s.lng));

    // Draw routing line
    const control = leaflet.Routing.control({
      waypoints,
      lineOptions: {
        styles: [{ 
          color: route.color, 
          weight: 4, 
          opacity: 0.85,
          dashArray: route.status === 'pending' ? '8 6' : undefined
        }],
        extendToWaypoints: true,
        missingRouteTolerance: 0
      },
      createMarker: function() { return null; }, // Hide default markers
      addWaypoints: false,
      routeWhileDragging: false,
      show: false, // Don't show text itinerary
      fitSelectedRoutes: false
    });
    
    control.addTo(map);
    controlsRef.current.push(control);

    // Draw markers
    route.coordinates.forEach((stop, idx) => {
      const isFirst = idx === 0;
      const isLast = idx === route.coordinates.length - 1 && idx !== 0;
      const marker = leaflet.circleMarker([stop.lat, stop.lng], {
        radius: isFirst || isLast ? 10 : 7,
        fillColor: isFirst ? '#111827' : route.color,
        color: 'white',
        weight: 2,
        fillOpacity: 1,
      });
      marker.bindPopup(
        `<div style="font-family:inherit;font-size:13px;">
          <strong>${stop.name}</strong><br/>
          <span style="color:#6b7280;">Parada ${idx + 1} de ${route.coordinates.length}</span>
        </div>`
      );
      (map as unknown as { addLayer(l: unknown): void }).addLayer(marker);
      layersRef.current.push(marker);
    });

    // Fit bounds
    const bounds = leaflet.latLngBounds(coords);
    (map as unknown as { fitBounds(b: unknown, o?: unknown): void }).fitBounds(bounds, { padding: [40, 40] });
  }, [leaflet, selectedRoute, mapReady]);

  // Re-init tile layer after map creation
  useEffect(() => {
    if (!leaflet || !leafletMapRef.current || !mapReady) return;
    const tile = leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      maxZoom: 19,
    } as unknown);
    (leafletMapRef.current as unknown as { addLayer(l: unknown): void }).addLayer(tile);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [mapReady]);

  const activeCount = ROUTES.filter((r) => r.status === 'active').length;
  const pendingCount = ROUTES.filter((r) => r.status === 'pending').length;

  return (
    <div className="route-map-page">
      {/* Header */}
      <div className="route-map-header">
        <div>
          <h2>Mapa de Rutas</h2>
          <p>Visualiza y gestiona las rutas de entrega en tiempo real</p>
        </div>
        <div className="route-map-actions">
          <button className="btn-secondary">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" />
              <polyline points="7 10 12 15 17 10" />
              <line x1="12" y1="15" x2="12" y2="3" />
            </svg>
            Exportar
          </button>
          <button className="btn-primary" onClick={() => onNavigate && onNavigate('new-route')}>
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="12" y1="8" x2="12" y2="16" />
              <line x1="8" y1="12" x2="16" y2="12" />
            </svg>
            Nueva Ruta
          </button>
        </div>
      </div>

      {/* Body */}
      <div className="route-map-body">
        {/* Sidebar */}
        <div className="route-sidebar">
          {/* Stats */}
          <div className="sidebar-card">
            <h3>Resumen del día</h3>
            <div className="stats-grid">
              <div className="stat-item">
                <span className="stat-item-label">Rutas totales</span>
                <span className="stat-item-value gradient-text">{ROUTES.length}</span>
              </div>
              <div className="stat-item">
                <span className="stat-item-label">Activas</span>
                <span className="stat-item-value gradient-text">{activeCount}</span>
              </div>
              <div className="stat-item">
                <span className="stat-item-label">Pendientes</span>
                <span className="stat-item-value" style={{ color: '#f59e0b' }}>{pendingCount}</span>
              </div>
              <div className="stat-item">
                <span className="stat-item-label">Completadas</span>
                <span className="stat-item-value" style={{ color: '#6b7280' }}>
                  {ROUTES.filter((r) => r.status === 'completed').length}
                </span>
              </div>
            </div>
          </div>

          {/* Routes */}
          <div className="sidebar-card">
            <h3>Rutas activas</h3>
            <div className="route-list">
              {ROUTES.map((route) => (
                <div
                  key={route.id}
                  className={`route-item ${selectedRoute === route.id ? 'selected' : ''}`}
                  onClick={() => setSelectedRoute(route.id)}
                >
                  <div className="route-dot" style={{ backgroundColor: route.color }} />
                  <div className="route-info">
                    <div className="route-name">{route.name}</div>
                    <div className="route-meta">
                      <span>{route.stops} paradas</span>
                      <span>·</span>
                      <span>{route.distance}</span>
                      <span>·</span>
                      <span>{route.eta}</span>
                    </div>
                    <div style={{ fontSize: '0.75rem', color: '#9ca3af', marginTop: 2 }}>
                      {route.driver}
                    </div>
                  </div>
                  <span className={`route-status status-${route.status}`}>
                    {STATUS_LABELS[route.status]}
                  </span>
                </div>
              ))}
            </div>
          </div>
        </div>

        {/* Map */}
        <div className="route-map-container">
          <div ref={mapRef} className="map-wrapper" />

          {/* Legend */}
          <div className="map-legend">
            <h4>Leyenda</h4>
            {ROUTES.map((r) => (
              <div key={r.id} className="legend-item">
                <div className="legend-dot" style={{ backgroundColor: r.color }} />
                <span>{r.name}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
