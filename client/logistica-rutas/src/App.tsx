import { useState } from 'react'
import { AuthProvider, useAuth } from './context/AuthContext'
import Sidebar from './components/layout/Sidebar'
import Header from './components/layout/Header'
import Dashboard from './pages/Dashboard'
import Shipments from './pages/Shipments'
import RouteMap from './pages/RouteMap'
import Login from './pages/Login'
import NewRoute from './pages/NewRoute'
import './App.css'

export type Page = 'dashboard' | 'shipments' | 'routes' | 'new-route'

function AppContent() {
  const { user } = useAuth()
  const [currentPage, setCurrentPage] = useState<Page>('dashboard')

  if (!user) {
    return <Login />
  }

  return (
    <div className="app-container">
      <Sidebar currentPage={currentPage} onNavigate={setCurrentPage} />
      <main className="main-content">
        <Header />
        <div className="page-content">
          {currentPage === 'dashboard' && <Dashboard />}
          {currentPage === 'shipments' && <Shipments />}
          {currentPage === 'routes' && <RouteMap onNavigate={setCurrentPage} />}
          {currentPage === 'new-route' && <NewRoute onNavigate={setCurrentPage} />}
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
