import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { getProducts } from "../services/productService";
import { getCategories } from "../services/categoryService";
import ProductCard from "../components/ProductCard";
import "./Products.css";

function Products() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [keyword, setKeyword] = useState(searchParams.get("q") || "");
  const [categoryId, setCategoryId] = useState(searchParams.get("category") || "");
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");
  const [inStock, setInStock] = useState(false);
  const [sortBy, setSortBy] = useState("createdAt");
  const [direction, setDirection] = useState("desc");
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadProducts = async (currentPage = 0, nextKeyword = keyword, nextCategory = categoryId) => {
    try {
      setLoading(true);
      setError("");

      const data = await getProducts({
        keyword: nextKeyword,
        categoryId: nextCategory,
        minPrice,
        maxPrice,
        inStock,
        page: currentPage,
        size: 12,
        sortBy,
        direction,
      });

      setProducts(data.content || []);
      setTotalPages(data.totalPages || 0);
      setTotalElements(data.totalElements || data.content?.length || 0);
    } catch (requestError) {
      const status = requestError.response?.status;
      const message = requestError.response?.data?.message;
      setError(message ? `${status}: ${message}` : `Request failed: ${status || "No response"}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getCategories()
      .then((data) => setCategories(data || []))
      .catch((requestError) => console.error("Failed to load categories:", requestError));
  }, []);

  useEffect(() => {
    const nextKeyword = searchParams.get("q") || "";
    const nextCategory = searchParams.get("category") || "";
    setKeyword(nextKeyword);
    setCategoryId(nextCategory);
    setPage(0);
    loadProducts(0, nextKeyword, nextCategory);
  }, [searchParams, sortBy, direction]);

  const handleSearch = (event) => {
    event.preventDefault();
    const params = {};
    if (keyword.trim()) params.q = keyword.trim();
    if (categoryId) params.category = categoryId;
    setSearchParams(params);
    setPage(0);
    loadProducts(0);
  };

  const handleReset = () => {
    setKeyword("");
    setCategoryId("");
    setMinPrice("");
    setMaxPrice("");
    setInStock(false);
    setSortBy("createdAt");
    setDirection("desc");
    setPage(0);
    setSearchParams({});
    setTimeout(() => loadProducts(0, "", ""), 0);
  };

  return (
    <main className="products-page">
      <div className="products-container">
        <div className="products-header">
          <div>
            <p className="products-eyebrow">STORE</p>
            <h1>Shop the catalog</h1>
            <p className="products-subtitle">
              Search, filter by price, and add pieces to your bag in a tap.
            </p>
          </div>
          {!loading && !error && (
            <span className="product-count">{totalElements} products</span>
          )}
        </div>

        <form className="product-filters" onSubmit={handleSearch}>
          <div className="filter-group filter-search">
            <label htmlFor="keyword">Search</label>
            <input
              id="keyword"
              type="text"
              placeholder="Try headphones, shoes, jacket..."
              value={keyword}
              onChange={(event) => setKeyword(event.target.value)}
            />
          </div>

          <div className="filter-group">
            <label htmlFor="category">Category</label>
            <select
              id="category"
              value={categoryId}
              onChange={(event) => setCategoryId(event.target.value)}
            >
              <option value="">All categories</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.name}
                </option>
              ))}
            </select>
          </div>

          <div className="filter-group">
            <label htmlFor="minPrice">Min price</label>
            <input
              id="minPrice"
              type="number"
              min="0"
              placeholder="₹0"
              value={minPrice}
              onChange={(event) => setMinPrice(event.target.value)}
            />
          </div>

          <div className="filter-group">
            <label htmlFor="maxPrice">Max price</label>
            <input
              id="maxPrice"
              type="number"
              min="0"
              placeholder="₹100000"
              value={maxPrice}
              onChange={(event) => setMaxPrice(event.target.value)}
            />
          </div>

          <div className="filter-group">
            <label htmlFor="sortBy">Sort</label>
            <select
              id="sortBy"
              value={`${sortBy}:${direction}`}
              onChange={(event) => {
                const [nextSort, nextDirection] = event.target.value.split(":");
                setSortBy(nextSort);
                setDirection(nextDirection);
              }}
            >
              <option value="createdAt:desc">Newest</option>
              <option value="price:asc">Price: low to high</option>
              <option value="price:desc">Price: high to low</option>
              <option value="name:asc">Name: A to Z</option>
            </select>
          </div>

          <label className="stock-filter">
            <input
              type="checkbox"
              checked={inStock}
              onChange={(event) => setInStock(event.target.checked)}
            />
            In stock only
          </label>

          <div className="filter-actions">
            <button type="submit" className="filter-button">
              Apply
            </button>
            <button type="button" className="reset-filter-button" onClick={handleReset}>
              Reset
            </button>
          </div>
        </form>

        {error && (
          <div className="products-error">
            <strong>Unable to load products</strong>
            <p>{error}</p>
          </div>
        )}

        {loading && <div className="products-loading"><p>Loading products...</p></div>}

        {!loading && !error && products.length === 0 && (
          <div className="products-loading">
            <p>No products match those filters.</p>
          </div>
        )}

        {!loading && !error && products.length > 0 && (
          <>
            <div className="products-grid">
              {products.map((product) => (
                <ProductCard key={product.id} product={product} />
              ))}
            </div>

            {totalPages > 1 && (
              <div className="products-pagination">
                <button
                  type="button"
                  onClick={() => {
                    const newPage = page - 1;
                    setPage(newPage);
                    loadProducts(newPage);
                  }}
                  disabled={page === 0}
                >
                  Previous
                </button>
                <span>
                  Page {page + 1} of {totalPages}
                </span>
                <button
                  type="button"
                  onClick={() => {
                    const newPage = page + 1;
                    setPage(newPage);
                    loadProducts(newPage);
                  }}
                  disabled={page >= totalPages - 1}
                >
                  Next
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </main>
  );
}

export default Products;
