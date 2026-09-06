import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { createProduct } from "../services/adminProductService";
import "./CreateProduct.css";

function CreateProduct() {
  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "",
    stockQuantity: "",
    categoryId: "",
  });

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [saving, setSaving] = useState(false);

  const navigate = useNavigate();

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");
    setSaving(true);

    try {
      const product = {
        name: form.name,
        description: form.description,
        price: Number(form.price),
        stockQuantity: Number(form.stockQuantity),
        categoryId: Number(form.categoryId),
      };

      const createdProduct = await createProduct(product);

      console.log("Product created:", createdProduct);

      setSuccess("Product created successfully.");

      setTimeout(() => {
        navigate("/admin/products");
      }, 1000);
    } catch (error) {
      console.error("Failed to create product:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setError(status + ": " + message);
      } else {
        setError(
          "Failed to create product: " + (status || "No response")
        );
      }
    } finally {
      setSaving(false);
    }
  };

  return (
    <main className="create-product-page">
      <div className="create-product-container">
        <Link
          to="/admin/products"
          className="back-products-link"
        >
          ← Back to Products
        </Link>

        <div className="create-product-header">
          <p className="create-product-eyebrow">
            CATALOG MANAGEMENT
          </p>

          <h1>Create Product</h1>

          <p>
            Add a new product to your CommerceFlow catalog.
          </p>
        </div>

        <div className="create-product-card">
          {error && (
            <div className="create-product-error">
              {error}
            </div>
          )}

          {success && (
            <div className="create-product-success">
              {success}
            </div>
          )}

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="name">Product Name</label>

              <input
                id="name"
                type="text"
                name="name"
                value={form.name}
                onChange={handleChange}
                placeholder="Enter product name"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="description">
                Description
              </label>

              <textarea
                id="description"
                name="description"
                value={form.description}
                onChange={handleChange}
                placeholder="Describe the product"
                rows="5"
                required
              />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label htmlFor="price">Price</label>

                <div className="input-with-prefix">
                  <span>₹</span>

                  <input
                    id="price"
                    type="number"
                    name="price"
                    value={form.price}
                    onChange={handleChange}
                    placeholder="0.00"
                    min="0.01"
                    step="0.01"
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label htmlFor="stockQuantity">
                  Stock Quantity
                </label>

                <input
                  id="stockQuantity"
                  type="number"
                  name="stockQuantity"
                  value={form.stockQuantity}
                  onChange={handleChange}
                  placeholder="0"
                  min="0"
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="categoryId">Category ID</label>

              <input
                id="categoryId"
                type="number"
                name="categoryId"
                value={form.categoryId}
                onChange={handleChange}
                placeholder="Enter category ID"
                min="1"
                required
              />

              <small>
                Enter the ID of an existing product category.
              </small>
            </div>

            <div className="form-actions">
              <Link
                to="/admin/products"
                className="cancel-product-button"
              >
                Cancel
              </Link>

              <button
                type="submit"
                className="save-product-button"
                disabled={saving}
              >
                {saving ? "Creating..." : "Create Product"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </main>
  );
}

export default CreateProduct;