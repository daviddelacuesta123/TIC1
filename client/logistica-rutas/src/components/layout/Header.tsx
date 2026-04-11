export default function Header() {
  return (
    <header className="topbar">
      <div className="search-bar">
        <input type="text" placeholder="Search shipments, routes..." className="search-input" />
      </div>
      <div className="user-profile">
        <div className="avatar">A</div>
        <span>Admin User</span>
      </div>
    </header>
  )
}
