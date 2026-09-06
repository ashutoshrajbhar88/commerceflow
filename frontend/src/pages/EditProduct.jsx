import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import {
  getProducts,
  updateProduct,
  uploadProductImage,
} from "../services/adminProductService";
import "./EditProduct.css";

function EditProduct() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "",
    stockQuantity: "",
    categoryId: "",
  });

  const [imageFile, setImageFile] = useState(null);
  const [imageUploading, setImageUploading] = useState(false);
  const [imageError, setImageError] = useState("");
  const [imageSuccess, setImageSuccess] = useState("");

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    getProducts()
      .then((data) => {
        const products = Array.isArray(data)
          ? data
          : data?.content || [];

        const product = products.find(
          (item) => String(item.id) === String(id)
        );

        if (!product) {
          setError("Product not found.");
          return;
        }

        setForm({
          name: product.name || "",
          description: product.description || "",
          price: product.price ?? "",
          stockQuantity: product.stockQuantity ?? "",
          categoryId: product.categoryId ?? "",
        });
      })
      .catch((error) => {
        console.error("Failed to load product:", error);

        const status = error.response?.status;
        const message = error.response?.data?.message;

        if (message) {
          setError(status + ": " + message);
        } else {
          setError(
            "Request failed: " + (status || "No response")
          );
        }
      })
      .finally(() => {
        setLoading(false);
      });
  }, [id]);

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

      const updatedProduct = await updateProduct(id, product);

      console.log("Product updated:", updatedProduct);

      setSuccess("Product updated successfully.");

      setTimeout(() => {
        navigate("/admin/products");
      }, 1000);
    } catch (error) {
      console.error("Failed to update product:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setError(status + ": " + message);
      } else {
        setError(
          "Failed to update product: " +
            (status || "No response")
        );
      }
    } finally {
      setSaving(false);
    }
  };

  const handleImageChange = (event) => {
    const file = event.target.files[0];

    setImageFile(file || null);
    setImageError("");
    setImageSuccess("");
  };

  const handleImageUpload = async () => {
    if (!imageFile) {
      setImageError("Please select an image first.");
      return;
    }

    setImageError("");
    setImageSuccess("");
    setImageUploading(true);

    try {
      await uploadProductImage(id, imageFile);

      setImageSuccess("Image uploaded successfully.");
      setImageFile(null);
    } catch (error) {
      console.error("Failed to upload image:", error);

      const status = error.response?.status;
      const message = error.response?.data?.message;

      if (message) {
        setImageError(status + ": " + message);
      } else {
        setImageError(
          "Image upload failed: " +
            (status || "No response")
        );
      }
    } finally {
      setImageUploading(false);
    }
  };

  if (loading) {
    return (
      <main className="edit-product-page">
        <div className="edit-product-container">
          <div className="edit-product-loading">
            <p>Loading product...</p>
          </div>
        </div>
      </main>
    );
  }

  if (error && !form.name) {
    return (
      <main className="edit-product-page">
        <div className="edit-product-container">
          <div className="edit-product-error">
            <h1>Edit Product</h1>
            <p>{error}</p>

            <Link
              to="/admin/products"
              className="back-products-button"
            >
              Back to Products
            </Link>
          </div>
        </div>
      </main>
    );
  }

  return (
    <main className="edit-product-page">
      <div className="edit-product-container">
        <Link
          to="/admin/products"
          className="back-products-link"
        >
          ← Back to Products
        </Link>

        <div className="edit-product-header">
          <p className="edit-product-eyebrow">
            CATALOG MANAGEMENT
          </p>

          <h1>Edit Product</h1>

          <p>
            Update the details and image for this product.
          </p>
        </div>

        <section className="edit-product-card">
          {error && (
            <div className="edit-product-error-message">
              {error}
            </div>
          )}

          {success && (
            <div className="edit-product-success-message">
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
                  min="0"
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="categoryId">
                Category ID
              </label>

              <input
                id="categoryId"
                type="number"
                name="categoryId"
                value={form.categoryId}
                onChange={handleChange}
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
                {saving ? "Saving..." : "Update Product"}
              </button>
            </div>
          </form>
        </section>

        <section className="image-upload-card">
          <div className="image-upload-header">
            <div>
              <p className="image-upload-eyebrow">
                PRODUCT MEDIA
              </p>

              <h2>Product Image</h2>

              <p>
                Upload a new image for this product.
              </p>
            </div>
          </div>

          <div className="image-upload-area">
            <label
              htmlFor="product-image"
              className="image-file-label"
            >
              <span className="image-upload-icon">↑</span>

              <strong>
                {imageFile
                  ? imageFile.name
                  : "Choose an image"}
              </strong>

              <small>
                PNG, JPG, JPEG or other image formats
              </small>
            </label>

            <input
              id="product-image"
              className="image-file-input"
              type="file"
              accept="image/*"
              onChange={handleImageChange}
            />

            <button
              type="button"
              className="upload-image-button"
              onClick={handleImageUpload}
              disabled={imageUploading}
            >
              {imageUploading
                ? "Uploading..."
                : "Upload Image"}
            </button>
          </div>

          {imageError && (
            <div className="image-upload-error">
              {imageError}
            </div>
          )}

          {imageSuccess && (
            <div className="image-upload-success">
              {imageSuccess}
            </div>
          )}
        </section>
      </div>
    </main>
  );
}

export default EditProduct;