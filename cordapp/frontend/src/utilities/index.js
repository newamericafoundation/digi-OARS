import lookup from "country-code-lookup";

export const toCountryByIsoFromX500 = (x500) => {
  const country = lookup.byIso(x500.split(/C=([a-zA-Z_]+)/)[1]).country;
  if (country === "United Kingdom") {
    return "Catan";
  }
  return country;
};

export const toCurrency = (number, currency) => {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: currency,
  }).format(number);
};