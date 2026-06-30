import axios from 'axios';

const API = axios.create({ baseURL: 'http://localhost:8080/api' });

// Automatically attach JWT token to every request
API.interceptors.request.use(config => {
  const token = localStorage.getItem('inv_token');
  if (token) config.headers['Authorization'] = `Bearer ${token}`;
  return config;
});

// Handle 401 — token expired or invalid
API.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('inv_token');
      localStorage.removeItem('inv_user');
      window.location.href = '/';
    }
    return Promise.reject(error);
  }
);

// Products
export const getProducts = () => API.get('/products');
export const getProductById = (id) => API.get(`/products/${id}`);
export const createProduct = (data) => API.post('/products', data);
export const updateProduct = (id, data) => API.put(`/products/${id}`, data);
export const deleteProduct = (id) => API.delete(`/products/${id}`);
export const getProductsByCategory = (cat) => API.get(`/products/category/${cat}`);

// Warehouses
export const getWarehouses = () => API.get('/warehouses');
export const createWarehouse = (data) => API.post('/warehouses', data);
export const updateWarehouse = (id, data) => API.put(`/warehouses/${id}`, data);
export const deleteWarehouse = (id) => API.delete(`/warehouses/${id}`);

// Inventory
export const getInventories = () => API.get('/inventories');
export const createInventory = (data) => API.post('/inventories', data);
export const updateInventory = (id, data) => API.put(`/inventories/${id}`, data);
export const deleteInventory = (id) => API.delete(`/inventories/${id}`);
export const getLowStockInventories = () => API.get('/inventories/low-stock');

// Sales
export const getSales = () => API.get('/sales');
export const createSale = (data) => API.post('/sales', data);
export const deleteSale = (id) => API.delete(`/sales/${id}`);
export const getSalesByDateRange = (start, end) => API.get(`/sales/date-range?startDate=${start}&endDate=${end}`);

// Forecasts
export const getForecastsByProduct = (pid) => API.get(`/forecasts/product/${pid}`);
export const getLatestForecast = (pid) => API.get(`/forecasts/product/${pid}/latest`);
export const createForecast = (data) => API.post('/forecasts', data);

// Suppliers
export const getSuppliers = () => API.get('/suppliers');
export const createSupplier = (data) => API.post('/suppliers', data);
export const updateSupplier = (id, data) => API.put(`/suppliers/${id}`, data);
export const deleteSupplier = (id) => API.delete(`/suppliers/${id}`);

// Purchase Orders
export const getPurchaseOrders = () => API.get('/purchase-orders');
export const createPurchaseOrder = (data) => API.post('/purchase-orders', data);
export const updateOrderStatus = (id, status) => API.patch(`/purchase-orders/${id}/status?status=${status}`);
export const deletePurchaseOrder = (id) => API.delete(`/purchase-orders/${id}`);

// Alerts
export const getAlerts = () => API.get('/alerts');
export const deleteAlert = (id) => API.delete(`/alerts/${id}`);

// ML Forecast
export const getMLForecast = (pid) => API.get(`/ml/forecast/${pid}`);
export const getMLForecastRetrain = (pid) => API.get(`/ml/forecast/${pid}/retrain`);
export const getMLForecastBatch = () => API.get('/ml/forecast/batch/all');
