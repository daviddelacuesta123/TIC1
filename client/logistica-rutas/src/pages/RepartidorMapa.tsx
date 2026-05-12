import { useEffect, useRef, useState } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { useAuth } from '../context/AuthContext';
import { listarRutasRepartidor, type RutaPorRepartidorResponse } from '../services/rutaService';
import './RouteMap.css';
import './RepartidorMapa.css';

const COLORS = ['#2563eb', '#dc2626', '#16a34a', '#d97706', '#7c3aed', '#db2777', '#0891b2', '#65a30d'];

export default function RepartidorMapa() {
  const { user } = useAuth();
  const mapRef = useRef<HTMLDivElement>(null);
  const leafletMapRef = useRef<L.Map | null>(null);
  const layersRef = useRef<L.Layer[]>([]);

  const [rutas, setRutas] = useState<RutaPorRepartidorResponse[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [mapReady, setMapReady] = useState(false);

  useEffect(() => {
    listarRutasRepartidor()
      .then(data => {
        setRutas(data);
        if (data.length > 0) setSelectedId(data[0].repartidorId);
      })
      .catch(err => setError(err instanceof Error ? err.message : 'Error al cargar rutas'))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    if (!mapRef.current || leafletMapRef.current) return;
    const map = L.map(mapRef.current, { center: [6.2476, -75.5709], zoom: 12 });
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors', maxZoom: 19,
    }).addTo(map);
    leafletMapRef.current = map;
    setMapReady(true);
    return () => { map.remove(); leafletMapRef.current = null; };
  }, []);

  useEffect(() => {
    if (!leafletMapRef.current || !mapReady || selectedId === null) return;
    const map = leafletMapRef.current;

    layersRef.current.forEach(l => map.removeLayer(l));
    layersRef.current = [];

    const ruta = rutas.find(r => r.repartidorId === selectedId);
    if (!ruta || ruta.puntos.length === 0) return;

    const color = COLORS[rutas.indexOf(ruta) % COLORS.length];
    const stopCoords: L.LatLngTuple[] = ruta.puntos.map(p => [p.latitud, p.longitud]);
    const coords: L.LatLngTuple[] = ruta.geometria && ruta.geometria.length > 1
      ? ruta.geometria.map(([lng, lat]) => [lat, lng])
      : stopCoords;

    const polyline = L.polyline(coords, {
      color,
      weight: 5,
      opacity: 0.9,
      dashArray: ruta.fuenteDistancias === 'HAVERSINE_FALLBACK' ? '8, 8' : undefined,
    }).addTo(map);
    layersRef.current.push(polyline);

    ruta.puntos.forEach((punto, idx) => {
      const isDepot = idx === 0;
      const marker = L.circleMarker([punto.latitud, punto.longitud], {
        radius: isDepot ? 11 : 8,
        fillColor: isDepot ? '#111827' : color,
        color: 'white',
        weight: 2.5,
        fillOpacity: 1,
      }).bindPopup(
        `<div style="font-family:inherit;font-size:13px;min-width:160px">
          <strong>Parada #${punto.orden}</strong><br/>
          <span>${punto.etiqueta}</span><br/>
          <span style="color:#6b7280;font-size:12px">${punto.etiqueta}</span><br/>
          <span style="color:#09b4db;font-weight:600">ETA: ${punto.etaAcumuladoMinutos} min</span>
        </div>`
      ).addTo(map);
      layersRef.current.push(marker);
    });

    map.fitBounds(L.latLngBounds(stopCoords), { padding: [50, 50] });
  }, [selectedId, mapReady, rutas]);

  const selectedRuta = rutas.find(r => r.repartidorId === selectedId);

  if (loading) {
    return (
      <div className="route-map-page">
        <div className="route-map-header"><div><h2>Mi Ruta Asignada</h2></div></div>
        <p style={{ textAlign: 'center', color: '#6b7280', padding: '2rem' }}>Cargando rutas…</p>
      </div>
    );
  }

  return (
    <div className="route-map-page">
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

      {error && (
        <div style={{ background: 'rgba(239,68,68,0.1)', border: '1px solid #ef4444', borderRadius: 8, padding: '1rem', color: '#ef4444', margin: '0 0 1rem' }}>
          {error}
        </div>
      )}

      {rutas.length === 0 ? (
        <div className="rep-mapa-empty">
          <div className="rep-mapa-empty-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <polygon points="3 6 9 3 15 6 21 3 21 18 15 21 9 18 3 21" />
              <line x1="9" y1="3" x2="9" y2="18" /><line x1="15" y1="6" x2="15" y2="21" />
            </svg>
          </div>
          <h3>Sin rutas asignadas</h3>
          <p>No tienes rutas asignadas hoy.<br />Vuelve más tarde o contacta a tu coordinador.</p>
          <div className="rep-mapa-empty-hint">Usuario: <strong>{user?.username}</strong></div>
        </div>
      ) : (
        <div className="route-map-body">
          <div className="route-sidebar">
            <div className="sidebar-card">
              <h3>Mis rutas de hoy</h3>
              <div className="stats-grid">
                <div className="stat-item">
                  <span className="stat-item-label">Rutas</span>
                  <span className="stat-item-value gradient-text">{rutas.length}</span>
                </div>
                <div className="stat-item">
                  <span className="stat-item-label">Paradas</span>
                  <span className="stat-item-value gradient-text">
                    {rutas.reduce((a, r) => a + r.numeroParadas, 0)}
                  </span>
                </div>
                <div className="stat-item">
                  <span className="stat-item-label">Distancia</span>
                  <span className="stat-item-value" style={{ color: '#09b4db', fontSize: '1rem' }}>
                    {rutas.reduce((a, r) => a + r.distanciaTotal, 0).toFixed(1)} km
                  </span>
                </div>
                <div className="stat-item">
                  <span className="stat-item-label">Tiempo est.</span>
                  <span className="stat-item-value" style={{ color: '#0ebb8d', fontSize: '0.95rem' }}>
                    {Math.floor(rutas.reduce((a, r) => a + r.tiempoEstimadoMinutos, 0) / 60)}h
                    {rutas.reduce((a, r) => a + r.tiempoEstimadoMinutos, 0) % 60}m
                  </span>
                </div>
              </div>
            </div>

            <div className="sidebar-card">
              <h3>Selecciona ruta</h3>
              <div className="route-list">
                {rutas.map((ruta, idx) => (
                  <div
                    key={ruta.repartidorId}
                    className={`route-item ${selectedId === ruta.repartidorId ? 'selected' : ''}`}
                    onClick={() => setSelectedId(ruta.repartidorId)}
                  >
                    <div className="route-dot" style={{ backgroundColor: COLORS[idx % COLORS.length] }} />
                    <div className="route-info">
                      <div className="route-name">Ruta #{ruta.repartidorId}</div>
                      <div className="route-meta">
                        <span>{ruta.numeroParadas} paradas</span>
                        <span>·</span>
                        <span>{ruta.distanciaTotal.toFixed(1)} km</span>
                        <span>·</span>
                        <span>{Math.floor(ruta.tiempoEstimadoMinutos / 60)}h {ruta.tiempoEstimadoMinutos % 60}m</span>
                      </div>
                    </div>
                    <span className={`route-status ${ruta.fuenteDistancias === 'OSRM_VIAL' ? 'status-active' : 'status-pending'}`}>
                      {ruta.fuenteDistancias === 'OSRM_VIAL' ? 'OSRM' : 'Haversine'}
                    </span>
                  </div>
                ))}
              </div>
            </div>

            {selectedRuta && (
              <div className="sidebar-card">
                <h3>Detalle de paradas</h3>
                <div className="rep-mapa-stops">
                  {selectedRuta.puntos.map((punto, idx) => {
                    const color = COLORS[rutas.indexOf(selectedRuta) % COLORS.length];
                    return (
                      <div key={idx} className="rep-mapa-stop-item">
                        <div className="rep-mapa-stop-num" style={{ background: idx === 0 ? '#111827' : color }}>
                          {idx === 0 ? (
                            <svg width="10" height="10" viewBox="0 0 24 24" fill="white"><circle cx="12" cy="12" r="8" /></svg>
                          ) : punto.orden}
                        </div>
                        <div className="rep-mapa-stop-name">
                          <span>{punto.etiqueta}</span>
                          <span style={{ fontSize: '0.75rem', color: '#6b7280' }}>{punto.etiqueta}</span>
                          <span style={{ fontSize: '0.72rem', color: '#09b4db' }}>ETA: {punto.etaAcumuladoMinutos} min</span>
                          {idx === 0 && <span className="rep-mapa-stop-tag">Depósito</span>}
                          {idx === selectedRuta.puntos.length - 1 && idx !== 0 && (
                            <span className="rep-mapa-stop-tag">Fin</span>
                          )}
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>
            )}
          </div>

          <div className="route-map-container">
            <div ref={mapRef} className="map-wrapper" />
            <div className="map-legend">
              <h4>Mis rutas</h4>
              {rutas.map((r, idx) => (
                <div key={r.repartidorId} className="legend-item">
                  <div className="legend-dot" style={{ backgroundColor: COLORS[idx % COLORS.length] }} />
                  <span>Ruta #{r.repartidorId}</span>
                  {r.fuenteDistancias === 'HAVERSINE_FALLBACK' && (
                    <span style={{ fontSize: '0.7rem', color: '#d97706', marginLeft: 4 }}>~</span>
                  )}
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
