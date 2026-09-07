import { createContext, useContext, useEffect, useState } from "react";
import api from "../services/api";

const AuthContext = createContext(null);

function getUserFromToken(token) {
  if (!token) {
    return null;
  }

  try {
    const parts = token.split(".");

    if (parts.length !== 3) {
      return null;
    }

    const payload = JSON.parse(atob(parts[1]));

    return {
      email: payload.sub,
      role: payload.role,
      exp: payload.exp,
    };
  } catch (error) {
    console.error("Invalid JWT token:", error);
    return null;
  }
}

function isTokenValid(token) {
  const user = getUserFromToken(token);

  if (!user) {
    return false;
  }

  if (!user.exp) {
    return true;
  }

  return user.exp * 1000 > Date.now();
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => {
    const savedToken = localStorage.getItem("token");

    if (!isTokenValid(savedToken)) {
      localStorage.removeItem("token");
      return null;
    }

    return savedToken;
  });

  const [user, setUser] = useState(() => {
    const savedToken = localStorage.getItem("token");

    if (!isTokenValid(savedToken)) {
      localStorage.removeItem("token");
      return null;
    }

    return getUserFromToken(savedToken);
  });

  const [loadingUser, setLoadingUser] = useState(true);

  useEffect(() => {
    const loadCurrentUser = async () => {
      const savedToken = localStorage.getItem("token");

      if (!isTokenValid(savedToken)) {
        setLoadingUser(false);
        return;
      }

      try {
        const response = await api.get("/users/me");

        setUser((currentUser) => ({
          ...currentUser,
          ...response.data,
        }));
      } catch (error) {
        console.error("Failed to load current user:", error);

        if (error.response?.status === 401) {
          localStorage.removeItem("token");
          setToken(null);
          setUser(null);
        }
      } finally {
        setLoadingUser(false);
      }
    };

    loadCurrentUser();
  }, [token]);

  const login = async (newToken) => {
    if (!isTokenValid(newToken)) {
      console.error("Received invalid or expired JWT token.");
      return;
    }

    localStorage.setItem("token", newToken);
    setToken(newToken);

    const tokenUser = getUserFromToken(newToken);
    setUser(tokenUser);

    try {
      const response = await api.get("/users/me");

      setUser({
        ...tokenUser,
        ...response.data,
      });
    } catch (error) {
      console.error("Failed to load user profile:", error);
    }
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setUser(null);
  };

  const isAuthenticated =
    Boolean(token) && isTokenValid(token);

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        isAuthenticated,
        loadingUser,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}