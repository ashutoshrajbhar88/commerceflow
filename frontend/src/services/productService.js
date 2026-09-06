import api from "./api";

export const getProducts = async () => {
  const response = await api.get("/v1/products");
  return response.data;
};

export const getProductById = async (id) => {
  const response = await api.get(`/v1/products/${id}`);
  return response.data;
};