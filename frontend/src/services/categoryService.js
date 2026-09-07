import api from "./api";

export const getCategories = async () => {
  const response = await api.get("/v1/categories");
  return response.data;
};