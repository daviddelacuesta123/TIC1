import { useState, type FormEvent } from 'react';
import { useAuth } from '../context/AuthContext';
import { loginApi } from '../services/authService';
import './Login.css';

export default function Login() {
  const { login } = useAuth();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const { token } = await loginApi(username, password);
      login(token);
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Error al iniciar sesión');
    } finally {
      setLoading(false);
    }
  };

  const handleNoPassword = () => {
    const payload = {
      sub: username || 'usuario_sin_clave',
      rol: 'ADMIN',
      exp: Math.floor(Date.now() / 1000) + 60 * 60 * 24 * 365,
    };
    const token = `${btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }))}.${btoa(JSON.stringify(payload))}.fake-signature`;
    login(token);
  };

  const handleRepartidor = () => {
    const payload = {
      sub: username || 'repartidor_demo',
      rol: 'REPARTIDOR',
      exp: Math.floor(Date.now() / 1000) + 60 * 60 * 24 * 365,
    };
    const token = `${btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }))}.${btoa(JSON.stringify(payload))}.fake-signature`;
    login(token);
  };


  return (
    <div className="login-container">
      {/* Left Panel */}
      <div className="login-left-panel">
        <div className="left-panel-content">
          <div className="brand">
            <svg
              className="brand-icon"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
              strokeLinecap="round"
              strokeLinejoin="round"
            >
              <rect width="16" height="16" x="4" y="4" rx="2" />
              <rect width="7" height="7" x="8" y="8" rx="1" />
              <path d="M4 12V8" />
              <path d="M20 12V8" />
            </svg>
            <span className="brand-name">RouteOptimizer</span>
          </div>

          <div className="hero-text">
            <h1>Optimiza tus rutas de entrega</h1>
            <p>
              Sistema inteligente de planificación y gestión de rutas para logística urbana.
              Reduce costos, optimiza tiempos y mejora la eficiencia de tus operaciones.
            </p>
          </div>

          <div className="features-footer">
            <span className="feature">
              <svg
                className="feature-icon"
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0Z" />
                <circle cx="12" cy="10" r="3" />
              </svg>
              Rutas optimizadas
            </span>
            <span className="feature">
              <svg
                className="feature-icon"
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <path d="M14 18V6a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2v11a1 1 0 0 0 1 1h2" />
                <path d="M15 18H9" />
                <path d="M19 18h2a1 1 0 0 0 1-1v-3.65a1 1 0 0 0-.22-.624l-3.48-4.35A1 1 0 0 0 17.52 8H14" />
                <circle cx="17" cy="18" r="2" />
                <circle cx="7" cy="18" r="2" />
              </svg>
              Seguimiento en tiempo real
            </span>
          </div>
        </div>
      </div>

      {/* Right Panel */}
      <div className="login-right-panel">
        <div className="login-form-wrapper">
          <div className="form-header">
            <h2>Bienvenido</h2>
            <p>Ingresa tus credenciales para continuar</p>
          </div>

          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="username">Usuario</label>
              <input
                type="text"
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="tu_usuario"
                required
                autoComplete="username"
              />
            </div>

            <div className="form-group">
              <label htmlFor="password">Contraseña</label>
              <input
                type="password"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                required
                autoComplete="current-password"
              />
            </div>

            {error && (
              <p className="login-error">{error}</p>
            )}

            <button type="submit" className="login-button" disabled={loading}>
              {loading ? 'Iniciando sesión...' : 'Iniciar sesión'}
            </button>
            <button
              type="button"
              className="login-button login-button-secondary"
              onClick={handleNoPassword}
            >
              Entrar como administrador
            </button>
            <button
              type="button"
              className="login-button login-button-secondary"
              onClick={handleRepartidor}
            >
              Entrar como repartidor
            </button>
          </form>
        </div>

        <button className="help-icon" aria-label="Ayuda">?</button>
      </div>
    </div>
  );
}
