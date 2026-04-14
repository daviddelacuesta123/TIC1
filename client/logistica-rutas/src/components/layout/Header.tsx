import { useAuth } from '../../context/AuthContext';

export default function Header() {
  const { user, logout } = useAuth();
  const initial = user?.username?.[0]?.toUpperCase() ?? '?';

  return (
    <header className="topbar">
      <div className="search-bar">
        <input type="text" placeholder="Search shipments, routes..." className="search-input" />
      </div>
      <div className="user-profile">
        <div className="avatar">{initial}</div>
        <span>{user?.username ?? ''}</span>
        <button
          onClick={logout}
          style={{ marginLeft: '12px', cursor: 'pointer', background: 'none', border: 'none', color: 'inherit', fontSize: '0.85rem', opacity: 0.7 }}
          title="Cerrar sesión"
        >
          Salir
        </button>
      </div>
    </header>
  );
}
