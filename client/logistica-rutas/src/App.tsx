import { useState } from 'react'
import { AuthProvider, useAuth } from './context/AuthContext'
import Sidebar from './components/layout/Sidebar'
import Header from './components/layout/Header'
import Dashboard from './pages/Dashboard'
import Shipments from './pages/Shipments'
import RouteMap from './pages/RouteMap'
import Login from './pages/Login'
import NewRoute from './pages/NewRoute'
import Vehicles from './pages/Vehicles'
import NewVehicle from './pages/NewVehicle'
import NewRepartidor from './pages/NewRepartidor'
import Repartidores from './pages/Repartidores'
import NewShipment from './pages/NewShipment'
import RepartidorDashboard from './pages/RepartidorDashboard'
import RepartidorPedidos from './pages/RepartidorPedidos'
import './App.css'


export type Page =
  | 'dashboard'
  | 'shipments'
  | 'routes'
  | 'new-route'
  | 'vehicles'
  | 'new-vehicle'
  | 'repartidores'
  | 'new-repartidor'
  | 'new-shipment'
  | 'repartidor-pedidos'


function AppContent() {
  const { user } = useAuth()
  const [currentPage, setCurrentPage] = useState<Page>('dashboard')

  if (!user) {
    return <Login />
  }

  const isRepartidor = user.rol === 'REPARTIDOR'

  return (
    <div className="app-container">
      <Sidebar currentPage={currentPage} onNavigate={setCurrentPage} />
      <main className="main-content">
        <Header />
        <div className="page-content">
          {currentPage === 'dashboard' && (isRepartidor ? <RepartidorDashboard onNavigate={setCurrentPage} /> : <Dashboard />)}
          {currentPage === 'repartidor-pedidos' && isRepartidor && <RepartidorPedidos />}
          {!isRepartidor && currentPage === 'shipments' && <Shipments onNavigate={(page) => setCurrentPage(page)} />}
          {!isRepartidor && currentPage === 'routes' && <RouteMap onNavigate={setCurrentPage} />}
          {!isRepartidor && currentPage === 'new-route' && <NewRoute onNavigate={setCurrentPage} />}
          {!isRepartidor && currentPage === 'vehicles' && <Vehicles onNavigate={(page) => setCurrentPage(page)} />}
          {!isRepartidor && currentPage === 'new-vehicle' && <NewVehicle onNavigate={(page) => setCurrentPage(page)} />}
          {!isRepartidor && currentPage === 'repartidores' && <Repartidores onNavigate={(page) => setCurrentPage(page)} />}
          {!isRepartidor && currentPage === 'new-repartidor' && <NewRepartidor onNavigate={(page) => setCurrentPage(page)} />}
          {!isRepartidor && currentPage === 'new-shipment' && <NewShipment onNavigate={(page) => setCurrentPage(page)} />}
        </div>
      </main>
    </div>
  )
}

function App() {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  )
}

export default App
