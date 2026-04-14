import { useState } from 'react';
import './Login.css';
import './Register.css';

interface RegisterProps {
  onRegister: () => void;
  onGoToLogin: () => void;
}

export default function Register({ onRegister, onGoToLogin }: RegisterProps) {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (name && email && password) {
      onRegister();
    } else {
      alert('Por favor completa todos los campos para registrarte.');
    }
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
            <h1>Únete a RouteOptimizer</h1>
            <p>
              Crea tu cuenta hoy y comienza a disfrutar de las ventajas de nuestro
              sistema inteligente de planificación y gestión de rutas.
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
            <h2>Crear Cuenta</h2>
            <p>Ingresa tus datos para registrarte en la plataforma</p>
          </div>

          <form onSubmit={handleSubmit} className="login-form">
            <div className="form-group">
              <label htmlFor="name">Nombre completo</label>
              <input
                type="text"
                id="name"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Tu nombre completo"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="email">Correo electrónico</label>
              <input
                type="email"
                id="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="tu@empresa.com"
                required
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
              />
            </div>

            <button type="submit" className="login-button">
              Registrarse
            </button>
          </form>

          <div className="register-link">
            ¿Ya tienes una cuenta?{' '}
            <a
              href="#"
              onClick={(e) => {
                e.preventDefault();
                onGoToLogin();
              }}
            >
              Inicia sesión aquí
            </a>
          </div>
        </div>

        <button className="help-icon" aria-label="Ayuda">?</button>
      </div>
    </div>
  );
}
