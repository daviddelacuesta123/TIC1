import { useState, useEffect } from 'react'
import { AuthProvider, useAuth } from './context/AuthContext'
import Sidebar from './components/layout/Sidebar'
import Header from './components/layout/Header'
import RepartidorLayout from './components/layout/RepartidorLayout'
import Dashboard from './pages/Dashboard'
import Shipments from './pages/Shipments'
import RouteMap from './pages/RouteMap'
import Login from './pages/Login'
import NewRoute from './pages/NewRoute'
import SesionDespacho from './pages/SesionDespacho'
import Vehicles from './pages/Vehicles'
import NewVehicle from './pages/NewVehicle'
import NewRepartidor from './pages/NewRepartidor'
import Repartidores from './pages/Repartidores'
import NewShipment from './pages/NewShipment'
import Productos from './pages/Productos'
import NewProducto from './pages/NewProducto'
import './App.css'

export type Page =
  | 'dashboard'
  | 'shipments'
  | 'routes'
  | 'new-route'
  | 'sesion-despacho'
  | 'vehicles'
  | 'new-vehicle'
  | 'repartidores'
  | 'new-repartidor'
  | 'new-shipment'
  | 'productos'
  | 'new-producto'
  | 'repartidor-pedidos'
  | 'repartidor-mapa'

function AppContent() {
  const { user, logout } = useAuth()
  const [currentPage, setCurrentPage] = useState<Page>('dashboard')

  useEffect(() => {
    const manejarLogout = () => logout()
    window.addEventListener('auth:logout', manejarLogout)
    return () => window.removeEventListener('auth:logout', manejarLogout)
  }, [logout])

  if (!user) return <Login />

  if (user.rol === 'REPARTIDOR') {
    return <RepartidorLayout currentPage={currentPage} onNavigate={setCurrentPage} />
  }

  return (
    <div className="app-container">
      <Sidebar currentPage={currentPage} onNavigate={setCurrentPage} />
      <main className="main-content">
        <Header />
        <div className="page-content">
          {currentPage === 'dashboard'        && <Dashboard />}
          {currentPage === 'shipments'        && <Shipments onNavigate={p => setCurrentPage(p)} />}
          {currentPage === 'routes'           && <RouteMap onNavigate={setCurrentPage} />}
          {currentPage === 'new-route'        && <NewRoute onNavigate={setCurrentPage} />}
          {currentPage === 'sesion-despacho'  && <SesionDespacho onNavigate={setCurrentPage} />}
          {currentPage === 'vehicles'         && <Vehicles onNavigate={p => setCurrentPage(p)} />}
          {currentPage === 'new-vehicle'      && <NewVehicle onNavigate={p => setCurrentPage(p)} />}
          {currentPage === 'repartidores'     && <Repartidores onNavigate={p => setCurrentPage(p)} />}
          {currentPage === 'new-repartidor'   && <NewRepartidor onNavigate={p => setCurrentPage(p)} />}
          {currentPage === 'new-shipment'     && <NewShipment onNavigate={p => setCurrentPage(p)} />}
          {currentPage === 'productos'        && <Productos onNavigate={p => setCurrentPage(p)} />}
          {currentPage === 'new-producto'     && <NewProducto onNavigate={p => setCurrentPage(p)} />}
        </div>
      </main>
    </div>
  )
}

function App() {
  return <AuthProvider><AppContent /></AuthProvider>
}

export default App
