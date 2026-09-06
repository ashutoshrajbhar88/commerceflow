
import { useEffect, useState } from "react";
import {
  getAllPayments,
  markPaymentSuccess,
  markPaymentFailed,
  refundPayment,
} from "../services/adminPaymentService";
import "./AdminPayments.css";

function AdminPayments() {
  const [payments, setPayments] = useState([]);
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadPayments();
  }, []);

  const loadPayments = async () => {
    try {
      setError("");

      const data = await getAllPayments();

      console.log("Admin payments:", data);

      if (Array.isArray(data)) {
        setPayments(data);
      } else if (data?.content) {
        setPayments(data.content);
      } else {
        setPayments([]);
      }
    } catch (error) {
      console.error("Failed to load payments:", error);

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

  const handleSuccess = async (paymentId) => {
    try {
      setError("");

      await markPaymentSuccess(paymentId);

      await loadPayments();
    } catch (error) {
      console.error("Failed to mark payment successful:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setError(status + ": " + message);
      } else {
        setError(
          "Payment update failed: " + (status || "No response")
        );
      }
    }
  };

  const handleFailed = async (paymentId) => {
    try {
      setError("");

      await markPaymentFailed(paymentId);

      await loadPayments();
    } catch (error) {
      console.error("Failed to mark payment failed:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setError(status + ": " + message);
      } else {
        setError(
          "Payment update failed: " + (status || "No response")
        );
      }
    }
  };

  const handleRefund = async (paymentId) => {
    const confirmed = window.confirm(
      "Are you sure you want to refund this payment?"
    );

    if (!confirmed) return;

    try {
      setError("");

      await refundPayment(paymentId);

      await loadPayments();
    } catch (error) {
      console.error("Failed to refund payment:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setError(status + ": " + message);
      } else {
        setError(
          "Refund failed: " + (status || "No response")
        );
      }
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case "PENDING":
        return "payment-status-pending";

      case "SUCCESS":
        return "payment-status-success";

      case "FAILED":
        return "payment-status-failed";

      case "REFUNDED":
        return "payment-status-refunded";

      default:
        return "";
    }
  };

  const getMethodLabel = (method) => {
    switch (method) {
      case "CASH_ON_DELIVERY":
        return "Cash on Delivery";

      case "UPI":
        return "UPI";

      case "CARD":
        return "Card";

      default:
        return method || "N/A";
    }
  };

  if (loading) {
    return (
      <main className="admin-payments-page">
        <div className="admin-payments-container">
          <div className="admin-payments-loading">
            <div className="payment-loading-spinner"></div>
            <p>Loading payments...</p>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="admin-payments-page">
      <div className="admin-payments-container">
        <div className="admin-payments-header">
          <div>
            <p className="admin-payments-eyebrow">
              PAYMENT MANAGEMENT
            </p>

            <h1>Payments</h1>

            <p>
              Review and manage customer payment transactions.
            </p>
          </div>

          <div className="payments-count-card">
            <span>Total Payments</span>
            <strong>{payments.length}</strong>
          </div>
        </div>

        {error && (
          <div className="admin-payments-error">
            <strong>Something went wrong</strong>
            <span>{error}</span>
          </div>
        )}

        {payments.length === 0 ? (
          <div className="admin-payments-empty">
            <div className="payment-empty-icon">₹</div>

            <h2>No payments found</h2>

            <p>
              There are currently no payment transactions to
              display.
            </p>
          </div>
        ) : (
          <div className="admin-payments-list">
            {payments.map((payment) => (
              <article
                className="admin-payment-card"
                key={payment.id}
              >
                <div className="admin-payment-top">
                  <div>
                    <p className="payment-number-label">
                      PAYMENT
                    </p>

                    <h2>#{payment.id}</h2>
                  </div>

                  <span
                    className={`payment-status-badge ${getStatusClass(
                      payment.status
                    )}`}
                  >
                    {payment.status}
                  </span>
                </div>

                <div className="admin-payment-info">
                  <div className="payment-info-item">
                    <span className="payment-info-label">
                      Order
                    </span>

                    <strong>
                      #{payment.orderId}
                    </strong>
                  </div>

                  <div className="payment-info-item">
                    <span className="payment-info-label">
                      Amount
                    </span>

                    <strong>
                      ₹{payment.amount}
                    </strong>
                  </div>

                  <div className="payment-info-item">
                    <span className="payment-info-label">
                      Payment Method
                    </span>

                    <strong>
                      {getMethodLabel(
                        payment.paymentMethod
                      )}
                    </strong>
                  </div>
                </div>

                <div className="admin-payment-actions">
                  {payment.status === "PENDING" && (
                    <>
                      <button
                        type="button"
                        className="success-payment-button"
                        onClick={() =>
                          handleSuccess(payment.id)
                        }
                      >
                        Mark Success
                      </button>

                      <button
                        type="button"
                        className="failed-payment-button"
                        onClick={() =>
                          handleFailed(payment.id)
                        }
                      >
                        Mark Failed
                      </button>
                    </>
                  )}

                  {payment.status === "SUCCESS" && (
                    <button
                      type="button"
                      className="refund-payment-button"
                      onClick={() =>
                        handleRefund(payment.id)
                      }
                    >
                      Refund Payment
                    </button>
                  )}

                  {payment.status === "FAILED" && (
                    <span className="payment-action-note">
                      Payment failed
                    </span>
                  )}

                  {payment.status === "REFUNDED" && (
                    <span className="payment-action-note">
                      Payment refunded
                    </span>
                  )}
                </div>
              </article>
            ))}
          </div>
        )}
      </div>
    </main>
  );
}

export default AdminPayments;
