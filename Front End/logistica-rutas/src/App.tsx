import { useState } from 'react'
import Sidebar from './components/layout/Sidebar'
import Header from './components/layout/Header'
import Dashboard from './pages/Dashboard'
import Shipments from './pages/Shipments'
import RouteMap from './pages/RouteMap'
import Login from './pages/Login'
import './App.css'

export type Page = 'dashboard' | 'shipments' | 'routes'

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [currentPage, setCurrentPage] = useState<Page>('dashboard')

  if (!isAuthenticated) {
    return <Login onLogin={() => setIsAuthenticated(true)} />
  }

  return (
    <div className="app-container">
      <Sidebar currentPage={currentPage} onNavigate={setCurrentPage} />
      <main className="main-content">
        <Header />
        <div className="page-content">
          {currentPage === 'dashboard' && <Dashboard />}
          {currentPage === 'shipments' && <Shipments />}
          {currentPage === 'routes' && <RouteMap />}
        </div>
      </main>
    </div>
  )
}

export default App
