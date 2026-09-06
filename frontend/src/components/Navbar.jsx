import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Navbar.css";

function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();

  if (!isAuthenticated) {
    return (
      <nav className="navbar">
        <div className="navbar-brand">
          <Link to="/login">CommerceFlow</Link>
        </div>
      </nav>
    );
  }

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <Link to="/home">CommerceFlow</Link>
      </div>

      <div className="navbar-links">
        <Link to="/home">Home</Link>
        <Link to="/products">Products</Link>
        <Link to="/cart">Cart</Link>
        <Link to="/orders">My Orders</Link>

        {user?.role === "ADMIN" && (
          <>
            <Link to="/admin/dashboard">Dashboard</Link>
            <Link to="/admin/products">Manage Products</Link>
            <Link to="/admin/orders">Manage Orders</Link>
            <Link to="/admin/payments">Payments</Link>
          </>
        )}

        <span className="navbar-user">
          {user?.email}
        </span>

        <button onClick={logout}>
          Logout
        </button>
      </div>
    </nav>
  );
}

export default Navbar;