import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import {
  getOrderById,
  getOrderHistory,
  cancelOrder,
} from "../services/orderService";
import "./OrderDetails.css";

function OrderDetails() {
  const { id } = useParams();

  const [order, setOrder] = useState(null);
  const [history, setHistory] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  const [cancelling, setCancelling] = useState(false);
  const [cancelError, setCancelError] = useState("");

  useEffect(() => {
    const loadOrderDetails = async () => {
      try {
        setError("");

        const [orderData, historyData] = await Promise.all([
          getOrderById(id),
          getOrderHistory(id),
        ]);

        setOrder(orderData);
        setHistory(historyData);
      } catch (error) {
        console.error("Failed to load order details:", error);

        const status = error.response?.status;
        const message = error.response?.data?.message;

        if (message) {
          setError(status + ": " + message);
        } else {
          setError("Request failed: " + (status || "No response"));
        }
      } finally {
        setLoading(false);
      }
    };

    loadOrderDetails();
  }, [id]);

  const handleCancelOrder = async () => {
    const confirmed = window.confirm(
      "Are you sure you want to cancel this order?"
    );

    if (!confirmed) {
      return;
    }

    setCancelError("");
    setCancelling(true);

    try {
      const updatedOrder = await cancelOrder(id);

      setOrder(updatedOrder);

      const updatedHistory = await getOrderHistory(id);
      setHistory(updatedHistory);
    } catch (error) {
      console.error("Failed to cancel order:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setCancelError(status + ": " + message);
      } else {
        setCancelError(
          "Cancellation failed: " + (status || "No response")
        );
      }
    } finally {
      setCancelling(false);
    }
  };

  if (loading) {
    return (
      <main className="order-details-page">
        <div className="order-details-container">
          <div className="order-details-loading">
            <p>Loading order details...</p>
          </div>
        </div>
      </main>
    );
  }

  if (error) {
    return (
      <main className="order-details-page">
        <div className="order-details-container">
          <div className="order-details-error">
            <h1>Order Details</h1>
            <p>{error}</p>

            <Link to="/orders" className="back-orders-button">
              Back to My Orders
            </Link>
          </div>
        </div>
      </main>
    );
  }

  if (!order) {
    return (
      <main className="order-details-page">
        <div className="order-details-container">
          <div className="order-details-error">
            <h1>Order Not Found</h1>
            <p>The requested order could not be found.</p>

            <Link to="/orders" className="back-orders-button">
              Back to My Orders
            </Link>
          </div>
        </div>
      </main>
    );
  }

  const canCancel =
    order.status === "PENDING" &&
    order.paymentStatus !== "SUCCESS";

  return (
    <main className="order-details-page">
      <div className="order-details-container">
        <Link to="/orders" className="back-orders-link">
          ← Back to My Orders
        </Link>

        <div className="order-details-header">
          <div>
            <p className="order-details-eyebrow">ORDER DETAILS</p>
            <h1>Order #{order.id}</h1>

            <p className="order-created">
              Placed{" "}
              {order.createdAt
                ? new Date(order.createdAt).toLocaleString()
                : "N/A"}
            </p>
          </div>

          <span
            className={`order-status status-${order.status?.toLowerCase()}`}
          >
            {order.status}
          </span>
        </div>

        <div className="order-details-layout">
          <div className="order-details-main">
            <section className="details-section">
              <div className="section-heading">
                <h2>Order Items</h2>
                <span>{order.items?.length || 0} items</span>
              </div>

              {order.items?.length === 0 ? (
                <div className="no-items">
                  <p>No items found.</p>
                </div>
              ) : (
                <div className="order-items">
                  {order.items.map((item) => (
                    <div className="order-item" key={item.productId}>
                      <div className="order-item-info">
                        <h3>{item.productName}</h3>

                        <p>
                          ₹{item.price} × {item.quantity}
                        </p>
                      </div>

                      <div className="order-item-subtotal">
                        ₹{item.subtotal}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </section>

            <section className="details-section">
              <div className="section-heading">
                <h2>Status History</h2>
              </div>

              {history.length === 0 ? (
                <div className="no-history">
                  <p>No status history found.</p>
                </div>
              ) : (
                <div className="status-history">
                  {history.map((entry, index) => (
                    <div className="history-entry" key={index}>
                      <div className="history-marker"></div>

                      <div className="history-content">
                        <div className="history-status">
                          {entry.oldStatus ? (
                            <>
                              <span>{entry.oldStatus}</span>
                              <strong>→</strong>
                              <span>{entry.newStatus}</span>
                            </>
                          ) : (
                            <span>{entry.newStatus}</span>
                          )}
                        </div>

                        <p>
                          {entry.changedAt
                            ? new Date(
                                entry.changedAt
                              ).toLocaleString()
                            : "N/A"}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </section>
          </div>

          <aside className="order-summary">
            <h2>Order Summary</h2>

            <div className="summary-detail">
              <span>Order ID</span>
              <strong>#{order.id}</strong>
            </div>

            <div className="summary-detail">
              <span>Payment</span>
              <strong>{order.paymentStatus || "NO PAYMENT"}</strong>
            </div>

            <div className="summary-detail">
              <span>Status</span>
              <strong>{order.status}</strong>
            </div>

            <div className="summary-total">
              <span>Total</span>
              <strong>₹{order.totalAmount}</strong>
            </div>

            {canCancel && (
              <div className="cancel-section">
                <button
                  className="cancel-order-button"
                  onClick={handleCancelOrder}
                  disabled={cancelling}
                >
                  {cancelling ? "Cancelling..." : "Cancel Order"}
                </button>

                {cancelError && (
                  <p className="cancel-error">{cancelError}</p>
                )}
              </div>
            )}

            {!canCancel &&
              order.status === "PENDING" &&
              order.paymentStatus === "SUCCESS" && (
                <p className="cancel-info">
                  This order cannot be cancelled because payment has already
                  been completed.
                </p>
              )}

            {order.status === "CANCELLED" && (
              <p className="cancel-info">
                This order has been cancelled.
              </p>
            )}

            {order.status === "DELIVERED" && (
              <p className="delivered-info">
                This order has been delivered.
              </p>
            )}
          </aside>
        </div>
      </div>
    </main>
  );
}

export default OrderDetails;