import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getProducts } from "../services/productService";
import { getCategories } from "../services/categoryService";
import { getRecentlyViewed } from "../utils/recentlyViewed";
import ProductCard from "../components/ProductCard";
import "./Home.css";

function Home() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [recent, setRecent] = useState([]);

  useEffect(() => {
    getProducts({ size: 8, sortBy: "createdAt", direction: "desc" })
      .then((data) => setProducts(data.content || []))
      .catch((error) => console.error("Failed to load products:", error));

    getCategories()
      .then((data) => setCategories(data || []))
      .catch((error) => console.error("Failed to load categories:", error));

    setRecent(getRecentlyViewed());
  }, []);

  return (
    <main className="home-page">
      <section className="home-hero">
        <div className="home-hero-copy">
          <p className="home-eyebrow">NEW SEASON · 2026</p>
          <h1>Shop the collection that actually feels like a store.</h1>
          <p className="home-description">
            Search, filter, save favourites, add to bag, checkout, pay, and
            track orders — a complete shopping flow built for real use.
          </p>
          <div className="home-actions">
            <Link to="/products" className="home-primary-button">
              Start shopping
            </Link>
            <Link to="/orders" className="home-secondary-button">
              Track an order
            </Link>
          </div>
          <div className="trust-row">
            <span>Secure payments</span>
            <span>Live stock</span>
            <span>Order tracking</span>
          </div>
        </div>
        <div className="home-hero-art" role="img" aria-label="Lifestyle shopping">
          <div className="hero-stamp">CF</div>
          <div className="hero-caption">CURATED DROPS · READY TO SHIP</div>
        </div>
      </section>

      <section className="home-section">
        <div className="section-head">
          <div>
            <p className="home-eyebrow">BROWSE BY CATEGORY</p>
            <h2>Find it faster</h2>
          </div>
          <Link to="/products">View all →</Link>
        </div>
        <div className="category-row">
          {categories.length === 0 ? (
            <p>Categories will appear here once the catalog is ready.</p>
          ) : (
            categories.map((category) => (
              <Link
                key={category.id}
                className="category-chip"
                to={`/products?category=${category.id}`}
              >
                {category.name}
              </Link>
            ))
          )}
        </div>
      </section>

      <section className="home-section">
        <div className="section-head">
          <div>
            <p className="home-eyebrow">JUST DROPPED</p>
            <h2>Fresh from the catalog</h2>
          </div>
          <Link to="/products">Shop all →</Link>
        </div>
        <div className="home-grid">
          {products.map((product) => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      </section>

      {recent.length > 0 && (
        <section className="home-section">
          <div className="section-head">
            <div>
              <p className="home-eyebrow">CONTINUE BROWSING</p>
              <h2>Recently viewed</h2>
            </div>
          </div>
          <div className="home-grid">
            {recent.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        </section>
      )}

      <section className="home-benefits">
        <article>
          <strong>Easy returns</strong>
          <p>Change of mind? Manage orders from your account.</p>
        </article>
        <article>
          <strong>Customer reviews</strong>
          <p>Read ratings before you buy, then share your own.</p>
        </article>
        <article>
          <strong>Wishlist</strong>
          <p>Save pieces now and come back when you are ready.</p>
        </article>
      </section>
    </main>
  );
}

export default Home;
