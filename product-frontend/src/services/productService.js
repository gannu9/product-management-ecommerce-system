
import axios from 'axios'

// Deployed Railway backend URL
const BASE_URL =
  'https://product-management-ecommerce-system-production.up.railway.app'

// Public product API
const API = axios.create({
  baseURL: `${BASE_URL}/products`,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Public order API (checkout)
const ORDER_API = axios.create({
  baseURL: `${BASE_URL}/api/orders`,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Admin authentication
let adminAuthorization = null

export const setAdminCredentials = (username, password) => {
  const encodedCredentials = btoa(`${username}:${password}`)
  adminAuthorization = `Basic ${encodedCredentials}`
}

export const clearAdminCredentials = () => {
  adminAuthorization = null
}

// Authenticated admin product API
const ADMIN_API = axios.create({
  baseURL: `${BASE_URL}/products`,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Authenticated admin order API
const ADMIN_ORDER_API = axios.create({
  baseURL: `${BASE_URL}/api/orders`,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Attach credentials only to admin API requests
const addAdminAuthorization = (config) => {
  if (!adminAuthorization) {
    return Promise.reject(
      new Error('Admin authentication required. Please log in.')
    )
  }

  config.headers = config.headers || {}
  config.headers.Authorization = adminAuthorization

  return config
}

ADMIN_API.interceptors.request.use(addAdminAuthorization)
ADMIN_ORDER_API.interceptors.request.use(addAdminAuthorization)

const productService = {
  // Public products
  getAllProducts: () => API.get(''),

  getCustomerCatalogue: () =>
    axios.get(`${BASE_URL}/customer/products`),

  getProductById: (id) => API.get(`/${id}`),

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

  // Admin product management
  addProduct: (product) => ADMIN_API.post('', product),

  updateProduct: (product) => ADMIN_API.put('', product),

  deleteProduct: (id) => ADMIN_API.delete(`/${id}`),

  // Admin dashboard
  getDashboardStats: () => ADMIN_API.get('/dashboard'),

  // Admin inventory
  getLowStockProducts: () => ADMIN_API.get('/low-stock'),

  getOutOfStockProducts: () => ADMIN_API.get('/out-of-stock'),

  getInStockProducts: () => ADMIN_API.get('/in-stock'),

  addStock: (id, quantity) =>
    ADMIN_API.patch(`/${id}/add-stock`, null, {
      params: { quantity }
    }),

  reduceStock: (id, quantity) =>
    ADMIN_API.patch(`/${id}/reduce-stock`, null, {
      params: { quantity }
    }),

  // Admin orders
  getAllOrders: () => ADMIN_ORDER_API.get(''),

  getOrderById: (id) => ADMIN_ORDER_API.get(`/${id}`),

  updateOrderStatus: (id, status) =>
    ADMIN_ORDER_API.patch(`/${id}/status`, { status }),

  // Public checkout
  checkout: (orderData) =>
    ORDER_API.post('/checkout', orderData)
}

export default productService
