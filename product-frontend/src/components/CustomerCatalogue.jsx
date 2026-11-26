
import { useEffect, useState } from 'react'
import productService from '../services/productService'
import Checkout from './Checkout'

function CustomerCatalogue() {
  const [products, setProducts] = useState([])
  const [searchTerm, setSearchTerm] = useState('')
  const [selectedCategory, setSelectedCategory] = useState('All')
  const [selectedProduct, setSelectedProduct] = useState(null)
  const [cart, setCart] = useState([])
  const [showCart, setShowCart] = useState(false)
  const [showCheckout, setShowCheckout] = useState(false)
  const [orderConfirmation, setOrderConfirmation] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    fetchProducts()
  }, [])

  const fetchProducts = async () => {
    try {
      setLoading(true)

      const response = await productService.getCustomerCatalogue()

      setProducts(response.data)
      setError('')
    } catch (err) {
      console.error('Failed to fetch customer products:', err)
      setError('Unable to load products. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const categories = ['All', 'Smartphones', 'Laptops', 'Tablets']

  // Add product to cart
  const handleAddToCart = (product) => {
    setCart((previousCart) => {
      const existingItem = previousCart.find(
        (item) => item.prodId === product.prodId
      )

      if (existingItem) {
        if (existingItem.cartQuantity >= product.quantity) {
          alert('Maximum available stock reached!')
          return previousCart
        }

        return previousCart.map((item) =>
          item.prodId === product.prodId
            ? {
                ...item,
                cartQuantity: item.cartQuantity + 1,
              }
            : item
        )
      }

      if (product.quantity <= 0) {
        alert('This product is out of stock!')
        return previousCart
      }

      return [
        ...previousCart,
        { ...product, cartQuantity: 1 },
      ]
    })
  }

  // Increase cart quantity
  const increaseQuantity = (productId) => {
    setCart((previousCart) =>
      previousCart.map((item) => {
        if (item.prodId !== productId) return item

        if (item.cartQuantity >= item.quantity) {
          alert('Maximum available stock reached!')
          return item
        }

        return {
          ...item,
          cartQuantity: item.cartQuantity + 1,
        }
      })
    )
  }

  // Decrease cart quantity
  const decreaseQuantity = (productId) => {
    setCart((previousCart) =>
      previousCart
        .map((item) =>
          item.prodId === productId
            ? {
                ...item,
                cartQuantity: item.cartQuantity - 1,
              }
            : item
        )
        .filter((item) => item.cartQuantity > 0)
    )
  }

  // Remove product from cart
  const removeFromCart = (productId) => {
    setCart((previousCart) =>
      previousCart.filter((item) => item.prodId !== productId)
    )
  }

  // Calculate cart values
  const cartCount = cart.reduce(
    (total, item) => total + item.cartQuantity,
    0
  )

  const cartTotal = cart.reduce(
    (total, item) => total + item.price * item.cartQuantity,
    0
  )

  // Handle successful order
  const handleOrderSuccess = (order) => {
    setCart([])
    setShowCart(false)
    setShowCheckout(false)
    setOrderConfirmation(order)

    fetchProducts()
  }

  // Search and category filtering
  const filteredProducts = products.filter((product) => {
    const matchesSearch = product.prodName
      .toLowerCase()
      .includes(searchTerm.toLowerCase())

    const matchesCategory =
      selectedCategory === 'All' ||
      product.category === selectedCategory

    return matchesSearch && matchesCategory
  })

  // Order confirmation screen
  if (orderConfirmation) {
    return (
      <div className="customer-catalogue">
        <div className="shopping-cart-section">
          <h2>Order Placed Successfully!</h2>

          <p>
            Your order ID is:{' '}
            <strong>{orderConfirmation.orderId}</strong>
          </p>

          <p>
            Total: ₹
            {Number(orderConfirmation.totalAmount).toLocaleString(
              'en-IN'
            )}
          </p>

          <p>Payment: Cash on Delivery</p>

          <p>Status: {orderConfirmation.orderStatus}</p>

          <button
            type="button"
            onClick={() => setOrderConfirmation(null)}
          >
            Continue Shopping
          </button>
        </div>
      </div>
    )
  }

  // Checkout screen
  if (showCheckout) {
    return (
      <Checkout
        cartItems={cart.map((item) => ({
          ...item,
          quantity: item.cartQuantity,
        }))}
        onBack={() => setShowCheckout(false)}
        onOrderSuccess={handleOrderSuccess}
      />
    )
  }

  if (loading) return <p>Loading products...</p>
  if (error) return <p>{error}</p>

  return (
    <div className="customer-catalogue">
      {/* Catalogue Header */}
      <div className="catalogue-header">
        <h1>Shop Our Products</h1>
        <p>
          Explore our available products and find what you need.
        </p>

        <button
          className="cart-toggle-button"
          type="button"
          onClick={() => setShowCart(!showCart)}
        >
          🛒 Shopping Cart ({cartCount})
        </button>
      </div>

      {/* Shopping Cart */}
      {showCart && (
        <div className="shopping-cart-section">
          <div className="cart-heading">
            <h2>Your Shopping Cart</h2>

            <button
              type="button"
              className="cart-close-button"
              onClick={() => setShowCart(false)}
            >
              Close
            </button>
          </div>

          {cart.length === 0 ? (
            <p className="empty-cart">
              Your cart is empty. Start shopping!
            </p>
          ) : (
            <>
              <div className="cart-items">
                {cart.map((item) => (
                  <div className="cart-item" key={item.prodId}>
                    <div className="cart-item-info">
                      <h3>{item.prodName}</h3>

                      <p>
                        ₹
                        {Number(item.price).toLocaleString('en-IN')}
                        {' '}each
                      </p>

                      <p>
                        Subtotal: ₹
                        {(
                          item.price * item.cartQuantity
                        ).toLocaleString('en-IN')}
                      </p>
                    </div>

                    <div className="cart-item-controls">
                      <button
                        type="button"
                        onClick={() =>
                          decreaseQuantity(item.prodId)
                        }
                      >
                        −
                      </button>

                      <span>{item.cartQuantity}</span>

                      <button
                        type="button"
                        onClick={() =>
                          increaseQuantity(item.prodId)
                        }
                      >
                        +
                      </button>

                      <button
                        type="button"
                        className="remove-cart-button"
                        onClick={() =>
                          removeFromCart(item.prodId)
                        }
                      >
                        Remove
                      </button>
                    </div>
                  </div>
                ))}
              </div>

              <div className="cart-summary">
                <h3>
                  Total: ₹
                  {cartTotal.toLocaleString('en-IN')}
                </h3>

                <button
                  type="button"
                  className="clear-cart-button"
                  onClick={() => setCart([])}
                >
                  Clear Cart
                </button>

                <button
                  type="button"
                  disabled={cart.length === 0}
                  onClick={() => setShowCheckout(true)}
                >
                  Proceed to Checkout
                </button>
              </div>
            </>
          )}
        </div>
      )}

      {/* Search */}
      <div className="catalogue-toolbar">
        <input
          className="catalogue-search"
          type="text"
          placeholder="Search products by name..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />

        <span className="catalogue-count">
          Showing {filteredProducts.length} of {products.length}{' '}
          products
        </span>
      </div>

      {/* Category Filters */}
      <div className="category-filters">
        {categories.map((category) => (
          <button
            key={category}
            type="button"
            className={
              selectedCategory === category
                ? 'category-filter active'
                : 'category-filter'
            }
            onClick={() => setSelectedCategory(category)}
          >
            {category === 'All' ? 'All Products' : category}
          </button>
        ))}
      </div>

      {/* Product Cards */}
      {filteredProducts.length === 0 ? (
        <p>No matching products found.</p>
      ) : (
        <div className="product-grid">
          {filteredProducts.map((product) => {
            const cartItem = cart.find(
              (item) => item.prodId === product.prodId
            )

            return (
              <div
                className="customer-product-card"
                key={product.prodId}
              >
                <div className="customer-product-image">
                  {product.imageUrl ? (
                    <img
                      src={product.imageUrl}
                      alt={product.prodName}
                      onError={(e) => {
                        e.currentTarget.style.display = 'none'
                      }}
                    />
                  ) : (
                    <span role="img" aria-label="Product placeholder">
                      📦
                    </span>
                  )}
                </div>

                <div className="customer-product-info">
                  {product.category && (
                    <p className="customer-product-category">
                      {product.category}
                    </p>
                  )}

                  <h3>{product.prodName}</h3>

                  <p className="customer-product-price">
                    ₹
                    {Number(product.price).toLocaleString('en-IN')}
                  </p>

                  <p className="customer-stock">
                    {product.quantity} units available
                  </p>

                  {product.description && (
                    <p className="customer-product-description">
                      {product.description}
                    </p>
                  )}

                  <div className="customer-product-actions">
                    <button
                      type="button"
                      onClick={() => setSelectedProduct(product)}
                    >
                      View Details
                    </button>

                    <button
                      type="button"
                      className="add-to-cart-button"
                      disabled={product.quantity <= 0}
                      onClick={() => handleAddToCart(product)}
                    >
                      {cartItem
                        ? `Add More (${cartItem.cartQuantity})`
                        : 'Add to Cart'}
                    </button>
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      )}

      {/* Product Details */}
      {selectedProduct && (
        <div className="dashboard-section product-details">
          <h2>{selectedProduct.prodName}</h2>

          {selectedProduct.imageUrl && (
            <img
              src={selectedProduct.imageUrl}
              alt={selectedProduct.prodName}
              className="product-detail-image"
            />
          )}

          <p>Product ID: {selectedProduct.prodId}</p>

          <p>Category: {selectedProduct.category}</p>

          <p>
            Price: ₹
            {Number(selectedProduct.price).toLocaleString('en-IN')}
          </p>

          <p>Available stock: {selectedProduct.quantity}</p>

          {selectedProduct.description && (
            <div>
              <h3>Description</h3>
              <p>{selectedProduct.description}</p>
            </div>
          )}

          {selectedProduct.features && (
            <div>
              <h3>Product Features</h3>
              <p>{selectedProduct.features}</p>
            </div>
          )}

          <button
            type="button"
            onClick={() => setSelectedProduct(null)}
          >
            Close Details
          </button>
        </div>
      )}
    </div>
  )
}

export default CustomerCatalogue

