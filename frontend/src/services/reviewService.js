import api from "./api";

export const getProductReviews = async (productId) => {
  const response = await api.get(`/v1/products/${productId}/reviews`);
  return response.data;
};

export const createReview = async (productId, payload) => {
  const response = await api.post(`/v1/products/${productId}/reviews`, payload);
  return response.data;
};
