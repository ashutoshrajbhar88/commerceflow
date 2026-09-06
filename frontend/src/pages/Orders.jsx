import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { getMyOrders } from "../services/orderService";
import "./Orders.css";

function Orders() {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    getMyOrders()
      .then((data) => {
        console.log("My orders:", data);

        if (Array.isArray(data)) {
          setOrders(data);
        } else if (data?.content) {
          setOrders(data.content);
        } else {
          setOrders([]);
        }
      })
      .catch((error) => {
        console.error("Failed to load orders:", error);

        const status = error.response?.status;
        const message = error.response?.data?.message;

        if (message) {
          setError(status + ": " + message);
        } else {
          setError("Request failed: " + (status || "No response"));
        }
      });
  }, []);

  if (error) {
    return (
      <main className="orders-page">
        <div className="orders-container">
          <div className="orders-error">
            <h1>My Orders</h1>
            <p>{error}</p>
            <Link to="/products" className="orders-primary-button">
              Continue Shopping
            </Link>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="orders-page">
      <div className="orders-container">
        <div className="orders-header">
          <div>
            <p className="orders-eyebrow">ACCOUNT</p>
            <h1>My Orders</h1>
            <p className="orders-subtitle">
              View and track your recent orders.
            </p>
          </div>

          <div className="orders-count">
            {orders.length} {orders.length === 1 ? "Order" : "Orders"}
          </div>
        </div>

        {orders.length === 0 ? (
          <div className="empty-orders">
            <div className="empty-orders-icon">📦</div>
            <h2>No orders yet</h2>
            <p>
              You haven't placed any orders yet. Browse our products and
              start shopping.
            </p>

            <Link to="/products" className="orders-primary-button">
              Start Shopping
            </Link>
          </div>
        ) : (
          <div className="orders-list">
            {orders.map((order) => (
              <article className="order-card" key={order.id}>
                <div className="order-card-header">
                  <div>
                    <p className="order-label">ORDER</p>

                    <Link
                      to={`/orders/${order.id}`}
                      className="order-number"
                    >
                      #{order.id}
                    </Link>
                  </div>

                  <span
                    className={`order-status status-${order.status?.toLowerCase()}`}
                  >
                    {order.status}
                  </span>
                </div>

                <div className="order-card-details">
                  <div className="order-detail">
                    <span>Payment</span>
                    <strong>
                      {order.paymentStatus || "NO PAYMENT"}
                    </strong>
                  </div>

                  <div className="order-detail">
                    <span>Total</span>
                    <strong>₹{order.totalAmount}</strong>
                  </div>

                  <div className="order-detail">
                    <span>Created</span>
                    <strong>
                      {order.createdAt
                        ? new Date(order.createdAt).toLocaleString()
                        : "N/A"}
                    </strong>
                  </div>
                </div>

                <div className="order-card-footer">
                  <Link
                    to={`/orders/${order.id}`}
                    className="view-order-button"
                  >
                    View Order
                  </Link>
                </div>
              </article>
            ))}
          </div>
        )}

        <div className="orders-footer">
          <Link to="/products" className="continue-shopping-link">
            Continue Shopping →
          </Link>
        </div>
      </div>
    </main>
  );
}

export default Orders;