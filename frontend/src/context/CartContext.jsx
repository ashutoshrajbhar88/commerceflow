import { createContext, useContext, useEffect, useState } from "react";
import { getCart } from "../services/cartService";
import { useAuth } from "./AuthContext";

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const { isAuthenticated, user } = useAuth();
  const [cart, setCart] = useState(null);

  const itemCount =
    cart?.items?.reduce((sum, item) => sum + (item.quantity || 0), 0) || 0;

  const refreshCart = async () => {
    if (!isAuthenticated || user?.role === "ADMIN") {
      setCart(null);
      return null;
    }

    try {
      const data = await getCart();
      setCart(data);
      return data;
    } catch (error) {
      console.error("Failed to load cart:", error);
      return null;
    }
  };

  useEffect(() => {
    refreshCart();
  }, [isAuthenticated, user?.role]);

  return (
    <CartContext.Provider value={{ cart, setCart, itemCount, refreshCart }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  return useContext(CartContext);
}
