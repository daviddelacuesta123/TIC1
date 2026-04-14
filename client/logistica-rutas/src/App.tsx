import { useState } from 'react'
import Sidebar from './components/layout/Sidebar'
import Header from './components/layout/Header'
import Dashboard from './pages/Dashboard'
import Shipments from './pages/Shipments'
import RouteMap from './pages/RouteMap'
import Login from './pages/Login'
import Register from './pages/Register'
import NewRoute from './pages/NewRoute'
import Vehicles from './pages/Vehicles'
import './App.css'

export type Page = 'dashboard' | 'shipments' | 'routes' | 'new-route' | 'vehicles'
type AuthView = 'login' | 'register'

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)
  const [authView, setAuthView] = useState<AuthView>('login')
  const [currentPage, setCurrentPage] = useState<Page>('dashboard')

  if (!isAuthenticated) {
    if (authView === 'register') {
      return (
        <Register
          onRegister={() => {
            setIsAuthenticated(true)
            setAuthView('login')
          }}
          onGoToLogin={() => setAuthView('login')}
        />
      )
    }
    return (
      <Login
        onLogin={() => setIsAuthenticated(true)}
        onGoToRegister={() => setAuthView('register')}
      />
    )
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
          {currentPage === 'vehicles' && <Vehicles />}
        </div>
      </main>
    </div>
  )
}

export default App
