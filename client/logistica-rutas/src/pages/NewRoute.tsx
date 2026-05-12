import { useEffect, useRef, useState } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import './RouteMap.css';
import './NewRoute.css';
import { geocodificar } from '../services/geoService';

interface NewRouteProps {
  onNavigate?: (page: 'dashboard' | 'shipments' | 'routes' | 'new-route') => void;
}

interface Stop {
  id: string;
  name: string;
  lat: number;
  lng: number;
}

const COLORS = ['#09b4db', '#0ebb8d', '#f59e0b', '#8b5cf6', '#ef4444', '#ec4899'];
const DEPOT = { lat: 6.2476, lng: -75.5709, name: 'Depósito Central' };

export default function NewRoute({ onNavigate }: NewRouteProps) {
  const mapRef = useRef<HTMLDivElement>(null);
  const leafletMapRef = useRef<L.Map | null>(null);
  const polylinesRef = useRef<L.Polyline[]>([]);
  const markersRef = useRef<L.CircleMarker[]>([]);
  const [mapReady, setMapReady] = useState(false);

  const [driversCount, setDriversCount] = useState<number>(1);
  const [searchQuery, setSearchQuery] = useState('');
  const [stops, setStops] = useState<Stop[]>([]);
  const [isSearching, setIsSearching] = useState(false);
  const [geoError, setGeoError] = useState<string | null>(null);

  useEffect(() => {
    if (!mapRef.current || leafletMapRef.current) return;
    const map = L.map(mapRef.current, { center: [DEPOT.lat, DEPOT.lng], zoom: 12 });
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap contributors',
    }).addTo(map);
    L.circleMarker([DEPOT.lat, DEPOT.lng], {
      radius: 10, fillColor: '#111827', color: 'white', weight: 2, fillOpacity: 1,
    }).addTo(map).bindPopup('Depósito Central');
    leafletMapRef.current = map;
    setMapReady(true);
    return () => { map.remove(); leafletMapRef.current = null; };
  }, []);

  async function handleSearch() {
    const q = searchQuery.trim();
    if (!q) return;
    setIsSearching(true);
    setGeoError(null);
    try {
      const ciudad = q.toLowerCase().includes('medellín') || q.toLowerCase().includes('medellin')
        ? '' : 'Medellín';
      const coords = await geocodificar(q, ciudad);
      const newStop: Stop = {
        id: `${q}-${coords.latitud}-${coords.longitud}`,
        name: q,
        lat: coords.latitud,
        lng: coords.longitud,
      };
      if (!stops.some(s => s.id === newStop.id)) setStops(prev => [...prev, newStop]);
      setSearchQuery('');
    } catch {
      setGeoError('No se pudo geocodificar la dirección. Intenta con más detalle.');
    } finally {
      setIsSearching(false);
    }
  }

  const removeStop = (id: string) => setStops(stops.filter(s => s.id !== id));

  useEffect(() => {
    if (!mapReady || !leafletMapRef.current) return;
    const map = leafletMapRef.current;

    polylinesRef.current.forEach(p => map.removeLayer(p));
    polylinesRef.current = [];
    markersRef.current.forEach(m => map.removeLayer(m));
    markersRef.current = [];

    if (stops.length === 0) return;

    stops.forEach(stop => {
      const marker = L.circleMarker([stop.lat, stop.lng], {
        radius: 8, fillColor: '#10b981', color: '#fff', weight: 2, fillOpacity: 1,
      }).bindPopup(`<div style="font-size:13px"><strong>${stop.name}</strong></div>`).addTo(map);
      markersRef.current.push(marker);
    });

    const chunks: Stop[][] = Array.from({ length: driversCount }, () => []);
    stops.forEach((stop, i) => chunks[i % driversCount].push(stop));

    chunks.forEach((chunk, i) => {
      if (chunk.length === 0) return;
      const latlngs: L.LatLngTuple[] = [
        [DEPOT.lat, DEPOT.lng],
        ...chunk.map(c => [c.lat, c.lng] as L.LatLngTuple),
      ];
      const pl = L.polyline(latlngs, {
        color: COLORS[i % COLORS.length], weight: 4, opacity: 0.8,
      }).addTo(map);
      polylinesRef.current.push(pl);
    });

    const allCoords: L.LatLngTuple[] = stops.map(s => [s.lat, s.lng]);
    if (allCoords.length > 0) map.fitBounds(L.latLngBounds(allCoords), { padding: [40, 40] });
  }, [mapReady, stops, driversCount]);

  return (
    <div className="new-route-page route-map-page">
      <div className="route-map-header">
        <div>
          <h2>Previsualizar Ruta</h2>
          <p>Agrega direcciones manualmente para ver la distribución en el mapa.</p>
        </div>
        <div className="route-map-actions">
          <button className="btn-secondary" onClick={() => onNavigate && onNavigate('routes')}>
            Ver mis rutas
          </button>
        </div>
      </div>

      <div className="route-map-body">
        <div className="route-sidebar new-route-form">
          <div className="sidebar-card">
            <h3>Configuración</h3>
            <div className="form-group drivers-count-group">
              <label>Cantidad de Repartidores</label>
              <input
                type="number" min="1" max="10" value={driversCount}
                onChange={e => setDriversCount(Math.max(1, parseInt(e.target.value) || 1))}
              />
            </div>
          </div>

          <div className="sidebar-card address-card">
            <h3>Agregar Direcciones</h3>
            <p className="instruction-text">
              Escribe una dirección y pulsa "Agregar". Se geocodificará con el servidor.
            </p>
            <div className="search-container">
              <input
                type="text"
                placeholder="Ej: Calle 10 #43-12, Medellín"
                value={searchQuery}
                onChange={e => { setSearchQuery(e.target.value); setGeoError(null); }}
                onKeyDown={e => e.key === 'Enter' && handleSearch()}
                className="address-input"
              />
              <button
                onClick={handleSearch}
                disabled={isSearching || !searchQuery.trim()}
                style={{
                  marginTop: '0.5rem', width: '100%', padding: '0.6rem',
                  background: '#09b4db', border: 'none', borderRadius: 8,
                  color: 'white', fontWeight: 600, cursor: 'pointer', fontSize: '0.9rem',
                }}
              >
                {isSearching ? 'Geocodificando…' : 'Agregar dirección'}
              </button>
              {geoError && (
                <p style={{ color: '#ef4444', fontSize: '0.8rem', marginTop: '0.4rem' }}>{geoError}</p>
              )}
            </div>
            <div className="stops-list-container">
              <h4>Direcciones Asignadas ({stops.length})</h4>
              {stops.length === 0 ? (
                <div className="empty-stops">No hay direcciones agregadas aún.</div>
              ) : (
                <ul className="stops-list">
                  {stops.map(stop => (
                    <li key={stop.id}>
                      <span>{stop.name}</span>
                      <button onClick={() => removeStop(stop.id)} className="remove-btn">&times;</button>
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </div>
        </div>

        <div className="route-map-container">
          <div ref={mapRef} className="map-wrapper" />
          <div className="map-legend">
            <h4>Repartidores ({Math.min(driversCount, stops.length)})</h4>
            {Array.from({ length: Math.min(driversCount, stops.length) }).map((_, idx) => (
              <div key={idx} className="legend-item">
                <div className="legend-dot" style={{ backgroundColor: COLORS[idx % COLORS.length] }} />
                <span>
                  Repartidor {idx + 1} ({stops.filter((_, i) => i % driversCount === idx).length} paradas)
                </span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
