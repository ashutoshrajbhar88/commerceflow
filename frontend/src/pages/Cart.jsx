import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  getCart,
  updateCartItem,
  removeCartItem,
  clearCart,
  checkout,
} from "../services/cartService";
import "./Cart.css";

function Cart() {
  const [cart, setCart] = useState(null);
  const [error, setError] = useState("");
  const [cartActionError, setCartActionError] = useState("");
  const [checkoutError, setCheckoutError] = useState("");
  const [checkingOut, setCheckingOut] = useState(false);
  const [updatingProductId, setUpdatingProductId] = useState(null);

  const navigate = useNavigate();

  const loadCart = async () => {
    try {
      setError("");

      const data = await getCart();
      setCart(data);
    } catch (error) {
      console.error("Failed to load cart:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setError(status + ": " + message);
      } else {
        setError("Request failed: " + (status || "No response"));
      }
    }
  };

  useEffect(() => {
    loadCart();
  }, []);

  const handleUpdateQuantity = async (productId, quantity) => {
    setCartActionError("");

    if (quantity < 1) {
      setCartActionError("Quantity must be at least 1.");
      return;
    }

    try {
      setUpdatingProductId(productId);

      const updatedCart = await updateCartItem(productId, quantity);

      setCart(updatedCart);
    } catch (error) {
      console.error("Failed to update cart item:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setCartActionError(status + ": " + message);
      } else {
        setCartActionError(
          "Update failed: " + (status || "No response")
        );
      }
    } finally {
      setUpdatingProductId(null);
    }
  };

  const handleRemoveItem = async (productId) => {
    setCartActionError("");

    try {
      setUpdatingProductId(productId);

      const updatedCart = await removeCartItem(productId);

      setCart(updatedCart);
    } catch (error) {
      console.error("Failed to remove cart item:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setCartActionError(status + ": " + message);
      } else {
        setCartActionError(
          "Remove failed: " + (status || "No response")
        );
      }
    } finally {
      setUpdatingProductId(null);
    }
  };

  const handleClearCart = async () => {
    setCartActionError("");

    const confirmed = window.confirm(
      "Are you sure you want to clear your cart?"
    );

    if (!confirmed) {
      return;
    }

    try {
      const updatedCart = await clearCart();

      setCart(updatedCart);
    } catch (error) {
      console.error("Failed to clear cart:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setCartActionError(status + ": " + message);
      } else {
        setCartActionError(
          "Clear cart failed: " + (status || "No response")
        );
      }
    }
  };

  const handleCheckout = async () => {
    setCheckoutError("");
    setCheckingOut(true);

    try {
      const order = await checkout();

      console.log("Checkout successful:", order);

      navigate("/payment", {
        state: {
          order: order,
        },
      });
    } catch (error) {
      console.error("Checkout failed:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setCheckoutError(status + ": " + message);
      } else {
        setCheckoutError(
          "Checkout failed: " + (status || "No response")
        );
      }
    } finally {
      setCheckingOut(false);
    }
  };

  if (error) {
    return (
      <main className="cart-page">
        <div className="cart-container">
          <div className="cart-error">
            <h1>Shopping Cart</h1>
            <p>{error}</p>
            <Link to="/products">Continue Shopping</Link>
          </div>
        </div>
      </main>
    );
  }

  if (!cart) {
    return (
      <main className="cart-page">
        <div className="cart-container">
          <p>Loading cart...</p>
        </div>
      </main>
    );
  }

  if (cart.items.length === 0) {
    return (
      <main className="cart-page">
        <div className="cart-container">
          <div className="cart-header">
            <div>
              <p className="cart-eyebrow">SHOPPING</p>
              <h1>Shopping Cart</h1>
            </div>
          </div>

          <div className="empty-cart">
            <div className="empty-cart-icon">🛒</div>

            <h2>Your cart is empty</h2>

            <p>
              You haven't added any products to your cart yet.
            </p>

            <Link
              className="continue-shopping-button"
              to="/products"
            >
              Continue Shopping
            </Link>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="cart-page">
      <div className="cart-container">
        <div className="cart-header">
          <div>
            <p className="cart-eyebrow">SHOPPING</p>
            <h1>Shopping Cart</h1>
            <p>Review your items before checkout.</p>
          </div>

          <button
            className="clear-cart-button"
            onClick={handleClearCart}
            disabled={checkingOut}
          >
            Clear Cart
          </button>
        </div>

        {cartActionError && (
          <div className="cart-action-error">
            {cartActionError}
          </div>
        )}

        <div className="cart-layout">
          <section className="cart-items">
            {cart.items.map((item) => (
              <article className="cart-item" key={item.productId}>
                <div className="cart-item-info">
                  <h2>{item.productName}</h2>

                  <p className="cart-item-price">
                    ₹{item.price} each
                  </p>
                </div>

                <div className="cart-item-controls">
                  <div className="quantity-control">
                    <button
                      onClick={() =>
                        handleUpdateQuantity(
                          item.productId,
                          item.quantity - 1
                        )
                      }
                      disabled={
                        updatingProductId === item.productId ||
                        item.quantity <= 1
                      }
                    >
                      −
                    </button>

                    <span>{item.quantity}</span>

                    <button
                      onClick={() =>
                        handleUpdateQuantity(
                          item.productId,
                          item.quantity + 1
                        )
                      }
                      disabled={
                        updatingProductId === item.productId
                      }
                    >
                      +
                    </button>
                  </div>

                  <strong className="cart-item-subtotal">
                    ₹{item.subtotal}
                  </strong>

                  <button
                    className="remove-item-button"
                    onClick={() =>
                      handleRemoveItem(item.productId)
                    }
                    disabled={
                      updatingProductId === item.productId
                    }
                  >
                    {updatingProductId === item.productId
                      ? "Processing..."
                      : "Remove"}
                  </button>
                </div>
              </article>
            ))}
          </section>

          <aside className="cart-summary">
            <h2>Order Summary</h2>

            <div className="summary-row">
              <span>Items</span>
              <span>{cart.items.length}</span>
            </div>

            <div className="summary-row summary-total">
              <span>Total</span>
              <strong>₹{cart.totalAmount}</strong>
            </div>

            <button
              className="checkout-button"
              onClick={handleCheckout}
              disabled={checkingOut}
            >
              {checkingOut
                ? "Processing..."
                : "Proceed to Checkout"}
            </button>

            {checkoutError && (
              <div className="checkout-error">
                {checkoutError}
              </div>
            )}

            <Link
              className="continue-shopping-link"
              to="/products"
            >
              ← Continue Shopping
            </Link>
          </aside>
        </div>
      </div>
    </main>
  );
}

export default Cart;