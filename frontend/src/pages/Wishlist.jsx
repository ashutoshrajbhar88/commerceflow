import { Link } from "react-router-dom";
import ProductCard from "../components/ProductCard";
import { useWishlist } from "../context/WishlistContext";
import "./Wishlist.css";

function Wishlist() {
  const { items } = useWishlist();

  return (
    <main className="wishlist-page">
      <div className="wishlist-container">
        <p className="wishlist-eyebrow">SAVED</p>
        <h1>Your wishlist</h1>
        <p>Keep favourites here and add them to your bag when you are ready.</p>

        {items.length === 0 ? (
          <div className="empty-wishlist">
            <h2>Nothing saved yet</h2>
            <p>Tap the heart on any product to build your list.</p>
            <Link to="/products" className="home-primary-button">
              Browse products
            </Link>
          </div>
        ) : (
          <div className="wishlist-grid">
            {items.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}
      </div>
    </main>
  );
}

export default Wishlist;
