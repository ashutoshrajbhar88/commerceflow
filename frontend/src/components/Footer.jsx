import { Link } from "react-router-dom";
import "./Footer.css";

function Footer() {
  return (
    <footer className="site-footer">
      <div className="footer-grid">
        <div>
          <h3>CommerceFlow</h3>
          <p>
            A full-stack shopping experience with catalog, cart, checkout,
            orders, payments, and admin tools.
          </p>
        </div>

        <div>
          <h4>Shop</h4>
          <Link to="/home">Home</Link>
          <Link to="/products">All products</Link>
          <Link to="/wishlist">Wishlist</Link>
          <Link to="/cart">Bag</Link>
        </div>

        <div>
          <h4>Help</h4>
          <Link to="/orders">Track order</Link>
          <Link to="/login">Sign in</Link>
          <Link to="/register">Create account</Link>
        </div>

        <div>
          <h4>Why shop here</h4>
          <p>Fast checkout, live stock, secure payments, and order tracking.</p>
        </div>
      </div>

      <div className="footer-bottom">
        <span>© {new Date().getFullYear()} CommerceFlow</span>
        <span>Built as a complete e-commerce platform</span>
      </div>
    </footer>
  );
}

export default Footer;
