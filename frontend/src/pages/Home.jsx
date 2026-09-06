import "./Home.css";

function Home() {
  return (
    <main className="home-page">
      <section className="home-hero">
        <div className="home-content">
          <p className="home-eyebrow">WELCOME TO COMMERCEFLOW</p>

          <h1>CommerceFlow</h1>

          <p className="home-description">
            A modern e-commerce platform for discovering products,
            managing your cart, placing orders, and tracking payments.
          </p>

          <div className="home-actions">
            <a href="/products" className="home-primary-button">
              Browse Products
            </a>

            <a href="/orders" className="home-secondary-button">
              My Orders
            </a>
          </div>
        </div>
      </section>
    </main>
  );
}

export default Home;