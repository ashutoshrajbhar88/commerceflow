import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getProducts } from "../services/productService";
import "./Products.css";

function Products() {
  const [products, setProducts] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    getProducts()
      .then((data) => {
        setProducts(data.content || []);
      })
      .catch((error) => {
        console.error("Failed to load products:", error);

        const status = error.response?.status;
        const message = error.response?.data?.message;

        setError(
          message
            ? `${status}: ${message}`
            : `Request failed: ${status || "No response"}`
        );
      });
  }, []);

  return (
    <main className="products-page">
      <div className="products-container">
        <div className="products-header">
          <div>
            <p className="products-eyebrow">OUR STORE</p>
            <h1>Products</h1>
            <p className="products-subtitle">
              Explore our collection and find the right product for you.
            </p>
          </div>

          {!error && products.length > 0 && (
            <span className="product-count">
              {products.length} products
            </span>
          )}
        </div>

        {error && (
          <div className="products-error">
            <strong>Unable to load products</strong>
            <p>{error}</p>
          </div>
        )}

        {!error && products.length === 0 && (
          <div className="products-loading">
            <p>Loading products...</p>
          </div>
        )}

        {!error && products.length > 0 && (
          <div className="products-grid">
            {products.map((product) => (
              <article className="product-card" key={product.id}>
                <div className="product-image-container">
                  {product.images?.length > 0 ? (
                    <img
                      className="product-image"
                      src={`http://localhost:8080${product.images[0].imageUrl}`}
                      alt={product.name}
                    />
                  ) : (
                    <div className="product-image-placeholder">
                      No Image
                    </div>
                  )}
                </div>

                <div className="product-card-content">
                  <p className="product-category">
                    {product.categoryName || "Product"}
                  </p>

                  <Link
                    className="product-name"
                    to={`/products/${product.id}`}
                  >
                    {product.name}
                  </Link>

                  <p className="product-description">
                    {product.description}
                  </p>

                  <div className="product-card-footer">
                    <div>
                      <p className="product-price">
                        ₹{product.price}
                      </p>

                      <p
                        className={
                          product.stockQuantity > 0
                            ? "product-stock in-stock"
                            : "product-stock out-of-stock"
                        }
                      >
                        {product.stockQuantity > 0
                          ? `${product.stockQuantity} in stock`
                          : "Out of stock"}
                      </p>
                    </div>

                    <Link
                      className="view-product-button"
                      to={`/products/${product.id}`}
                    >
                      View
                    </Link>
                  </div>
                </div>
              </article>
            ))}
          </div>
        )}
      </div>
    </main>
  );
}

export default Products;