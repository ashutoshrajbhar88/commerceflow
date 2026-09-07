import { Link, useNavigate } from "react-router-dom";
import { addToCart } from "../services/cartService";
import { useAuth } from "../context/AuthContext";
import { useCart } from "../context/CartContext";
import { useWishlist } from "../context/WishlistContext";
import { useToast } from "../context/ToastContext";
import { formatPrice, getPrimaryImage, productImageUrl } from "../utils/format";
import "./ProductCard.css";

function ProductCard({ product }) {
  const navigate = useNavigate();
  const { isAuthenticated, user } = useAuth();
  const { refreshCart } = useCart();
  const { isWishlisted, toggleWishlist } = useWishlist();
  const { toast } = useToast();

  const image = getPrimaryImage(product) || productImageUrl(product.imageUrl);
  const saved = isWishlisted(product.id);
  const inStock = product.stockQuantity > 0;
  const isNew =
    product.createdAt &&
    Date.now() - new Date(product.createdAt).getTime() < 1000 * 60 * 60 * 24 * 14;

  const handleWishlist = (event) => {
    event.preventDefault();
    event.stopPropagation();
    const added = toggleWishlist(product);
    toast(added ? "Saved to wishlist" : "Removed from wishlist", "success");
  };

  const handleAddToCart = async (event) => {
    event.preventDefault();
    event.stopPropagation();

    if (!isAuthenticated) {
      navigate("/login", { state: { from: `/products/${product.id}` } });
      return;
    }

    if (user?.role === "ADMIN") {
      toast("Switch to a customer account to shop", "error");
      return;
    }

    if (!inStock) {
      toast("This product is out of stock", "error");
      return;
    }

    try {
      await addToCart(product.id, 1);
      await refreshCart();
      toast("Added to bag", "success");
    } catch (error) {
      toast(error.response?.data?.message || "Could not add to bag", "error");
    }
  };

  return (
    <article className="store-card">
      <Link to={`/products/${product.id}`} className="store-card-media">
        {image ? (
          <img src={image} alt={product.name} />
        ) : (
          <div className="store-card-placeholder">No image</div>
        )}

        <div className="store-card-badges">
          {isNew && <span className="badge badge-new">New</span>}
          {!inStock && <span className="badge badge-out">Sold out</span>}
        </div>

        <button
          type="button"
          className={`wishlist-toggle ${saved ? "is-saved" : ""}`}
          onClick={handleWishlist}
          aria-label={saved ? "Remove from wishlist" : "Save to wishlist"}
        >
          {saved ? "♥" : "♡"}
        </button>
      </Link>

      <div className="store-card-body">
        <p className="store-card-category">
          {product.categoryName || "Collection"}
        </p>
        <Link to={`/products/${product.id}`} className="store-card-name">
          {product.name}
        </Link>
        <div className="store-card-meta">
          <strong>{formatPrice(product.price)}</strong>
          <span className={inStock ? "in-stock" : "out-of-stock"}>
            {inStock ? "In stock" : "Out of stock"}
          </span>
        </div>
        <button
          type="button"
          className="store-card-cart"
          onClick={handleAddToCart}
          disabled={!inStock}
        >
          {inStock ? "Add to bag" : "Notify me"}
        </button>
      </div>
    </article>
  );
}

export default ProductCard;
