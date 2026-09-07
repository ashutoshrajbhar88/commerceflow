import { createContext, useContext, useEffect, useState } from "react";
import { useAuth } from "./AuthContext";

const WishlistContext = createContext(null);

function storageKey(email) {
  return `cf_wishlist_${email || "guest"}`;
}

function readWishlist(email) {
  try {
    const raw = localStorage.getItem(storageKey(email));
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

export function WishlistProvider({ children }) {
  const { user } = useAuth();
  const [items, setItems] = useState(() => readWishlist(user?.email));

  useEffect(() => {
    setItems(readWishlist(user?.email));
  }, [user?.email]);

  const persist = (next) => {
    setItems(next);
    localStorage.setItem(storageKey(user?.email), JSON.stringify(next));
  };

  const isWishlisted = (productId) =>
    items.some((item) => item.id === productId);

  const toggleWishlist = (product) => {
    if (!product?.id) {
      return;
    }

    if (isWishlisted(product.id)) {
      persist(items.filter((item) => item.id !== product.id));
      return false;
    }

    persist([
      {
        id: product.id,
        name: product.name,
        price: product.price,
        categoryName: product.categoryName,
        imageUrl: product.images?.[0]?.imageUrl || "",
        stockQuantity: product.stockQuantity,
      },
      ...items,
    ]);
    return true;
  };

  const removeFromWishlist = (productId) => {
    persist(items.filter((item) => item.id !== productId));
  };

  return (
    <WishlistContext.Provider
      value={{
        items,
        isWishlisted,
        toggleWishlist,
        removeFromWishlist,
      }}
    >
      {children}
    </WishlistContext.Provider>
  );
}

export function useWishlist() {
  return useContext(WishlistContext);
}
