import api from "./api";

export const getProducts = async (filters = {}) => {
  const params = {
    page: filters.page ?? 0,
    size: filters.size ?? 10,
    sortBy: filters.sortBy ?? "createdAt",
    direction: filters.direction ?? "desc",
  };

  if (filters.keyword?.trim()) {
    params.keyword = filters.keyword.trim();
  }

  if (filters.categoryId) {
    params.categoryId = filters.categoryId;
  }

  if (filters.minPrice !== "" && filters.minPrice != null) {
    params.minPrice = filters.minPrice;
  }

  if (filters.maxPrice !== "" && filters.maxPrice != null) {
    params.maxPrice = filters.maxPrice;
  }

  if (filters.inStock) {
    params.inStock = true;
  }

  const response = await api.get("/v1/products", { params });
  return response.data;
};

export const getProductById = async (id) => {
  const response = await api.get(`/v1/products/${id}`);
  return response.data;
};