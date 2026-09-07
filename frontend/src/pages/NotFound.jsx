import { Link } from "react-router-dom";
import "./NotFound.css";

function NotFound() {
  return (
    <main className="not-found-page">
      <h1>404</h1>
      <p>That page is not in the catalog.</p>
      <Link to="/products">Continue shopping</Link>
    </main>
  );
}

export default NotFound;
