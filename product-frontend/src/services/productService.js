
import axios from 'axios'

const API = axios.create({
  baseURL: 'http://localhost:8080/products',
  headers: {
    'Content-Type': 'application/json'
  }
})

const productService = {
  // Get all products
  getAllProducts: () => API.get(''),

  // Get product by ID
  getProductById: (id) => API.get(`/${id}`),

  // Add product
  addProduct: (product) => API.post('', product),

  // Update product
  updateProduct: (product) => API.put('', product),

  // Delete product
  deleteProduct: (id) => API.delete(`/${id}`),

  // Search products
  searchProducts: (keyword) =>
    API.get('/search', { params: { keyword } }),

  // Pagination
  getProductsPage: (page = 0, size = 10) =>
    API.get('/page', { params: { page, size } }),

  // Sorting
  getSortedProducts: (page = 0, size = 10, sortBy = 'prodId', direction = 'asc') =>
    API.get('/sort', {
      params: { page, size, sortBy, direction }
    }),

  // Dashboard statistics
  getDashboardStats: () => API.get('/dashboard'),

  // Inventory categories
  getLowStockProducts: () => API.get('/low-stock'),
  getOutOfStockProducts: () => API.get('/out-of-stock'),
  getInStockProducts: () => API.get('/in-stock'),

  // Inventory adjustments
  addStock: (id, quantity) =>
    API.patch(`/${id}/add-stock`, null, { params: { quantity } }),

  reduceStock: (id, quantity) =>
    API.patch(`/${id}/reduce-stock`, null, { params: { quantity } })
}

export default productService
