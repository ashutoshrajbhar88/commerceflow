export function formatPrice(value) {
  const amount = Number(value);

  if (Number.isNaN(amount)) {
    return "₹0";
  }

  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 0,
  }).format(amount);
}

export function productImageUrl(imageUrl) {
  if (!imageUrl) {
    return "";
  }

  if (imageUrl.startsWith("http")) {
    return imageUrl;
  }

  return `http://localhost:8080${imageUrl}`;
}

export function getPrimaryImage(product) {
  const imageUrl = product?.images?.[0]?.imageUrl;
  return productImageUrl(imageUrl);
}
