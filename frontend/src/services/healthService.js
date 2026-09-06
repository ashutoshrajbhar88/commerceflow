import api from "./api";

export const checkHealth = async () => {
  const response = await api.get("/v1/health");
  return response.data;
};