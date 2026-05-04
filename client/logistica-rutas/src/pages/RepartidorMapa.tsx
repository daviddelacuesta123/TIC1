import { useEffect, useRef, useState } from 'react';
import { ROUTES, STATUS_LABELS } from '../data/routes';
import { useAuth } from '../context/AuthContext';
import './RouteMap.css';
import './RepartidorMapa.css';

// ──────────────────────────────────────────────
// Leaflet dynamic loader (shared with RouteMap)
// ──────────────────────────────────────────────
async function loadLeaflet() {
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
interface LeafletRoutingControl { addTo(m: LeafletMap): unknown; }
interface LeafletType {
  map(el: HTMLElement, opts?: unknown): LeafletMap;
  tileLayer(url: string, opts?: unknown): unknown;
  polyline(coords: [number, number][], opts?: unknown): { addTo(m: LeafletMap): unknown; getBounds(): unknown };
  circleMarker(latlng: LatLng | [number, number], opts?: unknown): { addTo(m: LeafletMap): unknown; bindPopup(s: string): unknown };
  latLngBounds(coords: [number, number][]): unknown;
  latLng(lat: number, lng: number): LatLng;
  Routing: { control(opts: unknown): LeafletRoutingControl };
}

// ──────────────────────────────────────────────
// Component
// ──────────────────────────────────────────────
export default function RepartidorMapa() {
  const { user } = useAuth();
  const mapRef = useRef<HTMLDivElement>(null);
  const leafletMapRef = useRef<LeafletMap | null>(null);
  const layersRef = useRef<unknown[]>([]);
  const controlsRef = useRef<unknown[]>([]);
  const [selectedRoute, setSelectedRoute] = useState<string>('');
  const [leaflet, setLeaflet] = useState<LeafletType | null>(null);
  const [mapReady, setMapReady] = useState(false);

  // Filter routes by logged-in driver username
  const myRoutes = ROUTES.filter(r => r.driverUsername === user?.username);

  // Auto-select first assigned route
  useEffect(() => {
    if (myRoutes.length > 0 && !selectedRoute) {
      setSelectedRoute(myRoutes[0].id);
    }
  }, [myRoutes, selectedRoute]);

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

    (map as unknown as { addLayer(l: unknown): void }).addLayer(
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

  // Draw selected route on map
  useEffect(() => {
    if (!leaflet || !leafletMapRef.current || !mapReady || !selectedRoute) return;

    const map = leafletMapRef.current;

    layersRef.current.forEach((layer) => {
      (map as unknown as { removeLayer(l: unknown): void }).removeLayer(layer);
    });
    layersRef.current = [];

    controlsRef.current.forEach((control) => {
      if (typeof map.removeControl === 'function') map.removeControl(control);
    });
    controlsRef.current = [];

    const route = myRoutes.find((r) => r.id === selectedRoute);
    if (!route) return;

    const coords = route.coordinates.map((s) => [s.lat, s.lng] as [number, number]);
    const waypoints = route.coordinates.map(s => leaflet.latLng(s.lat, s.lng));

    const control = leaflet.Routing.control({
      waypoints,
      lineOptions: {
        styles: [{
          color: route.color,
          weight: 5,
          opacity: 0.9,
          dashArray: route.status === 'pending' ? '8 6' : undefined,
        }],
        extendToWaypoints: true,
        missingRouteTolerance: 0,
      },
      createMarker: function () { return null; },
      addWaypoints: false,
      routeWhileDragging: false,
      show: false,
      fitSelectedRoutes: false,
    });

    control.addTo(map);
    controlsRef.current.push(control);

    route.coordinates.forEach((stop, idx) => {
      const isFirst = idx === 0;
      const isLast = idx === route.coordinates.length - 1 && idx !== 0;
      const marker = leaflet.circleMarker([stop.lat, stop.lng], {
        radius: isFirst || isLast ? 11 : 8,
        fillColor: isFirst ? '#111827' : route.color,
        color: 'white',
        weight: 2.5,
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

    const bounds = leaflet.latLngBounds(coords);
    (map as unknown as { fitBounds(b: unknown, o?: unknown): void }).fitBounds(bounds, { padding: [50, 50] });
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

  return (
    <div className="route-map-page">
      {/* Header */}
      <div className="route-map-header">
        <div>
          <h2>Mi Ruta Asignada</h2>
          <p>Visualiza las rutas que te han sido asignadas para hoy</p>
        </div>
        <div className="rep-mapa-badge">
          <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
            <circle cx="12" cy="7" r="4" />
          </svg>
          {user?.username}
        </div>
      </div>

      {myRoutes.length === 0 ? (
        /* No routes assigned */
        <div className="rep-mapa-empty">
          <div className="rep-mapa-empty-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <polygon points="3 6 9 3 15 6 21 3 21 18 15 21 9 18 3 21" />
              <line x1="9" y1="3" x2="9" y2="18" />
              <line x1="15" y1="6" x2="15" y2="21" />
            </svg>
          </div>
          <h3>Sin rutas asignadas</h3>
          <p>
            El administrador aún no te ha asignado ninguna ruta para hoy.<br />
            Vuelve más tarde o contacta a tu coordinador.
          </p>
          <div className="rep-mapa-empty-hint">
            Usuario: <strong>{user?.username}</strong>
          </div>
        </div>
      ) : (
        <div className="route-map-body">
          {/* Sidebar */}
          <div className="route-sidebar">
            {/* Summary */}
            <div className="sidebar-card">
              <h3>Mis rutas de hoy</h3>
              <div className="stats-grid">
                <div className="stat-item">
                  <span className="stat-item-label">Rutas asignadas</span>
                  <span className="stat-item-value gradient-text">{myRoutes.length}</span>
                </div>
                <div className="stat-item">
                  <span className="stat-item-label">Paradas totales</span>
                  <span className="stat-item-value gradient-text">
                    {myRoutes.reduce((acc, r) => acc + r.stops, 0)}
                  </span>
                </div>
                <div className="stat-item">
                  <span className="stat-item-label">Distancia total</span>
                  <span className="stat-item-value" style={{ color: '#09b4db', fontSize: '1rem' }}>
                    {myRoutes.reduce((acc, r) => acc + parseFloat(r.distance), 0).toFixed(1)} km
                  </span>
                </div>
                <div className="stat-item">
                  <span className="stat-item-label">Activas</span>
                  <span className="stat-item-value" style={{ color: '#0ebb8d' }}>
                    {myRoutes.filter(r => r.status === 'active').length}
                  </span>
                </div>
              </div>
            </div>

            {/* Route list */}
            <div className="sidebar-card">
              <h3>Selecciona ruta</h3>
              <div className="route-list">
                {myRoutes.map((route) => (
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
                    </div>
                    <span className={`route-status status-${route.status}`}>
                      {STATUS_LABELS[route.status]}
                    </span>
                  </div>
                ))}
              </div>
            </div>

            {/* Selected route detail */}
            {selectedRoute && (() => {
              const route = myRoutes.find(r => r.id === selectedRoute);
              if (!route) return null;
              return (
                <div className="sidebar-card">
                  <h3>Detalle de paradas</h3>
                  <div className="rep-mapa-stops">
                    {route.coordinates.map((stop, idx) => (
                      <div key={idx} className="rep-mapa-stop-item">
                        <div
                          className="rep-mapa-stop-num"
                          style={{
                            background: idx === 0 ? '#111827' : route.color,
                          }}
                        >
                          {idx === 0 ? (
                            <svg width="10" height="10" viewBox="0 0 24 24" fill="white">
                              <circle cx="12" cy="12" r="8" />
                            </svg>
                          ) : idx + 1}
                        </div>
                        <div className="rep-mapa-stop-name">
                          <span>{stop.name}</span>
                          {idx === 0 && <span className="rep-mapa-stop-tag">Inicio</span>}
                          {idx === route.coordinates.length - 1 && idx !== 0 && (
                            <span className="rep-mapa-stop-tag">Fin</span>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              );
            })()}
          </div>

          {/* Map */}
          <div className="route-map-container">
            <div ref={mapRef} className="map-wrapper" />
            {/* Legend */}
            <div className="map-legend">
              <h4>Mis rutas</h4>
              {myRoutes.map((r) => (
                <div key={r.id} className="legend-item">
                  <div className="legend-dot" style={{ backgroundColor: r.color }} />
                  <span>{r.name}</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
