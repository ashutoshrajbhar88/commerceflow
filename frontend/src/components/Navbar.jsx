import { useState } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import { useWishlist } from "../context/WishlistContext";
import "./Navbar.css";

function Navbar() {
  const { user, isAuthenticated, logout } = useAuth();
  const { itemCount } = useCart();
  const { items } = useWishlist();
  const navigate = useNavigate();
  const [query, setQuery] = useState("");
  const [open, setOpen] = useState(false);

  const handleSearch = (event) => {
    event.preventDefault();
    const keyword = query.trim();
    navigate(keyword ? `/products?q=${encodeURIComponent(keyword)}` : "/products");
    setOpen(false);
  };

  const close = () => setOpen(false);

  return (
    <header className="site-header">
      <div className="promo-bar">
        Free shipping on orders above ₹999 · Easy returns · Secure checkout
      </div>

      <nav className="navbar">
        <Link to="/home" className="navbar-brand" onClick={close}>
          <span className="brand-mark">CF</span>
          CommerceFlow
        </Link>

        <form className="nav-search" onSubmit={handleSearch}>
          <input
            type="search"
            placeholder="Search for products, brands and more"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
          />
          <button type="submit">Search</button>
        </form>

        <button
          type="button"
          className="nav-toggle"
          onClick={() => setOpen((value) => !value)}
          aria-label="Toggle menu"
        >
          Menu
        </button>

        <div className={`navbar-links ${open ? "is-open" : ""}`}>
          <NavLink to="/home" end onClick={close}>
            Home
          </NavLink>
          <NavLink to="/products" onClick={close}>
            Shop
          </NavLink>
          <NavLink to="/wishlist" onClick={close}>
            Wishlist <span className="nav-count">{items.length}</span>
          </NavLink>

          {isAuthenticated ? (
            <>
              {user?.role !== "ADMIN" && (
                <>
                  <NavLink to="/cart" onClick={close}>
                    Bag <span className="nav-count">{itemCount}</span>
                  </NavLink>
                  <NavLink to="/orders" onClick={close}>
                    Orders
                  </NavLink>
                </>
              )}

              {user?.role === "ADMIN" && (
                <>
                  <NavLink to="/admin/dashboard" onClick={close}>
                    Dashboard
                  </NavLink>
                  <NavLink to="/admin/products" onClick={close}>
                    Catalog
                  </NavLink>
                  <NavLink to="/admin/orders" onClick={close}>
                    Orders
                  </NavLink>
                  <NavLink to="/admin/payments" onClick={close}>
                    Payments
                  </NavLink>
                </>
              )}

              <span className="navbar-user">{user?.name || user?.email}</span>
              <button className="navbar-logout" onClick={() => { logout(); close(); }}>
                Sign out
              </button>
            </>
          ) : (
            <>
              <NavLink to="/login" onClick={close}>
                Sign in
              </NavLink>
              <Link to="/register" className="nav-cta" onClick={close}>
                Join free
              </Link>
            </>
          )}
        </div>
      </nav>
    </header>
  );
}

export default Navbar;
