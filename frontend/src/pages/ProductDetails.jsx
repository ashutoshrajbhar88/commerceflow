import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getProductById } from "../services/productService";
import { addToCart } from "../services/cartService";
import "./ProductDetails.css";

function ProductDetails() {
  const { id } = useParams();

  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [error, setError] = useState("");
  const [cartMessage, setCartMessage] = useState("");
  const [cartError, setCartError] = useState("");

  useEffect(() => {
    getProductById(id)
      .then((data) => {
        setProduct(data);
      })
      .catch((error) => {
        console.error("Failed to load product:", error);

        const status = error.response?.status;
        const message = error.response?.data?.message;

        if (message) {
          setError(status + ": " + message);
        } else {
          setError("Request failed: " + (status || "No response"));
        }
      });
  }, [id]);

  const handleAddToCart = async () => {
    setCartMessage("");
    setCartError("");

    if (
      quantity < 1 ||
      quantity > product.stockQuantity
    ) {
      setCartError(
        `Quantity must be between 1 and ${product.stockQuantity}.`
      );
      return;
    }

    try {
      await addToCart(product.id, quantity);
      setCartMessage("Product added to cart.");
    } catch (error) {
      console.error("Failed to add product to cart:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setCartError(status + ": " + message);
      } else {
        setCartError(
          "Request failed: " + (status || "No response")
        );
      }
    }
  };

  if (error) {
    return (
      <main className="product-details-page">
        <div className="product-details-container">
          <div className="product-details-error">
            <h1>Product Details</h1>
            <p>{error}</p>
            <Link to="/products">Back to Products</Link>
          </div>
        </div>
      </main>
    );
  }

  if (!product) {
    return (
      <main className="product-details-page">
        <div className="product-details-container">
          <p>Loading product...</p>
        </div>
      </main>
    );
  }

  return (
    <main className="product-details-page">
      <div className="product-details-container">
        <Link
          className="back-to-products"
          to="/products"
        >
          ← Back to Products
        </Link>

        <div className="product-details-card">
          <div className="product-gallery">
            {product.images?.length > 0 ? (
              <div className="product-images">
                {product.images.map((image) => (
                  <div
                    className="product-detail-image-wrapper"
                    key={image.id}
                  >
                    <img
                      className="product-detail-image"
                      src={`http://localhost:8080${image.imageUrl}`}
                      alt={product.name}
                    />
                  </div>
                ))}
              </div>
            ) : (
              <div className="product-detail-placeholder">
                No Image Available
              </div>
            )}
          </div>

          <div className="product-information">
            <p className="product-detail-category">
              {product.categoryName || "Product"}
            </p>

            <h1>{product.name}</h1>

            <p className="product-detail-description">
              {product.description}
            </p>

            <div className="product-detail-price">
              ₹{product.price}
            </div>

            <div
              className={
                product.stockQuantity > 0
                  ? "product-detail-stock in-stock"
                  : "product-detail-stock out-of-stock"
              }
            >
              {product.stockQuantity > 0
                ? `${product.stockQuantity} units available`
                : "Out of stock"}
            </div>

            {product.stockQuantity > 0 && (
              <div className="purchase-section">
                <label htmlFor="quantity">
                  Quantity
                </label>

                <div className="purchase-controls">
                  <input
                    id="quantity"
                    type="number"
                    min="1"
                    max={product.stockQuantity}
                    value={quantity}
                    onChange={(event) => {
                      setQuantity(Number(event.target.value));
                    }}
                  />

                  <button onClick={handleAddToCart}>
                    Add to Cart
                  </button>
                </div>
              </div>
            )}

            {product.stockQuantity === 0 && (
              <div className="out-of-stock-message">
                This product is currently unavailable.
              </div>
            )}

            {cartMessage && (
              <div className="cart-success-message">
                {cartMessage}
              </div>
            )}

            {cartError && (
              <div className="cart-error-message">
                {cartError}
              </div>
            )}
          </div>
        </div>
      </div>
    </main>
  );
}

export default ProductDetails;