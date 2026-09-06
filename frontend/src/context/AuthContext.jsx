import { createContext, useContext, useState } from "react";

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

  const login = (newToken) => {
    if (!isTokenValid(newToken)) {
      console.error("Received invalid or expired JWT token.");
      return;
    }

    localStorage.setItem("token", newToken);
    setToken(newToken);
    setUser(getUserFromToken(newToken));
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