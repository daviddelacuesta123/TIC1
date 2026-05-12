import { useEffect, useRef, useState } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import type { Page } from '../App';
import {
  listarRepartidores,
  obtenerVehiculo,
  type RepartidorResponseDTO,
  type AsignacionVehiculoResponseDTO,
} from '../services/repartidorService';
import {
  listarPedidos,
  geocodificarPedido,
  type PedidoResponse,
} from '../services/pedidoService';
import {
  crearSesionDespacho,
  despacharSesion,
  cancelarSesion,
  type SesionDespachoResponse,
  type RutaPorRepartidorResponse,
} from '../services/rutaService';
import { listarVehiculosActivos, type VehiculoResponseDTO } from '../services/vehiculoService';
import './SesionDespacho.css';

const ROUTE_COLORS = ['#2563eb','#dc2626','#16a34a','#d97706','#7c3aed','#db2777','#0891b2','#65a30d'];
const DEPOT = { latitud: 6.2476, longitud: -75.5709 };

interface Props {
  onNavigate: (page: Page) => void;
}

export default function SesionDespacho({ onNavigate }: Props) {
  // ── Paso actual ──
  const [paso, setPaso] = useState<1 | 2 | 3>(1);

  // ── Paso A — datos ──
  const [pedidos, setPedidos] = useState<PedidoResponse[]>([]);
  const [repartidores, setRepartidores] = useState<RepartidorResponseDTO[]>([]);
  const [asignaciones, setAsignaciones] = useState<Map<number, AsignacionVehiculoResponseDTO>>(new Map());
  const [selectedPedidos, setSelectedPedidos] = useState<Set<number>>(new Set());
  const [selectedReps, setSelectedReps] = useState<Set<number>>(new Set());
  const [deposito, setDeposito] = useState(DEPOT);
  const [geocodificando, setGeocodificando] = useState<Set<number>>(new Set());
  const [loadingInicial, setLoadingInicial] = useState(true);
  const [errorA, setErrorA] = useState<string | null>(null);

  // ── Paso B ──
  const [errorB, setErrorB] = useState<string | null>(null);

  // ── Paso C — resultado ──
  const [sesion, setSesion] = useState<SesionDespachoResponse | null>(null);
  const [vehiculos, setVehiculos] = useState<Map<number, VehiculoResponseDTO>>(new Map());
  const [selectedRutaId, setSelectedRutaId] = useState<string | null>(null);
  const [visibleRutas, setVisibleRutas] = useState<Set<string>>(new Set());
  const [showModal, setShowModal] = useState(false);
  const [despachando, setDespachando] = useState(false);
  const [cancelando, setCancelando] = useState(false);
  const [exitoMsg, setExitoMsg] = useState(false);

  // ── Mapa Paso C ──
  const mapaRef = useRef<HTMLDivElement>(null);
  const leafletRef = useRef<L.Map | null>(null);
  const capasRef = useRef<L.Layer[]>([]);

  // ── Carga inicial ──
  useEffect(() => {
    async function cargar() {
      try {
        const [peds, reps, vehs] = await Promise.all([listarPedidos(), listarRepartidores(), listarVehiculosActivos()]);
        setPedidos(peds.filter(p => p.estado === 'PENDIENTE'));
        const activos = reps.filter(r => r.estado);
        setRepartidores(activos);

        const vehMap = new Map<number, VehiculoResponseDTO>();
        vehs.forEach(v => vehMap.set(v.id, v));
        setVehiculos(vehMap);

        const asgResults = await Promise.allSettled(activos.map(r => obtenerVehiculo(r.id)));
        const map = new Map<number, AsignacionVehiculoResponseDTO>();
        asgResults.forEach((res, idx) => {
          if (res.status === 'fulfilled' && res.value !== null) map.set(activos[idx].id, res.value);
        });
        setAsignaciones(map);
      } catch (err) {
        setErrorA(err instanceof Error ? err.message : 'Error al cargar datos');
      } finally {
        setLoadingInicial(false);
      }
    }
    cargar();
  }, []);

  // ── Mapa Paso C: inicializar ──
  useEffect(() => {
    if (paso !== 3 || !sesion) return;
    const timer = setTimeout(() => {
      if (!mapaRef.current || leafletRef.current) return;
      const map = L.map(mapaRef.current, { center: [deposito.latitud, deposito.longitud], zoom: 12 });
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 19, attribution: '© OpenStreetMap contributors',
      }).addTo(map);
      leafletRef.current = map;
    }, 50);
    return () => {
      clearTimeout(timer);
      leafletRef.current?.remove();
      leafletRef.current = null;
    };
  }, [paso, sesion]); // eslint-disable-line react-hooks/exhaustive-deps

  // ── Mapa Paso C: dibujar rutas ──
  useEffect(() => {
    const map = leafletRef.current;
    if (!map || !sesion) return;

    capasRef.current.forEach(l => map.removeLayer(l));
    capasRef.current = [];

    // Depósito
    const depot = L.circleMarker([deposito.latitud, deposito.longitud], {
      radius: 12, fillColor: '#111827', color: 'white', weight: 2, fillOpacity: 1,
    }).bindPopup('Depósito Central').addTo(map);
    capasRef.current.push(depot);

    const rutasADibujar = sesion.rutas.filter(r =>
      selectedRutaId !== null ? r.repartidorId === selectedRutaId : visibleRutas.has(r.repartidorId)
    );

    rutasADibujar.forEach(ruta => {
      const color = ROUTE_COLORS[sesion.rutas.indexOf(ruta) % ROUTE_COLORS.length];
      const coords: L.LatLngTuple[] = ruta.puntos.map(p => [p.latitud, p.longitud]);

      const pl = L.polyline(coords, {
        color, weight: 4, opacity: 0.85,
        dashArray: ruta.fuenteDistancias === 'HAVERSINE_FALLBACK' ? '8, 8' : undefined,
      }).addTo(map);
      capasRef.current.push(pl);

      ruta.puntos.forEach(punto => {
        const m = L.circleMarker([punto.latitud, punto.longitud], {
          radius: 7, fillColor: color, color: 'white', weight: 2, fillOpacity: 1,
        }).bindPopup(
          `<div style="font-size:13px;min-width:140px">
            <strong>Parada #${punto.orden}</strong><br/>
            ${punto.etiqueta}<br/>
            <span style="color:#09b4db">ETA: ${punto.etaAcumuladoMinutos} min</span>
          </div>`
        ).addTo(map);
        capasRef.current.push(m);
      });

      if (selectedRutaId !== null && coords.length > 1) {
        map.fitBounds(L.latLngBounds(coords), { padding: [40, 40] });
      }
    });

    if (selectedRutaId === null && rutasADibujar.length > 0) {
      const all = sesion.rutas.flatMap(r => r.puntos.map(p => [p.latitud, p.longitud] as L.LatLngTuple));
      if (all.length > 0) map.fitBounds(L.latLngBounds(all), { padding: [40, 40] });
    }
  }, [sesion, selectedRutaId, visibleRutas]); // eslint-disable-line react-hooks/exhaustive-deps

  // ── Helpers ──
  function togglePedido(id: number) {
    setSelectedPedidos(prev => {
      const s = new Set(prev);
      s.has(id) ? s.delete(id) : s.add(id);
      return s;
    });
  }

  function toggleRep(id: number) {
    if (!asignaciones.has(id)) return;
    setSelectedReps(prev => {
      const s = new Set(prev);
      s.has(id) ? s.delete(id) : s.add(id);
      return s;
    });
  }

  async function handleGeocodificar(pedido: PedidoResponse) {
    setGeocodificando(prev => new Set([...prev, pedido.id]));
    try {
      const updated = await geocodificarPedido(pedido.id);
      setPedidos(prev => prev.map(p => p.id === pedido.id ? updated : p));
    } catch (err) {
      setErrorA(err instanceof Error ? err.message : 'Error al geocodificar pedido');
    } finally {
      setGeocodificando(prev => { const s = new Set(prev); s.delete(pedido.id); return s; });
    }
  }

  function validarPasoA(): string | null {
    if (selectedPedidos.size === 0) return 'Selecciona al menos un pedido.';
    if (selectedReps.size === 0) return 'Selecciona al menos un repartidor.';
    const sinGeo = Array.from(selectedPedidos).some(id => {
      const p = pedidos.find(x => x.id === id);
      return !p?.direccion.latitud || !p?.direccion.longitud;
    });
    if (sinGeo) return 'Hay pedidos seleccionados sin geocodificar.';
    return null;
  }

  async function handleCalcular() {
    const err = validarPasoA();
    if (err) { setErrorA(err); return; }
    setErrorA(null);
    setErrorB(null);
    setPaso(2);

    const pedidosPayload = Array.from(selectedPedidos).map(id => {
      const p = pedidos.find(x => x.id === id)!;
      return {
        id: String(p.id),
        etiqueta: `${p.destinatario.nombre} ${p.destinatario.apellido}`,
        latitud: p.direccion.latitud!,
        longitud: p.direccion.longitud!,
        direccion: p.direccion.direccionTexto,
        ciudad: p.direccion.ciudad,
      };
    });

    const repartidoresPayload = Array.from(selectedReps).map(id => {
      const r = repartidores.find(x => x.id === id)!;
      const asg = asignaciones.get(id)!;
      const veh = vehiculos.get(asg.idVehiculo);
      return {
        id: String(asg.idRepartidorVehiculo),
        nombre: `${r.nombre} ${r.apellido}`,
        capacidadPesoKg: veh?.capacidadPeso ?? 500,
        capacidadVolumenM3: veh?.capacidadVolumen ?? 5,
      };
    });

    try {
      const result = await crearSesionDespacho({
        deposito,
        pedidos: pedidosPayload,
        repartidores: repartidoresPayload,
        estrategia: 'GEOGRAFICA_BALANCEADA',
      });
      setSesion(result);
      setVisibleRutas(new Set(result.rutas.map(r => r.repartidorId)));
      setSelectedRutaId(null);
      setPaso(3);
    } catch (err) {
      setErrorB(err instanceof Error ? err.message : 'Error al calcular rutas');
      setPaso(1);
    }
  }

  async function handleDespachar() {
    if (!sesion) return;
    setDespachando(true);
    try {
      await despacharSesion(sesion.id);
      setShowModal(false);
      setExitoMsg(true);
      setTimeout(() => onNavigate('dashboard'), 2500);
    } catch (err) {
      setErrorB(err instanceof Error ? err.message : 'Error al despachar');
      setShowModal(false);
    } finally {
      setDespachando(false);
    }
  }

  async function handleCancelar() {
    if (!sesion) return;
    if (!confirm('¿Cancelar la sesión de despacho? Se perderán las rutas calculadas.')) return;
    setCancelando(true);
    try {
      await cancelarSesion(sesion.id);
    } catch { /* silent */ } finally {
      setCancelando(false);
      setSesion(null);
      setSelectedPedidos(new Set());
      setSelectedReps(new Set());
      setPaso(1);
    }
  }

  // ── Render helpers ──
  const validationMsg = validarPasoA();

  const sumKm   = sesion?.rutas.reduce((a, r) => a + r.distanciaTotal, 0) ?? 0;
  const sumCost = sesion?.rutas.reduce((a, r) => a + r.costoEstimado, 0) ?? 0;

  function semaforo(c: RutaPorRepartidorResponse['clasificacionCosto']) {
    if (c === 'EFICIENTE') return <span className="sd-semaforo sd-sem-verde" title="Eficiente" />;
    if (c === 'ACEPTABLE') return <span className="sd-semaforo sd-sem-amarillo" title="Aceptable" />;
    return <span className="sd-semaforo sd-sem-rojo" title="Alto" />;
  }

  // ════════════════════════════════════════
  return (
    <div className="sd-page">
      {/* Header */}
      <div className="sd-header">
        <div>
          <h2>Sesión de Despacho</h2>
          <p>Optimiza y despacha las rutas del día con el algoritmo NN+2opt</p>
        </div>
        <div className="sd-steps">
          {(['Configurar','Calculando','Resultados'] as const).map((label, i) => {
            const n = (i + 1) as 1 | 2 | 3;
            const cls = paso > n ? 'done' : paso === n ? 'active' : '';
            return (
              <span key={n} style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                {i > 0 && <span className={`sd-step-sep ${paso > n ? 'done' : ''}`} />}
                <span className={`sd-step ${cls}`}>
                  <span className="sd-step-dot">{paso > n ? '✓' : n}</span>
                  {label}
                </span>
              </span>
            );
          })}
        </div>
      </div>

      {/* ══ PASO A ══ */}
      {paso === 1 && (
        <>
          {loadingInicial ? (
            <p style={{ textAlign: 'center', color: '#6b7280', padding: '2rem' }}>Cargando datos…</p>
          ) : (
            <div className="sd-body-a">
              {/* Columna izquierda — Pedidos */}
              <div className="sd-col">
                <div className="sd-panel">
                  <h3>Pedidos del día <span>{selectedPedidos.size} seleccionados</span></h3>
                  {pedidos.length === 0 ? (
                    <p style={{ color: '#6b7280', fontSize: '0.85rem' }}>No hay pedidos pendientes.</p>
                  ) : (
                    <div className="sd-list">
                      {pedidos.map(p => {
                        const geo = !!(p.direccion.latitud && p.direccion.longitud);
                        const sel = selectedPedidos.has(p.id);
                        const inGeo = geocodificando.has(p.id);
                        return (
                          <div
                            key={p.id}
                            className={`sd-item ${sel ? 'selected' : ''}`}
                            onClick={() => togglePedido(p.id)}
                          >
                            <input type="checkbox" checked={sel} onChange={() => togglePedido(p.id)} onClick={e => e.stopPropagation()} />
                            <div className="sd-item-body">
                              <strong>{p.destinatario.nombre} {p.destinatario.apellido}</strong>
                              <small>{p.direccion.direccionTexto}, {p.direccion.ciudad}</small>
                              <div style={{ display: 'flex', alignItems: 'center', gap: 4, flexWrap: 'wrap', marginTop: 4 }}>
                                <span className={`sd-badge ${geo ? 'sd-badge-geo' : 'sd-badge-nogeo'}`}>
                                  {geo ? 'Geocodificado' : 'Sin geocodificar'}
                                </span>
                                {!geo && (
                                  <button
                                    className="sd-geo-btn"
                                    disabled={inGeo}
                                    onClick={e => { e.stopPropagation(); handleGeocodificar(p); }}
                                  >
                                    {inGeo ? 'Geocodificando…' : 'Geocodificar'}
                                  </button>
                                )}
                              </div>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              </div>

              {/* Columna derecha — Repartidores + Depósito */}
              <div className="sd-col">
                <div className="sd-panel">
                  <h3>Repartidores disponibles <span>{selectedReps.size} seleccionados</span></h3>
                  {repartidores.length === 0 ? (
                    <p style={{ color: '#6b7280', fontSize: '0.85rem' }}>No hay repartidores activos.</p>
                  ) : (
                    <div className="sd-list">
                      {repartidores.map(r => {
                        const asg = asignaciones.get(r.id);
                        const tieneVehiculo = !!asg;
                        const sel = selectedReps.has(r.id);
                        return (
                          <div
                            key={r.id}
                            className={`sd-item ${sel ? 'selected' : ''} ${!tieneVehiculo ? 'disabled' : ''}`}
                            onClick={() => toggleRep(r.id)}
                            title={!tieneVehiculo ? 'Sin vehículo asignado' : undefined}
                          >
                            <input
                              type="checkbox" checked={sel}
                              disabled={!tieneVehiculo}
                              onChange={() => toggleRep(r.id)}
                              onClick={e => e.stopPropagation()}
                            />
                            <div className="sd-item-body">
                              <strong>{r.nombre} {r.apellido}</strong>
                              <small>
                                {tieneVehiculo
                                  ? `Vehículo asignado (asig. #${asg.idRepartidorVehiculo})`
                                  : 'Sin vehículo asignado'}
                              </small>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>

                <div className="sd-panel">
                  <h3>Punto de partida (depósito)</h3>
                  <div className="sd-deposito-grid">
                    <div>
                      <label>Latitud</label>
                      <input
                        type="number" step="0.0001"
                        value={deposito.latitud}
                        onChange={e => setDeposito(d => ({ ...d, latitud: parseFloat(e.target.value) || d.latitud }))}
                      />
                    </div>
                    <div>
                      <label>Longitud</label>
                      <input
                        type="number" step="0.0001"
                        value={deposito.longitud}
                        onChange={e => setDeposito(d => ({ ...d, longitud: parseFloat(e.target.value) || d.longitud }))}
                      />
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}

          <div className="sd-footer-a">
            <div>
              {(errorA || errorB) && (
                <div className="sd-validacion">{errorA || errorB}</div>
              )}
              {!errorA && !errorB && validationMsg && selectedPedidos.size + selectedReps.size > 0 && (
                <div className="sd-validacion">{validationMsg}</div>
              )}
            </div>
            <button
              className="btn-primary"
              style={{ padding: '0.7rem 1.8rem', borderRadius: 8, border: 'none', background: '#09b4db', color: 'white', fontWeight: 700, fontSize: '0.95rem', cursor: 'pointer' }}
              disabled={loadingInicial || !!validationMsg}
              onClick={handleCalcular}
            >
              Calcular rutas óptimas
            </button>
          </div>
        </>
      )}

      {/* ══ PASO B ══ */}
      {paso === 2 && (
        <div className="sd-body-b">
          <div className="sd-spinner" />
          <h3>Calculando rutas óptimas para {selectedPedidos.size} pedidos y {selectedReps.size} repartidores…</h3>
          <p>El algoritmo NN+2opt está optimizando las rutas. Esto puede tomar algunos segundos.</p>
        </div>
      )}

      {/* ══ PASO C ══ */}
      {paso === 3 && sesion && (
        <div className="sd-body-c">
          {exitoMsg && (
            <div className="sd-success-banner">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                <polyline points="20 6 9 17 4 12" />
              </svg>
              Rutas despachadas correctamente. Los repartidores ya tienen sus rutas asignadas.
            </div>
          )}

          {errorB && (
            <div style={{ background: 'rgba(239,68,68,0.1)', border: '1px solid #ef4444', borderRadius: 8, padding: '0.75rem 1rem', color: '#ef4444' }}>
              {errorB}
            </div>
          )}

          {/* Resumen */}
          <div className="sd-resumen">
            <div className="sd-kpi">
              <span className="sd-kpi-label">Total pedidos</span>
              <span className="sd-kpi-value">{sesion.totalPedidos}</span>
            </div>
            <div className="sd-kpi">
              <span className="sd-kpi-label">Repartidores</span>
              <span className="sd-kpi-value">{sesion.totalRepartidores}</span>
            </div>
            <div className="sd-kpi">
              <span className="sd-kpi-label">Km totales</span>
              <span className="sd-kpi-value" style={{ color: '#09b4db' }}>{sesion.kmTotales.toFixed(1)} km</span>
            </div>
            <div className="sd-kpi">
              <span className="sd-kpi-label">Costo estimado</span>
              <span className="sd-kpi-value" style={{ color: '#10b981' }}>${sesion.costoTotalEstimado.toFixed(2)}</span>
            </div>
          </div>

          {/* Tabla */}
          <div className="sd-tabla-wrap">
            <table className="sd-tabla">
              <thead>
                <tr>
                  <th>Repartidor</th>
                  <th>Paradas</th>
                  <th>Km</th>
                  <th>Tiempo</th>
                  <th>Costo</th>
                  <th>Carga</th>
                  <th>Mejora</th>
                  <th>Fuente</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {sesion.rutas.map(r => (
                  <tr
                    key={r.repartidorId}
                    className={selectedRutaId === r.repartidorId ? 'selected-row' : ''}
                    onClick={() => setSelectedRutaId(prev => prev === r.repartidorId ? null : r.repartidorId)}
                  >
                    <td style={{ fontWeight: 600 }}>{r.repartidorNombre}</td>
                    <td>{r.numeroParadas}</td>
                    <td>{r.distanciaTotal.toFixed(1)}</td>
                    <td>{Math.floor(r.tiempoEstimadoMinutos / 60)}h {r.tiempoEstimadoMinutos % 60}m</td>
                    <td>${r.costoEstimado.toFixed(2)}</td>
                    <td>{r.cargaUtilizadaPct.toFixed(0)}%</td>
                    <td>{r.mejoraPorcentaje.toFixed(1)}%</td>
                    <td>
                      <span className={`sd-badge ${r.fuenteDistancias === 'OSRM_VIAL' ? 'sd-badge-osrm' : 'sd-badge-haversine'}`}>
                        {r.fuenteDistancias === 'OSRM_VIAL' ? 'OSRM' : 'Haversine'}
                      </span>
                    </td>
                    <td>{semaforo(r.clasificacionCosto)}</td>
                  </tr>
                ))}
                <tr className="sd-totales">
                  <td>TOTAL</td>
                  <td>{sesion.rutas.reduce((a, r) => a + r.numeroParadas, 0)}</td>
                  <td>{sumKm.toFixed(1)}</td>
                  <td>—</td>
                  <td>${sumCost.toFixed(2)}</td>
                  <td>—</td>
                  <td>—</td>
                  <td colSpan={2}>—</td>
                </tr>
              </tbody>
            </table>
          </div>

          {/* Mapa */}
          <div className="sd-mapa-wrap">
            <div className="sd-mapa-panel">
              <h4>Rutas en mapa</h4>
              <p style={{ fontSize: '0.76rem', color: '#6b7280', margin: '0 0 0.5rem' }}>
                Haz clic en la tabla para aislar una ruta.
              </p>
              {sesion.rutas.map((r, idx) => (
                <label key={r.repartidorId} className="sd-mapa-legend-item">
                  <input
                    type="checkbox"
                    checked={visibleRutas.has(r.repartidorId)}
                    onChange={() => {
                      setVisibleRutas(prev => {
                        const s = new Set(prev);
                        s.has(r.repartidorId) ? s.delete(r.repartidorId) : s.add(r.repartidorId);
                        return s;
                      });
                      setSelectedRutaId(null);
                    }}
                  />
                  <span className="sd-mapa-dot" style={{ background: ROUTE_COLORS[idx % ROUTE_COLORS.length] }} />
                  <span>{r.repartidorNombre}</span>
                  {r.fuenteDistancias === 'HAVERSINE_FALLBACK' && (
                    <span style={{ fontSize: '0.7rem', color: '#d97706' }}>~</span>
                  )}
                </label>
              ))}
              {selectedRutaId !== null && (
                <button
                  style={{ marginTop: '0.75rem', fontSize: '0.78rem', background: 'none', border: '1px solid #d1d5db', borderRadius: 6, padding: '4px 8px', cursor: 'pointer', color: '#374151' }}
                  onClick={() => setSelectedRutaId(null)}
                >
                  Ver todas
                </button>
              )}
            </div>
            <div ref={mapaRef} className="sd-mapa-container" />
          </div>

          {/* Botones */}
          <div className="sd-footer-c">
            <button className="btn-danger" disabled={cancelando} onClick={handleCancelar}>
              {cancelando ? 'Cancelando…' : 'Cancelar sesión'}
            </button>
            <button className="btn-success" disabled={despachando || exitoMsg} onClick={() => setShowModal(true)}>
              Aprobar y despachar
            </button>
          </div>
        </div>
      )}

      {/* ── Modal confirmación despacho ── */}
      {showModal && sesion && (
        <div className="sd-modal-overlay" onClick={() => setShowModal(false)}>
          <div className="sd-modal" onClick={e => e.stopPropagation()}>
            <h3>¿Confirmar despacho?</h3>
            <p>
              Vas a despachar <strong>{sesion.totalRepartidores} rutas</strong> para{' '}
              <strong>{sesion.totalRepartidores} repartidores</strong>.
              Esta acción no se puede deshacer.
            </p>
            <div className="sd-modal-actions">
              <button
                style={{ padding: '0.55rem 1.2rem', border: '1px solid #d1d5db', borderRadius: 8, background: 'white', cursor: 'pointer', fontWeight: 600 }}
                onClick={() => setShowModal(false)}
              >
                Cancelar
              </button>
              <button className="btn-success" disabled={despachando} onClick={handleDespachar}>
                {despachando ? 'Despachando…' : 'Confirmar despacho'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
