import { useState } from "react";
import { login } from "../services/authService";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Login.css";

function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const { login: saveToken } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      const data = await login(email, password);

      saveToken(data.token);

      navigate(location.state?.from || "/home");
    } catch (error) {
      console.error("Login failed:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (status === 401) {
        setError("Invalid email or password");
      } else if (message) {
        setError(message);
      } else {
        setError(
          "Login failed: " + (status || "No response")
        );
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="login-page">
      <section className="auth-visual">
        <p className="login-eyebrow">COMMERCEFLOW</p>
        <h2>Your bag, orders and wishlist in one place.</h2>
        <p>Sign in to checkout faster and track every order.</p>
      </section>
      <div className="login-card">
        <div className="login-header">
          <p className="login-eyebrow">WELCOME BACK</p>
          <h1>Sign in</h1>
          <p>Use your customer or admin account to continue.</p>
        </div>

        {error && (
          <div className="login-error">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="login-form">
          <div className="login-form-group">
            <label htmlFor="email">Email</label>

            <input
              id="email"
              type="email"
              value={email}
              onChange={(event) =>
                setEmail(event.target.value)
              }
              placeholder="Enter your email"
              autoComplete="email"
              required
            />
          </div>

          <div className="login-form-group">
            <label htmlFor="password">Password</label>

            <input
              id="password"
              type="password"
              value={password}
              onChange={(event) =>
                setPassword(event.target.value)
              }
              placeholder="Enter your password"
              autoComplete="current-password"
              required
            />
          </div>

          <button
            type="submit"
            className="login-button"
            disabled={loading}

          >
            {loading ? "Signing in..." : "Sign In"}
          </button>
          <div className="login-register-link">
            <span>Don't have an account?</span>
            <Link to="/register">Create Account</Link>
          </div>
        </form>
      </div>
    </main>
  );
}

export default Login;