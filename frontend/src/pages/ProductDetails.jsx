import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { getProductById } from "../services/productService";
import { addToCart } from "../services/cartService";
import { createReview, getProductReviews } from "../services/reviewService";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import { useWishlist } from "../context/WishlistContext";
import { useToast } from "../context/ToastContext";
import { addRecentlyViewed } from "../utils/recentlyViewed";
import { formatPrice, productImageUrl } from "../utils/format";
import "./ProductDetails.css";

function ProductDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();
  const { refreshCart } = useCart();
  const { isWishlisted, toggleWishlist } = useWishlist();
  const { toast } = useToast();

  const [product, setProduct] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [activeImage, setActiveImage] = useState(0);
  const [quantity, setQuantity] = useState(1);
  const [error, setError] = useState("");
  const [rating, setRating] = useState(5);
  const [comment, setComment] = useState("");
  const [reviewError, setReviewError] = useState("");

  useEffect(() => {
    getProductById(id)
      .then((data) => {
        setProduct(data);
        addRecentlyViewed(data);
      })
      .catch((requestError) => {
        const status = requestError.response?.status;
        const message = requestError.response?.data?.message;
        setError(message ? `${status}: ${message}` : `Request failed: ${status || "No response"}`);
      });

    getProductReviews(id)
      .then((data) => setReviews(data || []))
      .catch(() => setReviews([]));
  }, [id]);

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      navigate("/login", { state: { from: `/products/${id}` } });
      return;
    }

    if (user?.role === "ADMIN") {
      toast("Use a customer account to shop", "error");
      return;
    }

    try {
      await addToCart(product.id, quantity);
      await refreshCart();
      toast("Added to bag", "success");
    } catch (requestError) {
      toast(requestError.response?.data?.message || "Could not add to bag", "error");
    }
  };

  const handleReview = async (event) => {
    event.preventDefault();
    setReviewError("");

    if (!isAuthenticated) {
      navigate("/login", { state: { from: `/products/${id}` } });
      return;
    }

    try {
      const created = await createReview(id, { rating: Number(rating), comment });
      setReviews((current) => [created, ...current]);
      setComment("");
      toast("Thanks for your review", "success");
    } catch (requestError) {
      setReviewError(requestError.response?.data?.message || "Could not submit review");
    }
  };

  if (error) {
    return (
      <main className="product-details-page">
        <div className="product-details-container">
          <div className="product-details-error">
            <h1>Product not found</h1>
            <p>{error}</p>
            <Link to="/products">Back to shop</Link>
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

  const images = product.images || [];
  const currentImage = images[activeImage]?.imageUrl;
  const average =
    reviews.length > 0
      ? (reviews.reduce((sum, item) => sum + (item.rating || 0), 0) / reviews.length).toFixed(1)
      : null;

  return (
    <main className="product-details-page">
      <div className="product-details-container">
        <Link className="back-to-products" to="/products">
          ← Back to shop
        </Link>

        <div className="product-details-card">
          <div className="product-gallery">
            <div className="product-hero-image">
              {currentImage ? (
                <img src={productImageUrl(currentImage)} alt={product.name} />
              ) : (
                <div className="product-detail-placeholder">No image available</div>
              )}
            </div>
            {images.length > 1 && (
              <div className="product-thumbs">
                {images.map((image, index) => (
                  <button
                    type="button"
                    key={image.id || index}
                    className={index === activeImage ? "is-active" : ""}
                    onClick={() => setActiveImage(index)}
                  >
                    <img src={productImageUrl(image.imageUrl)} alt="" />
                  </button>
                ))}
              </div>
            )}
          </div>

          <div className="product-information">
            <p className="product-detail-category">{product.categoryName || "Collection"}</p>
            <h1>{product.name}</h1>
            {average && (
              <p className="product-rating">
                ★ {average} · {reviews.length} review{reviews.length === 1 ? "" : "s"}
              </p>
            )}
            <p className="product-detail-description">{product.description}</p>
            <div className="product-detail-price">{formatPrice(product.price)}</div>
            <div className={product.stockQuantity > 0 ? "product-detail-stock in-stock" : "product-detail-stock out-of-stock"}>
              {product.stockQuantity > 0
                ? `${product.stockQuantity} units available · ships in 2-4 days`
                : "Currently out of stock"}
            </div>

            {product.stockQuantity > 0 && (
              <div className="purchase-section">
                <label htmlFor="quantity">Quantity</label>
                <div className="purchase-controls">
                  <input
                    id="quantity"
                    type="number"
                    min="1"
                    max={product.stockQuantity}
                    value={quantity}
                    onChange={(event) => setQuantity(Number(event.target.value))}
                  />
                  <button type="button" onClick={handleAddToCart}>
                    Add to bag
                  </button>
                  <button
                    type="button"
                    className="ghost-button"
                    onClick={() => {
                      const added = toggleWishlist(product);
                      toast(added ? "Saved to wishlist" : "Removed from wishlist");
                    }}
                  >
                    {isWishlisted(product.id) ? "Saved" : "Wishlist"}
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>

        <section className="reviews-panel">
          <div className="section-heading">
            <h2>Customer reviews</h2>
          </div>

          {isAuthenticated && user?.role === "CUSTOMER" ? (
            <form className="review-form" onSubmit={handleReview}>
              <label htmlFor="rating">Your rating</label>
              <select id="rating" value={rating} onChange={(event) => setRating(event.target.value)}>
                <option value="5">5 · Excellent</option>
                <option value="4">4 · Good</option>
                <option value="3">3 · Okay</option>
                <option value="2">2 · Poor</option>
                <option value="1">1 · Terrible</option>
              </select>
              <textarea
                rows="3"
                placeholder="Share how the product felt in real use"
                value={comment}
                onChange={(event) => setComment(event.target.value)}
              />
              {reviewError && <p className="cart-error-message">{reviewError}</p>}
              <button type="submit">Post review</button>
            </form>
          ) : (
            <p>
              <Link to="/login">Sign in</Link> as a customer to leave a review.
            </p>
          )}

          {reviews.length === 0 ? (
            <p>No reviews yet. Be the first to rate this product.</p>
          ) : (
            <div className="review-list">
              {reviews.map((review) => (
                <article key={review.id} className="review-card">
                  <strong>{review.userName || "Customer"}</strong>
                  <span>{"★".repeat(review.rating || 0)}</span>
                  <p>{review.comment || "No comment"}</p>
                </article>
              ))}
            </div>
          )}
        </section>
      </div>
    </main>
  );
}

export default ProductDetails;
