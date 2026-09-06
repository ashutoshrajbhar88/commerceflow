import api from "./api";

export const createPayment = async (orderId, paymentMethod) => {
  const response = await api.post("/payments", {
    orderId,
    paymentMethod,
  });

  return response.data;
};

export const getPaymentByOrder = async (orderId) => {
  const response = await api.get(`/payments/order/${orderId}`);
  return response.data;
};

export const getMyPayments = async () => {
  const response = await api.get("/payments/my");
  return response.data;
};