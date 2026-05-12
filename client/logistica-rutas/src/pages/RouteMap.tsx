import { useCallback, useEffect, useRef, useState } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { listarSesiones, type RutaPorRepartidorResponse, type SesionDespachoResponse } from '../services/rutaService';
import './RouteMap.css';

const ROUTE_COLORS = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#06b6d4', '#84cc16'];

interface RouteMapProps {
  onNavigate?: (page: 'dashboard' | 'shipments' | 'routes' | 'new-route' | 'sesion-despacho') => void;
}

export default function RouteMap({ onNavigate }: RouteMapProps) {
  const mapRef = useRef<HTMLDivElement>(null);
  const leafletMapRef = useRef<L.Map | null>(null);
  const layersRef = useRef<L.Layer[]>([]);
  const [sesion, setSesion] = useState<SesionDespachoResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedRouteId, setSelectedRouteId] = useState<string | null>(null);
  const [mapReady, setMapReady] = useState(false);

  // Initialize map once on mount
  useEffect(() => {
    if (!mapRef.current || leafletMapRef.current) return;

    const map = L.map(mapRef.current, {
      center: [6.2476, -75.5709],
      zoom: 12,
    });

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
      maxZoom: 19,
    }).addTo(map);

    leafletMapRef.current = map;
    setMapReady(true);

    return () => {
      map.remove();
      leafletMapRef.current = null;
    };
  }, []);

  // Load most recent dispatched session
  const fetchSesiones = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const sesiones = await listarSesiones();
      const latest = sesiones.sort(
        (a, b) => new Date(b.fechaCreacion).getTime() - new Date(a.fechaCreacion).getTime()
      )[0] ?? null;
      setSesion(latest);
      if (latest && latest.rutas.length > 0) {
        setSelectedRouteId(latest.rutas[0].repartidorId);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al cargar sesiones');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchSesiones(); }, [fetchSesiones]);

  // Draw selected route whenever selection, map, or session data changes
  useEffect(() => {
    if (!leafletMapRef.current || !mapReady || !sesion) return;

    const map = leafletMapRef.current;
    layersRef.current.forEach((layer) => map.removeLayer(layer));
    layersRef.current = [];

    const routeIdx = sesion.rutas.findIndex((r) => r.repartidorId === selectedRouteId);
    const ruta: RutaPorRepartidorResponse | undefined = sesion.rutas[routeIdx];
    if (!ruta || ruta.puntos.length === 0) return;

    const color = ROUTE_COLORS[routeIdx % ROUTE_COLORS.length];
    const stopCoords: L.LatLngTuple[] = ruta.puntos.map((p) => [p.latitud, p.longitud]);
    // Use OSRM road geometry if available, else fall back to straight lines between stops
    const coords: L.LatLngTuple[] = ruta.geometria && ruta.geometria.length > 1
      ? ruta.geometria.map(([lng, lat]) => [lat, lng])
      : stopCoords;

    const polyline = L.polyline(coords, {
      color,
      weight: 4,
      opacity: 0.85,
      dashArray: ruta.fuenteDistancias === 'HAVERSINE_FALLBACK' ? '8, 8' : undefined,
    }).addTo(map);
    layersRef.current.push(polyline);

    map.fitBounds(L.latLngBounds(stopCoords), { padding: [40, 40] });

    ruta.puntos.forEach((punto, idx) => {
      const isDeposito = idx === 0;
      const marker = L.circleMarker([punto.latitud, punto.longitud], {
        radius: isDeposito ? 10 : 7,
        fillColor: isDeposito ? '#111827' : color,
        color: 'white',
        weight: 2,
        fillOpacity: 1,
      }).bindPopup(
        `<div style="font-family:inherit;font-size:13px;">
          <strong>${isDeposito ? 'Depósito' : punto.etiqueta}</strong><br/>
          <span style="color:#6b7280;">${isDeposito ? 'Punto de salida' : punto.etiqueta}</span>
          ${!isDeposito ? `<br/><span style="color:#09b4db;">ETA: ${punto.etaAcumuladoMinutos} min · Parada #${punto.orden}</span>` : ''}
        </div>`
      ).addTo(map);
      layersRef.current.push(marker);
    });

  }, [selectedRouteId, mapReady, sesion]);

  const rutas = sesion?.rutas ?? [];

  return (
    <div className="route-map-page">
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
          <button className="btn-primary" onClick={() => onNavigate && onNavigate('sesion-despacho')}>
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10" />
              <line x1="12" y1="8" x2="12" y2="16" />
              <line x1="8" y1="12" x2="16" y2="12" />
            </svg>
            Nueva sesión
          </button>
        </div>
      </div>

      <div className="route-map-body">
        <div className="route-sidebar">
          <div className="sidebar-card">
            <h3>Resumen del día</h3>
            {loading ? (
              <p style={{ color: '#9ca3af', fontSize: '0.85rem' }}>Cargando…</p>
            ) : sesion ? (
              <div className="stats-grid">
                <div className="stat-item">
                  <span className="stat-item-label">Rutas totales</span>
                  <span className="stat-item-value gradient-text">{rutas.length}</span>
                </div>
                <div className="stat-item">
                  <span className="stat-item-label">Pedidos</span>
                  <span className="stat-item-value gradient-text">{sesion.totalPedidos}</span>
                </div>
                <div className="stat-item">
                  <span className="stat-item-label">KM totales</span>
                  <span className="stat-item-value" style={{ color: '#09b4db' }}>
                    {sesion.kmTotales.toFixed(1)}
                  </span>
                </div>
                <div className="stat-item">
                  <span className="stat-item-label">Repartidores</span>
                  <span className="stat-item-value" style={{ color: '#6b7280' }}>
                    {sesion.totalRepartidores}
                  </span>
                </div>
              </div>
            ) : (
              <p style={{ color: '#9ca3af', fontSize: '0.85rem' }}>Sin sesiones despachadas</p>
            )}
          </div>

          <div className="sidebar-card">
            <h3>Rutas despachadas</h3>
            {error && (
              <p style={{ color: '#ef4444', fontSize: '0.82rem', marginBottom: '0.5rem' }}>{error}</p>
            )}
            <div className="route-list">
              {!loading && rutas.length === 0 ? (
                <p style={{ color: '#9ca3af', fontSize: '0.85rem', padding: '0.5rem 0' }}>
                  No hay rutas despachadas
                </p>
              ) : (
                rutas.map((ruta, idx) => {
                  const color = ROUTE_COLORS[idx % ROUTE_COLORS.length];
                  return (
                    <div
                      key={ruta.repartidorId}
                      className={`route-item ${selectedRouteId === ruta.repartidorId ? 'selected' : ''}`}
                      onClick={() => setSelectedRouteId(ruta.repartidorId)}
                    >
                      <div className="route-dot" style={{ backgroundColor: color }} />
                      <div className="route-info">
                        <div className="route-name">{ruta.repartidorNombre}</div>
                        <div className="route-meta">
                          <span>{ruta.numeroParadas} paradas</span>
                          <span>·</span>
                          <span>{ruta.distanciaTotal.toFixed(1)} km</span>
                          <span>·</span>
                          <span>{ruta.tiempoEstimadoMinutos} min</span>
                        </div>
                        <div style={{ fontSize: '0.75rem', color: '#9ca3af', marginTop: 2 }}>
                          {ruta.clasificacionCosto}
                        </div>
                      </div>
                      <span className="route-status status-active">Despachada</span>
                    </div>
                  );
                })
              )}
            </div>
          </div>
        </div>

        <div className="route-map-container">
          <div ref={mapRef} className="map-wrapper" />
          <div className="map-legend">
            <h4>Leyenda</h4>
            {rutas.map((r, idx) => (
              <div key={r.repartidorId} className="legend-item">
                <div className="legend-dot" style={{ backgroundColor: ROUTE_COLORS[idx % ROUTE_COLORS.length] }} />
                <span>{r.repartidorNombre}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
