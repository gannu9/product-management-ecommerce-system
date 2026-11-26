
import { useState } from 'react'

const API_URL = 'https://product-management-ecommerce-system-production.up.railway.app/api/orders/checkout'

function Checkout({ cartItems, onBack, onOrderSuccess }) {
  const [form, setForm] = useState({
    customerName: '',
    email: '',
    phone: '',
    deliveryAddress: '',
  })

  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const total = cartItems.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  )

  const handleChange = (event) => {
    const { name, value } = event.target

    setForm((previousForm) => ({
      ...previousForm,
      [name]: value,
    }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')

    if (cartItems.length === 0) {
      setError('Your cart is empty.')
      return
    }

    const orderRequest = {
      ...form,
      items: cartItems.map((item) => ({
        productId: item.prodId,
        quantity: item.quantity,
      })),
    }

    try {
      setLoading(true)

      const response = await fetch(API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(orderRequest),
      })

      const result = await response.json()

      if (!response.ok) {
        throw new Error(
          result.message || 'Unable to place your order.'
        )
      }

      onOrderSuccess(result)
    } catch (err) {
      setError(err.message || 'Something went wrong.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="shopping-cart-section checkout-container">
      <h2>Checkout</h2>

      <form onSubmit={handleSubmit}>
        <h3>Delivery Details</h3>

        <input
          name="customerName"
          placeholder="Full name"
          value={form.customerName}
          onChange={handleChange}
          minLength={2}
          maxLength={100}
          required
        />

        <input
          name="email"
          type="email"
          placeholder="Email address"
          value={form.email}
          onChange={handleChange}
          required
        />

        <input
          name="phone"
          type="tel"
          placeholder="10-digit mobile number"
          value={form.phone}
          onChange={handleChange}
          pattern="[0-9]{10}"
          maxLength={10}
          required
        />

        <textarea
          name="deliveryAddress"
          placeholder="Complete delivery address"
          value={form.deliveryAddress}
          onChange={handleChange}
          minLength={10}
          maxLength={500}
          required
        />

        <h3>Order Summary</h3>

        {cartItems.map((item) => (
          <div key={item.prodId}>
            <p>
              {item.prodName} × {item.quantity}
            </p>

            <p>
              ₹{(item.price * item.quantity).toLocaleString('en-IN')}
            </p>
          </div>
        ))}

        <h3>
          Total: ₹{total.toLocaleString('en-IN')}
        </h3>

        <p>Payment Method: Cash on Delivery</p>

        {error && (
          <p className="checkout-error">{error}</p>
        )}

        <button
          type="button"
          onClick={onBack}
          disabled={loading}
        >
          Back to Cart
        </button>

        <button
          type="submit"
          disabled={loading || cartItems.length === 0}
        >
          {loading ? 'Placing Order...' : 'Place COD Order'}
        </button>
      </form>
    </div>
  )
}

export default Checkout
