import { useEffect, useState } from "react";
import { getDashboard } from "../services/adminService";
import "./AdminDashboard.css";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

function AdminDashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [error, setError] = useState("");

  const chartData = dashboard
    ? [
        { status: "Pending", count: dashboard.pendingOrders },
        { status: "Confirmed", count: dashboard.confirmedOrders },
        { status: "Shipped", count: dashboard.shippedOrders },
        { status: "Delivered", count: dashboard.deliveredOrders },
        { status: "Cancelled", count: dashboard.cancelledOrders },
      ]
    : [];

  useEffect(() => {
    getDashboard()
      .then((data) => {
        console.log("Dashboard data:", data);
        setDashboard(data);
      })
      .catch((error) => {
        console.error("Failed to load dashboard:", error);

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
      <main className="admin-dashboard-page">
        <div className="admin-dashboard-container">
          <div className="dashboard-error">
            <h1>Admin Dashboard</h1>
            <p>{error}</p>
          </div>
        </div>
      </main>
    );
  }

  if (!dashboard) {
    return (
      <main className="admin-dashboard-page">
        <div className="admin-dashboard-container">
          <div className="dashboard-loading">
            <p>Loading dashboard...</p>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="admin-dashboard-page">
      <div className="admin-dashboard-container">

        <div className="dashboard-header">
          <div>
            <p className="dashboard-eyebrow">ADMINISTRATION</p>
            <h1>Dashboard</h1>
            <p className="dashboard-subtitle">
              Monitor your store's orders and revenue.
            </p>
          </div>
        </div>

        <section className="dashboard-stats">
          <div className="dashboard-card dashboard-card-primary">
            <span className="dashboard-card-label">
              Total Orders
            </span>

            <strong className="dashboard-card-value">
              {dashboard.totalOrders}
            </strong>
          </div>

          <div className="dashboard-card dashboard-card-revenue">
            <span className="dashboard-card-label">
              Total Revenue
            </span>

            <strong className="dashboard-card-value">
              ₹{dashboard.totalRevenue}
            </strong>
          </div>

          <div className="dashboard-card">
            <span className="dashboard-card-label">
              Pending
            </span>

            <strong className="dashboard-card-value">
              {dashboard.pendingOrders}
            </strong>
          </div>

          <div className="dashboard-card">
            <span className="dashboard-card-label">
              Confirmed
            </span>

            <strong className="dashboard-card-value">
              {dashboard.confirmedOrders}
            </strong>
          </div>

          <div className="dashboard-card">
            <span className="dashboard-card-label">
              Shipped
            </span>

            <strong className="dashboard-card-value">
              {dashboard.shippedOrders}
            </strong>
          </div>

          <div className="dashboard-card">
            <span className="dashboard-card-label">
              Delivered
            </span>

            <strong className="dashboard-card-value">
              {dashboard.deliveredOrders}
            </strong>
          </div>

          <div className="dashboard-card">
            <span className="dashboard-card-label">
              Cancelled
            </span>

            <strong className="dashboard-card-value">
              {dashboard.cancelledOrders}
            </strong>
          </div>
        </section>

        <section className="dashboard-chart-card">
          <div className="dashboard-section-header">
            <div>
              <p className="dashboard-section-eyebrow">
                ORDER ANALYTICS
              </p>

              <h2>Order Status</h2>
            </div>
          </div>

          <div className="dashboard-chart">
            <ResponsiveContainer width="100%" height={350}>
              <BarChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="status" />
                <YAxis allowDecimals={false} />
                <Tooltip />
                <Bar dataKey="count" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </section>

      </div>
    </main>
  );
}

export default AdminDashboard;