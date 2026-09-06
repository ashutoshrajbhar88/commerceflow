import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import {
  getProducts,
  deleteProduct,
} from "../services/adminProductService";
import "./AdminProducts.css";

function AdminProducts() {
  const [products, setProducts] = useState([]);
  const [error, setError] = useState("");

  useEffect(() => {
    getProducts()
      .then((data) => {
        console.log("Admin products:", data);

        if (Array.isArray(data)) {
          setProducts(data);
        } else if (data?.content) {
          setProducts(data.content);
        } else {
          setProducts([]);
        }
      })
      .catch((error) => {
        console.error("Failed to load products:", error);

        const status = error.response?.status;
        const message = error.response?.data?.message;

        if (message) {
          setError(status + ": " + message);
        } else {
          setError("Request failed: " + (status || "No response"));
        }
      });
  }, []);

  const handleDelete = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this product?"
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");

      await deleteProduct(id);

      setProducts((previousProducts) =>
        previousProducts.filter((product) => product.id !== id)
      );
    } catch (error) {
      console.error("Failed to delete product:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setError(status + ": " + message);
      } else {
        setError("Delete failed: " + (status || "No response"));
      }
    }
  };

  if (error && products.length === 0) {
    return (
      <main className="admin-products-page">
        <div className="admin-products-container">
          <div className="admin-products-error">
            <h1>Admin Products</h1>
            <p>{error}</p>

            <Link
              to="/admin/products/create"
              className="create-product-button"
            >
              Create Product
            </Link>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="admin-products-page">
      <div className="admin-products-container">
        <div className="admin-products-header">
          <div>
            <p className="admin-products-eyebrow">
              CATALOG MANAGEMENT
            </p>

            <h1>Products</h1>

            <p className="admin-products-subtitle">
              Manage your store's product catalog.
            </p>
          </div>

          <Link
            to="/admin/products/create"
            className="create-product-button"
          >
            + Create Product
          </Link>
        </div>

        {error && (
          <div className="admin-products-action-error">
            {error}
          </div>
        )}

        <div className="admin-products-toolbar">
          <span>
            {products.length}{" "}
            {products.length === 1 ? "product" : "products"}
          </span>
        </div>

        {products.length === 0 ? (
          <div className="empty-products">
            <div className="empty-products-icon">📦</div>

            <h2>No products found</h2>

            <p>
              Your catalog is currently empty. Create your first
              product to get started.
            </p>

            <Link
              to="/admin/products/create"
              className="create-product-button"
            >
              Create Product
            </Link>
          </div>
        ) : (
          <div className="admin-products-table-wrapper">
            <table className="admin-products-table">
              <thead>
                <tr>
                  <th>Product</th>
                  <th>Category</th>
                  <th>Price</th>
                  <th>Stock</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>
                {products.map((product) => (
                  <tr key={product.id}>
                    <td>
                      <div className="admin-product-name">
                        <strong>{product.name}</strong>
                        <span>Product #{product.id}</span>
                      </div>
                    </td>

                    <td>
                      <span className="product-category-badge">
                        {product.categoryName || "Uncategorized"}
                      </span>
                    </td>

                    <td>
                      <strong className="product-price">
                        ₹{product.price}
                      </strong>
                    </td>

                    <td>
                      <span
                        className={`stock-badge ${
                          product.stockQuantity > 0
                            ? "stock-available"
                            : "stock-empty"
                        }`}
                      >
                        {product.stockQuantity > 0
                          ? `${product.stockQuantity} in stock`
                          : "Out of stock"}
                      </span>
                    </td>

                    <td>
                      <div className="product-actions">
                        <Link
                          to={`/admin/products/edit/${product.id}`}
                          className="edit-product-button"
                        >
                          Edit
                        </Link>

                        <button
                          className="delete-product-button"
                          onClick={() =>
                            handleDelete(product.id)
                          }
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </main>
  );
}

export default AdminProducts;