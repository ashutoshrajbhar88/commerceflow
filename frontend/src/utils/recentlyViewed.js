const STORAGE_KEY = "cf_recently_viewed";
const MAX_ITEMS = 8;

export function getRecentlyViewed() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

export function addRecentlyViewed(product) {
  if (!product?.id) {
    return;
  }

  const next = [
    {
      id: product.id,
      name: product.name,
      price: product.price,
      categoryName: product.categoryName,
      imageUrl: product.images?.[0]?.imageUrl || "",
      stockQuantity: product.stockQuantity,
    },
    ...getRecentlyViewed().filter((item) => item.id !== product.id),
  ].slice(0, MAX_ITEMS);

  localStorage.setItem(STORAGE_KEY, JSON.stringify(next));
}
