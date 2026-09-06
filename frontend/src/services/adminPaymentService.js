import api from "./api";

export const getAllPayments = async () => {
  const response = await api.get("/payments");
  return response.data;
};

export const markPaymentSuccess = async (paymentId) => {
  const response = await api.put(
    `/payments/${paymentId}/success`
  );
  return response.data;
};

export const markPaymentFailed = async (paymentId) => {
  const response = await api.put(
    `/payments/${paymentId}/failed`
  );
  return response.data;
};

export const refundPayment = async (paymentId) => {
  const response = await api.put(
    `/payments/${paymentId}/refund`
  );
  return response.data;
};