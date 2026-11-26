import axios from 'axios'

// Deployed Railway backend URL
const BASE_URL =
  'https://product-management-ecommerce-system-production.up.railway.app'

// Product API
const API = axios.create({
  baseURL: `${BASE_URL}/products`,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Order API
const ORDER_API = axios.create({
  baseURL: `${BASE_URL}/api/orders`,
  headers: {
    'Content-Type': 'application/json'
  }
})

const productService = {
  // Products
  getAllProducts: () => API.get(''),

  getCustomerCatalogue: () =>
    axios.get(`${BASE_URL}/customer/products`),

  getProductById: (id) => API.get(`/${id}`),

  addProduct: (product) => API.post('', product),

  updateProduct: (product) => API.put('', product),

  deleteProduct: (id) => API.delete(`/${id}`),

  searchProducts: (keyword) =>
    API.get('/search', { params: { keyword } }),

  getProductsPage: (page = 0, size = 10) =>
    API.get('/page', { params: { page, size } }),

  getSortedProducts: (
    page = 0,
    size = 10,
    sortBy = 'prodId',
    direction = 'asc'
  ) =>
    API.get('/sort', {
      params: { page, size, sortBy, direction }
    }),

  // Dashboard
  getDashboardStats: () => API.get('/dashboard'),

  // Inventory
  getLowStockProducts: () => API.get('/low-stock'),

  getOutOfStockProducts: () => API.get('/out-of-stock'),

  getInStockProducts: () => API.get('/in-stock'),

  addStock: (id, quantity) =>
    API.patch(`/${id}/add-stock`, null, {
      params: { quantity }
    }),

  reduceStock: (id, quantity) =>
    API.patch(`/${id}/reduce-stock`, null, {
      params: { quantity }
    }),

  // Orders
  getAllOrders: () => ORDER_API.get(''),

  getOrderById: (id) => ORDER_API.get(`/${id}`),

  updateOrderStatus: (id, status) =>
    ORDER_API.patch(`/${id}/status`, { status }),

  checkout: (orderData) =>
    ORDER_API.post('/checkout', orderData)
}

export default productService