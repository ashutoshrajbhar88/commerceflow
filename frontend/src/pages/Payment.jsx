import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { createPayment } from "../services/paymentService";
import "./Payment.css";

function Payment() {
  const location = useLocation();
  const navigate = useNavigate();

  const order = location.state?.order;

  const [paymentMethod, setPaymentMethod] = useState("UPI");
  const [error, setError] = useState("");
  const [processing, setProcessing] = useState(false);

  if (!order) {
    return (
      <main className="payment-page">
        <div className="payment-container">
          <div className="payment-error">
            <h1>Payment</h1>

            <p>Order information is missing.</p>

            <Link to="/orders" className="payment-primary-button">
              Go to My Orders
            </Link>
          </div>
        </div>
      </main>
    );
  }

  const handlePayment = async () => {
    setError("");
    setProcessing(true);

    try {
      const payment = await createPayment(
        order.id,
        paymentMethod
      );

      console.log("Payment created:", payment);

      navigate(`/orders/${order.id}`);
    } catch (error) {
      console.error("Payment failed:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setError(status + ": " + message);
      } else {
        setError(
          "Payment failed: " + (status || "No response")
        );
      }
    } finally {
      setProcessing(false);
    }
  };

  return (
    <main className="payment-page">
      <div className="payment-container">
        <Link to="/orders" className="back-orders-link">
          ← Back to My Orders
        </Link>

        <div className="payment-header">
          <p className="payment-eyebrow">CHECKOUT</p>
          <h1>Complete Your Payment</h1>
          <p>
            Select your preferred payment method for this order.
          </p>
        </div>

        <div className="payment-layout">
          <section className="payment-method-card">
            <div className="section-heading">
              <h2>Payment Method</h2>
            </div>

            <div className="payment-options">
              <label
                className={`payment-option ${
                  paymentMethod === "UPI"
                    ? "payment-option-selected"
                    : ""
                }`}
              >
                <input
                  type="radio"
                  value="UPI"
                  checked={paymentMethod === "UPI"}
                  onChange={(event) =>
                    setPaymentMethod(event.target.value)
                  }
                />

                <span className="payment-option-content">
                  <strong>UPI</strong>
                  <small>
                    Pay using your preferred UPI app
                  </small>
                </span>
              </label>

              <label
                className={`payment-option ${
                  paymentMethod === "CARD"
                    ? "payment-option-selected"
                    : ""
                }`}
              >
                <input
                  type="radio"
                  value="CARD"
                  checked={paymentMethod === "CARD"}
                  onChange={(event) =>
                    setPaymentMethod(event.target.value)
                  }
                />

                <span className="payment-option-content">
                  <strong>Card</strong>
                  <small>
                    Pay securely using your debit or credit card
                  </small>
                </span>
              </label>

              <label
                className={`payment-option ${
                  paymentMethod === "CASH_ON_DELIVERY"
                    ? "payment-option-selected"
                    : ""
                }`}
              >
                <input
                  type="radio"
                  value="CASH_ON_DELIVERY"
                  checked={
                    paymentMethod === "CASH_ON_DELIVERY"
                  }
                  onChange={(event) =>
                    setPaymentMethod(event.target.value)
                  }
                />

                <span className="payment-option-content">
                  <strong>Cash on Delivery</strong>
                  <small>
                    Pay when your order is delivered
                  </small>
                </span>
              </label>
            </div>

            {error && (
              <div className="payment-error-message">
                {error}
              </div>
            )}

            <button
              className="payment-submit-button"
              onClick={handlePayment}
              disabled={processing}
            >
              {processing
                ? "Processing..."
                : "Create Payment"}
            </button>
          </section>

          <aside className="payment-summary">
            <p className="summary-label">ORDER SUMMARY</p>

            <h2>Order #{order.id}</h2>

            <div className="summary-row">
              <span>Order status</span>
              <strong>{order.status}</strong>
            </div>

            <div className="summary-row">
              <span>Payment method</span>
              <strong>{paymentMethod}</strong>
            </div>

            <div className="summary-total">
              <span>Total Amount</span>
              <strong>₹{order.totalAmount}</strong>
            </div>

            <p className="payment-note">
              Your payment record will be created as pending and
              can be processed from the payment workflow.
            </p>
          </aside>
        </div>
      </div>
    </main>
  );
}

export default Payment;