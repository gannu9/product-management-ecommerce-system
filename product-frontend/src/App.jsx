
import { useState, useEffect } from 'react'
import './App.css'

import ProductForm from './components/ProductForm'
import ProductList from './components/ProductList'

function App() {
  // Product states
  const [products, setProducts] = useState([])

  const [prodId, setProdId] = useState('')
  const [prodName, setProdName] = useState('')
  const [price, setPrice] = useState('')

  const [isEditing, setIsEditing] = useState(false)

  // Search state
  const [searchTerm, setSearchTerm] = useState('')

  const API_URL = 'http://localhost:8080/products'

  // Fetch all products
  const fetchProducts = async () => {
    try {
      const response = await fetch(API_URL)

      if (!response.ok) {
        throw new Error('Failed to fetch products')
      }

      const data = await response.json()
      setProducts(data)

    } catch (error) {
      console.error(error)
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
    setIsEditing(false)
  }

  // Add product
  const handleAdd = async (e) => {
    e.preventDefault()

    const newProduct = {
      prodId: Number(prodId),
      prodName,
      price: Number(price)
    }

    try {
      const response = await fetch(API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newProduct)
      })

      if (!response.ok) {
        throw new Error('Failed to add product')
      }

      alert('Product added successfully!')
      clearForm()
      await fetchProducts()

    } catch (error) {
      console.error(error)
      alert('Error adding product')
    }
  }

  // Select product for editing
  const selectProduct = (product) => {
    setProdId(product.prodId)
    setProdName(product.prodName)
    setPrice(product.price)
    setIsEditing(true)
  }

  // Update product
  const handleUpdate = async (e) => {
    e.preventDefault()

    const updatedProduct = {
      prodId: Number(prodId),
      prodName,
      price: Number(price)
    }

    try {
      const response = await fetch(API_URL, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(updatedProduct)
      })

      if (!response.ok) {
        throw new Error('Failed to update product')
      }

      alert('Product updated successfully!')
      clearForm()
      await fetchProducts()

    } catch (error) {
      console.error(error)
      alert('Error updating product')
    }
  }

  // Delete product
  const handleDelete = async (id) => {
    const confirmDelete = window.confirm(
      'Are you sure you want to delete this product?'
    )

    if (!confirmDelete) return

    try {
      const response = await fetch(`${API_URL}/${id}`, {
        method: 'DELETE'
      })

      if (!response.ok) {
        throw new Error('Failed to delete product')
      }

      alert('Product deleted successfully!')

      if (Number(prodId) === id) {
        clearForm()
      }

      await fetchProducts()

    } catch (error) {
      console.error(error)
      alert('Error deleting product')
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
    <div>
      <h1>Product Management System</h1>

      {/* Search section */}
      <input
        type="text"
        placeholder="Search by ID, name or price..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
      />

      {/* Product form */}
      <ProductForm
        prodId={prodId}
        setProdId={setProdId}
        prodName={prodName}
        setProdName={setProdName}
        price={price}
        setPrice={setPrice}
        handleAdd={handleAdd}
        handleUpdate={handleUpdate}
        isEditing={isEditing}
        clearForm={clearForm}
      />

      {/* Product list */}
      <ProductList
        products={filteredProducts}
        selectProduct={selectProduct}
        handleDelete={handleDelete}
      />
    </div>
  )
}

export default App
