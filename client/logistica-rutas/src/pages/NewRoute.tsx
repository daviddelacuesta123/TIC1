/* eslint-disable @typescript-eslint/no-explicit-any */
import { useEffect, useRef, useState } from 'react';
import './RouteMap.css'; // Reusing layout CSS
import './NewRoute.css';

// Minimal Leaflet types and map initialization
async function loadRoutingLeaflet() {
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

  if (!(window as any)['L']) {
    await new Promise<void>((resolve, reject) => {
      const script = document.createElement('script');
      script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js';
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Leaflet load failed'));
      document.head.appendChild(script);
    });
  }
  
  if (!(window as any)['L']?.Routing) {
    await new Promise<void>((resolve, reject) => {
      const script = document.createElement('script');
      script.src = 'https://unpkg.com/leaflet-routing-machine@3.2.12/dist/leaflet-routing-machine.js';
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Leaflet Routing Machine load failed'));
      document.head.appendChild(script);
    });
  }
  
  return (window as any)['L'];
}

interface NewRouteProps {
  onNavigate?: (page: 'dashboard' | 'shipments' | 'routes' | 'new-route') => void;
}

interface AddressResult {
  place_id: string;
  display_name: string;
  lat: string;
  lon: string;
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
  const leafletMapRef = useRef<any>(null);
  const routingControlsRef = useRef<any[]>([]);
  const markersRef = useRef<any[]>([]);
  
  const [leaflet, setLeaflet] = useState<any>(null);
  const [mapReady, setMapReady] = useState(false);

  // Form State
  const [driversCount, setDriversCount] = useState<number>(1);
  const [searchQuery, setSearchQuery] = useState('');
  const [suggestions, setSuggestions] = useState<AddressResult[]>([]);
  const [stops, setStops] = useState<Stop[]>([]);
  const [isSearching, setIsSearching] = useState(false);

  // Load Leaflet and Map
  useEffect(() => {
    loadRoutingLeaflet().then(setLeaflet).catch(console.error);
  }, []);

  useEffect(() => {
    if (!leaflet || !mapRef.current || leafletMapRef.current) return;
    const map = leaflet.map(mapRef.current, {
      center: [DEPOT.lat, DEPOT.lng],
      zoom: 12,
    });
    leaflet.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap contributors'
    }).addTo(map);

    // Initial Marker
    leaflet.marker([DEPOT.lat, DEPOT.lng]).addTo(map).bindPopup('Depósito Central (Mendoza)');

    leafletMapRef.current = map;
    setTimeout(() => setMapReady(true), 0);

    return () => {
      map.remove();
      leafletMapRef.current = null;
    };
  }, [leaflet]);

  // Autocomplete search
  useEffect(() => {
    if (!searchQuery || searchQuery.length < 3) {
      setTimeout(() => setSuggestions([]), 0);
      return;
    }
    const delayDebounceFn = setTimeout(() => {
      setIsSearching(true);
      // Apend Medellin to query if not present
      const query = searchQuery.toLowerCase().includes('medellin') 
        ? searchQuery 
        : `${searchQuery}, Medellín, Colombia`;
        
      fetch(`https://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer/findAddressCandidates?f=json&singleLine=${encodeURIComponent(query)}&maxLocations=8`)
        .then(res => res.json())
        .then((data) => {
          if (data.candidates) {
            const results = data.candidates.map((c: any) => ({
              place_id: c.address + c.location.x,
              display_name: c.address,
              lat: c.location.y.toString(),
              lon: c.location.x.toString()
            }));
            setSuggestions(results);
          } else {
            setSuggestions([]);
          }
          setIsSearching(false);
        }).catch(() => setIsSearching(false));
    }, 600);

    return () => clearTimeout(delayDebounceFn);
  }, [searchQuery]);

  const addStop = (item: AddressResult) => {
    const newStop: Stop = {
      id: item.place_id,
      name: item.display_name,
      lat: parseFloat(item.lat),
      lng: parseFloat(item.lon)
    };
    if (!stops.some(s => s.id === newStop.id)) {
      setStops([...stops, newStop]);
    }
    setSearchQuery('');
    setSuggestions([]);
  };

  const removeStop = (id: string) => {
    setStops(stops.filter(s => s.id !== id));
  };

  // Draw routes dynamically based on drivers count and stops
  useEffect(() => {
    if (!leaflet || !mapReady || !leafletMapRef.current) return;
    const map = leafletMapRef.current;

    // Remove old routing controls and markers
    routingControlsRef.current.forEach(control => map.removeControl(control));
    routingControlsRef.current = [];
    markersRef.current.forEach(marker => map.removeLayer(marker));
    markersRef.current = [];

    if (stops.length === 0) return;

    // Draw markers for all stops
    stops.forEach((stop) => {
      const marker = leaflet.circleMarker([stop.lat, stop.lng], {
        radius: 8,
        fillColor: '#10b981',
        color: '#fff',
        weight: 2,
        fillOpacity: 1
      }).bindPopup(`<div style="font-size:13px"><strong>${stop.name}</strong></div>`);
      map.addLayer(marker);
      markersRef.current.push(marker);
    });

    // Simple partitioning: distribute stops evenly among drivers
    const chunks: Stop[][] = Array.from({ length: driversCount }, () => []);
    stops.forEach((stop, i) => chunks[i % driversCount].push(stop));

    chunks.forEach((chunk, i) => {
      if (chunk.length === 0) return;
      
      const waypoints = [
        leaflet.latLng(DEPOT.lat, DEPOT.lng),
        ...chunk.map(c => leaflet.latLng(c.lat, c.lng))
      ];

      const control = leaflet.Routing.control({
        waypoints,
        lineOptions: {
          styles: [{ color: COLORS[i % COLORS.length], weight: 4, opacity: 0.8 }]
        },
        createMarker: function() { return null; }, // hide default markers since we can just use the line
        addWaypoints: false,
        routeWhileDragging: false,
        show: false, // Don't show text itinerary
        fitSelectedRoutes: true
      }).addTo(map);

      routingControlsRef.current.push(control);
    });

  }, [leaflet, mapReady, stops, driversCount]);

  return (
    <div className="new-route-page route-map-page">
      <div className="route-map-header">
        <div>
          <h2>Crear Nueva Ruta</h2>
          <p>Asigna las direcciones a una cantidad definida de repartidores.</p>
        </div>
        <div className="route-map-actions">
          <button className="btn-secondary" onClick={() => onNavigate && onNavigate('routes')}>
            Ver mis rutas
          </button>
          <button className="btn-primary" onClick={() => onNavigate && onNavigate('routes')}>
            Guardar Rutas
          </button>
        </div>
      </div>

      <div className="route-map-body">
        {/* Left Form Panel */}
        <div className="route-sidebar new-route-form">
          <div className="sidebar-card">
            <h3>Configuración</h3>
            <div className="form-group drivers-count-group">
              <label>Cantidad de Repartidores</label>
              <input 
                type="number" 
                min="1" 
                max="10" 
                value={driversCount}
                onChange={e => setDriversCount(Math.max(1, parseInt(e.target.value) || 1))}
              />
            </div>
          </div>

          <div className="sidebar-card address-card">
            <h3>1. Agregar Direcciones</h3>
            <p className="instruction-text">
              Busca las direcciones a entregar. Se distribuirán automáticamente entre los repartidores.
            </p>
            <div className="search-container">
              <input
                type="text"
                placeholder="Busca una dirección (Ej: Medellín, Poblado...)"
                value={searchQuery}
                onChange={e => setSearchQuery(e.target.value)}
                className="address-input"
              />
              {isSearching && <div className="searching-indicator">Buscando...</div>}
              {suggestions.length > 0 && (
                <ul className="suggestions-list">
                  {suggestions.map(s => (
                    <li key={s.place_id} onClick={() => addStop(s)}>
                      {s.display_name}
                    </li>
                  ))}
                </ul>
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
                      <button onClick={() => removeStop(stop.id)} className="remove-btn">
                        &times;
                      </button>
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </div>
        </div>

        {/* Map Panel */}
        <div className="route-map-container">
          <div ref={mapRef} className="map-wrapper" />
          <div className="map-legend">
            <h4>Repartidores ({Math.min(driversCount, stops.length)})</h4>
            {Array.from({ length: Math.min(driversCount, stops.length) }).map((_, idx) => (
              <div key={idx} className="legend-item">
                <div className="legend-dot" style={{ backgroundColor: COLORS[idx % COLORS.length] }} />
                <span>Repartidor {idx + 1} ({stops.filter((_, i) => i % driversCount === idx).length} paradas)</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
