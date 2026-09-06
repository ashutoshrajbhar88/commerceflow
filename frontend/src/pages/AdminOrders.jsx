
import { useEffect, useState } from "react";
import {
  getAllOrders,
  updateOrderStatus,
  cancelOrder,
  deleteOrder,
} from "../services/adminOrderService";
import "./AdminOrders.css";

function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    try {
      setError("");

      const data = await getAllOrders();

      console.log("Admin orders:", data);

      if (Array.isArray(data)) {
        setOrders(data);
      } else if (data?.content) {
        setOrders(data.content);
      } else {
        setOrders([]);
      }
    } catch (error) {
      console.error("Failed to load admin orders:", error);

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

  const handleStatusChange = async (id, status) => {
    try {
      setError("");

      const updatedOrder = await updateOrderStatus(id, status);

      console.log("Updated order:", updatedOrder);

      await loadOrders();
    } catch (error) {
      console.error("Failed to update order status:", error);

      const statusCode = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setError(statusCode + ": " + message);
      } else {
        setError(
          "Status update failed: " + (statusCode || "No response")
        );
      }
    }
  };

  const handleCancel = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to cancel this order?"
    );

    if (!confirmed) return;

    try {
      setError("");

      await cancelOrder(id);

      await loadOrders();
    } catch (error) {
      console.error("Failed to cancel order:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setError(status + ": " + message);
      } else {
        setError("Cancel failed: " + (status || "No response"));
      }
    }
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to permanently delete this order?"
    );

    if (!confirmed) return;

    try {
      setError("");

      await deleteOrder(id);

      setOrders((previousOrders) =>
        previousOrders.filter((order) => order.id !== id)
      );
    } catch (error) {
      console.error("Failed to delete order:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setError(status + ": " + message);
      } else {
        setError("Delete failed: " + (status || "No response"));
      }
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "PENDING":
        return "status-pending";

      case "CONFIRMED":
        return "status-confirmed";

      case "SHIPPED":
        return "status-shipped";

      case "DELIVERED":
        return "status-delivered";

      case "CANCELLED":
        return "status-cancelled";

      default:
        return "";
    }
  };

  const getPaymentClass = (paymentStatus) => {
    switch (paymentStatus) {
      case "SUCCESS":
        return "payment-success";

      case "FAILED":
        return "payment-failed";

      case "REFUNDED":
        return "payment-refunded";

      case "PENDING":
        return "payment-pending";

      default:
        return "payment-none";
    }
  };

  if (loading) {
    return (
      <main className="admin-orders-page">
        <div className="admin-orders-container">
          <div className="admin-orders-loading">
            <div className="loading-spinner"></div>
            <p>Loading orders...</p>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="admin-orders-page">
      <div className="admin-orders-container">
        <div className="admin-orders-header">
          <div>
            <p className="admin-orders-eyebrow">
              ORDER MANAGEMENT
            </p>

            <h1>Manage Orders</h1>

            <p>
              Review customer orders and manage their status.
            </p>
          </div>

          <div className="orders-count-card">
            <span>Total Orders</span>
            <strong>{orders.length}</strong>
          </div>
        </div>

        {error && (
          <div className="admin-orders-error">
            <strong>Something went wrong</strong>
            <span>{error}</span>
          </div>
        )}

        {orders.length === 0 ? (
          <div className="admin-orders-empty">
            <div className="empty-icon">□</div>

            <h2>No orders found</h2>

            <p>
              There are currently no customer orders to display.
            </p>
          </div>
        ) : (
          <div className="admin-orders-list">
            {orders.map((order) => (
              <article
                className="admin-order-card"
                key={order.id}
              >
                <div className="admin-order-top">
                  <div>
                    <p className="order-number-label">
                      ORDER
                    </p>

                    <h2>#{order.id}</h2>
                  </div>

                  <span
                    className={`order-status-badge ${getStatusClass(
                      order.status
                    )}`}
                  >
                    {order.status}
                  </span>
                </div>

                <div className="admin-order-info">
                  <div className="order-info-item">
                    <span className="info-label">
                      Customer
                    </span>

                    <strong>
                      {order.customerEmail || "N/A"}
                    </strong>
                  </div>

                  <div className="order-info-item">
                    <span className="info-label">
                      Order Total
                    </span>

                    <strong>
                      ₹{order.totalAmount}
                    </strong>
                  </div>

                  <div className="order-info-item">
                    <span className="info-label">
                      Payment
                    </span>

                    <span
                      className={`payment-badge ${getPaymentClass(
                        order.paymentStatus
                      )}`}
                    >
                      {order.paymentStatus || "NO PAYMENT"}
                    </span>
                  </div>
                </div>

                <div className="admin-order-actions">
                  <div className="status-control">
                    <label htmlFor={`status-${order.id}`}>
                      Update Status
                    </label>

                    <select
                      id={`status-${order.id}`}
                      value={order.status}
                      onChange={(event) =>
                        handleStatusChange(
                          order.id,
                          event.target.value
                        )
                      }
                    >
                      {order.status === "PENDING" && (
                        <>
                          <option value="PENDING">
                            PENDING
                          </option>

                          <option value="CONFIRMED">
                            CONFIRMED
                          </option>

                          <option value="CANCELLED">
                            CANCELLED
                          </option>
                        </>
                      )}

                      {order.status === "CONFIRMED" && (
                        <>
                          <option value="CONFIRMED">
                            CONFIRMED
                          </option>

                          <option value="SHIPPED">
                            SHIPPED
                          </option>

                          <option value="CANCELLED">
                            CANCELLED
                          </option>
                        </>
                      )}

                      {order.status === "SHIPPED" && (
                        <>
                          <option value="SHIPPED">
                            SHIPPED
                          </option>

                          <option value="DELIVERED">
                            DELIVERED
                          </option>
                        </>
                      )}

                      {order.status === "DELIVERED" && (
                        <option value="DELIVERED">
                          DELIVERED
                        </option>
                      )}

                      {order.status === "CANCELLED" && (
                        <option value="CANCELLED">
                          CANCELLED
                        </option>
                      )}
                    </select>
                  </div>

                  <div className="order-button-group">
                    {order.status === "PENDING" &&
                      order.paymentStatus !== "SUCCESS" && (
                        <button
                          type="button"
                          className="cancel-order-button"
                          onClick={() =>
                            handleCancel(order.id)
                          }
                        >
                          Cancel Order
                        </button>
                      )}

                    <button
                      type="button"
                      className="delete-order-button"
                      onClick={() =>
                        handleDelete(order.id)
                      }
                    >
                      Delete Order
                    </button>
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

export default AdminOrders;