/**
 * Formatea un número como moneda (sin el símbolo $) con el formato 1,000.00
 * @param {number|string} amount 
 * @returns {string} valor formateado
 */
export const formatCurrency = (amount) => {
  const num = Number(amount) || 0;
  return num.toLocaleString('en-US', {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2
  });
};

/**
 * Formatea un número como moneda con el símbolo $ (ej: $1,000.00)
 * @param {number|string} amount 
 * @returns {string} valor formateado
 */
export const formatCurrencyWithSign = (amount) => {
  return `$${formatCurrency(amount)}`;
};

/**
 * Remueve acentos de un string para búsquedas insensibles a acentos
 * @param {string} str 
 * @returns {string} string sin acentos
 */
export const removeAccents = (str) => {
  return str.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
};
