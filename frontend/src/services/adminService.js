import api from "./api";

export const getDashboard = async () => {
  const response = await api.get("/orders/dashboard");
  return response.data;
};