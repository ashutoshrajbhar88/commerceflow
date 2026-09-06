
import api from "./api";

export const getMyOrders = async () => {
  const response = await api.get("/orders/my");

  return response.data;
};
export const getOrderById = async (id) => {
  const response = await api.get(`/orders/${id}`);
  return response.data;
};

export const getOrderHistory = async (id) => {
  const response = await api.get(`/orders/${id}/history`);
  return response.data;
};
export const cancelOrder = async (id) => {
  const response = await api.patch(`/orders/${id}/cancel`);
  return response.data;
};