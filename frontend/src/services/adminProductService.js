import api from "./api";

export const getProducts = async () => {
  const response = await api.get("/v1/products");
  return response.data;
};

export const createProduct = async (product) => {
  const response = await api.post("/v1/products", product);
  return response.data;
};

export const updateProduct = async (id, product) => {
  const response = await api.put(`/v1/products/${id}`, product);
  return response.data;
};

export const deleteProduct = async (id) => {
  const response = await api.delete(`/v1/products/${id}`);
  return response.data;
};
export const uploadProductImage = async (productId, file) => {
  const formData = new FormData();
  formData.append("file", file);

  const response = await api.post(
    `/v1/products/${productId}/images`,
    formData,
    {
      headers: {
        "Content-Type": undefined,
      },
    }
  );

  return response.data;
};