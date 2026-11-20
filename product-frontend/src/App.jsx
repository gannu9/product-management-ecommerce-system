
import { useState, useEffect } from 'react'
import './App.css'

import ProductForm from './components/ProductForm'
import ProductList from './components/ProductList'
import Dashboard from './components/Dashboard'
import productService from './services/productService'

function App() {
  const [activeView, setActiveView] = useState('dashboard')

  // Product states
  const [products, setProducts] = useState([])

  const [prodId, setProdId] = useState('')
  const [prodName, setProdName] = useState('')
  const [price, setPrice] = useState('')
  const [quantity, setQuantity] = useState('')

  const [isEditing, setIsEditing] = useState(false)

  // Search state
  const [searchTerm, setSearchTerm] = useState('')

  // Fetch all products
  const fetchProducts = async () => {
    try {
      const response = await productService.getAllProducts()
      setProducts(response.data)
    } catch (error) {
      console.error('Error fetching products:', error)
      alert('Could not load products')
    }
  }

  useEffect(() => {
    fetchProducts()
  }, [])

  // Clear form
  const clearForm = () => {
    setProdId('')
    setProdName('')
    setPrice('')
    setQuantity('')
    setIsEditing(false)
  }

  // Add product
  const handleAdd = async (e) => {
    e.preventDefault()

    const newProduct = {
      prodId: Number(prodId),
      prodName: prodName.trim(),
      price: Number(price),
      quantity: Number(quantity)
    }

    try {
      await productService.addProduct(newProduct)

      alert('Product added successfully!')
      clearForm()
      await fetchProducts()
    } catch (error) {
      console.error('Error adding product:', error)
      alert(error.response?.data?.message || 'Error adding product')
    }
  }

  // Select product for editing
  const selectProduct = (product) => {
    setProdId(String(product.prodId))
    setProdName(product.prodName)
    setPrice(String(product.price))
    setQuantity(String(product.quantity))
    setIsEditing(true)
  }

  // Update product
  const handleUpdate = async (e) => {
    e.preventDefault()

    const updatedProduct = {
      prodId: Number(prodId),
      prodName: prodName.trim(),
      price: Number(price),
      quantity: Number(quantity)
    }

    try {
      await productService.updateProduct(updatedProduct)

      alert('Product updated successfully!')
      clearForm()
      await fetchProducts()
    } catch (error) {
      console.error('Error updating product:', error)
      alert(error.response?.data?.message || 'Error updating product')
    }
  }

  // Delete product
  const handleDelete = async (id) => {
    const confirmDelete = window.confirm(
      'Are you sure you want to delete this product?'
    )

    if (!confirmDelete) return

    try {
      await productService.deleteProduct(id)

      alert('Product deleted successfully!')

      if (Number(prodId) === Number(id)) {
        clearForm()
      }

      await fetchProducts()
    } catch (error) {
      console.error('Error deleting product:', error)
      alert(error.response?.data?.message || 'Error deleting product')
    }
  }


  // Add stock to a product
  const handleAddStock = async (id) => {
    const input = window.prompt('Enter quantity to add:')

    if (input === null) return

    const quantity = Number(input)

    if (!Number.isInteger(quantity) || quantity <= 0) {
      alert('Please enter a valid positive whole number.')
      return
    }

    try {
      await productService.addStock(id, quantity)
      alert('Stock added successfully!')
      await fetchProducts()
    } catch (error) {
      console.error('Error adding stock:', error)
      alert(error.response?.data?.message || 'Error adding stock')
    }
  }

  // Reduce stock from a product
  const handleReduceStock = async (id) => {
    const input = window.prompt('Enter quantity to reduce:')

    if (input === null) return

    const quantity = Number(input)

    if (!Number.isInteger(quantity) || quantity <= 0) {
      alert('Please enter a valid positive whole number.')
      return
    }

    try {
      await productService.reduceStock(id, quantity)
      alert('Stock reduced successfully!')
      await fetchProducts()
    } catch (error) {
      console.error('Error reducing stock:', error)
      alert(error.response?.data?.message || 'Error reducing stock')
    }
  }


  // Filter products based on search
  const filteredProducts = products.filter((product) => {
    const search = searchTerm.toLowerCase()

    return (
      String(product.prodId).includes(search) ||
      product.prodName.toLowerCase().includes(search) ||
      String(product.price).includes(search)
    )
  })

  return (
    <div className="app-layout">

      {/* Sidebar */}
      <aside className="sidebar">
        <div className="sidebar-brand">
          Product Admin
        </div>

        <p className="sidebar-label">MAIN MENU</p>

        <nav className="sidebar-nav">
          <button
            className={activeView === 'dashboard' ? 'active' : ''}
            onClick={() => setActiveView('dashboard')}
          >
            Dashboard
          </button>

          <button
            className={activeView === 'products' ? 'active' : ''}
            onClick={() => setActiveView('products')}
          >
            Products
          </button>
        </nav>
      </aside>

      {/* Main content */}
      <main className="main-content">

        {activeView === 'dashboard' && (
          <Dashboard />
        )}

        {activeView === 'products' && (
          <>
            <div className="dashboard-header">
              <div>
                <h1>Product Management</h1>
                <p>Manage your products and inventory.</p>
              </div>
            </div>

            {/* Search section */}
            <div className="dashboard-section">
              <div className="section-heading">
                <h2>Search Products</h2>
              </div>

              <input
                className="search-input"
                type="text"
                placeholder="Search by ID, name or price..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>

            {/* Product form */}
            <div className="dashboard-section">
              <div className="section-heading">
                <h2>
                  {isEditing ? 'Update Product' : 'Add New Product'}
                </h2>
              </div>

              <ProductForm
                prodId={prodId}
                setProdId={setProdId}
                prodName={prodName}
                setProdName={setProdName}
                price={price}
                setPrice={setPrice}
                quantity={quantity}
                setQuantity={setQuantity}
                handleAdd={handleAdd}
                handleUpdate={handleUpdate}
                isEditing={isEditing}
                clearForm={clearForm}
              />
            </div>

            {/* Product list */}
            <div className="dashboard-section">
              <div className="section-heading">
                <h2>All Products ({filteredProducts.length})</h2>
              </div>

              <div className="table-wrapper">
              <ProductList
  products={filteredProducts}
  selectProduct={selectProduct}
  handleDelete={handleDelete}
  handleAddStock={handleAddStock}
  handleReduceStock={handleReduceStock}
/>
              </div>
            </div>
          </>
        )}

      </main>
    </div>
  )
}

export default App
