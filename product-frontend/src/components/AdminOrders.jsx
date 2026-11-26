
import { useEffect, useState } from 'react'
import productService from "../services/productService"

function AdminOrders() {
  const [orders, setOrders] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [updatingOrderId, setUpdatingOrderId] = useState(null)

  // Load all orders
  const fetchOrders = async () => {
    try {
      setLoading(true)
      setError('')

      const response = await productService.getAllOrders()
      setOrders(response.data)
    } catch (err) {
      console.error('Error loading orders:', err)
      setError(
        err.response?.data?.message ||
          'Unable to load orders. Please try again.'
      )
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchOrders()
  }, [])

  // Format currency
  const formatCurrency = (value) =>
    `₹${Number(value).toLocaleString('en-IN', {
      maximumFractionDigits: 2
    })}`

  // Format order date
  const formatDate = (dateValue) => {
    if (!dateValue) return 'N/A'

    return new Date(dateValue).toLocaleString('en-IN', {
      dateStyle: 'medium',
      timeStyle: 'short'
    })
  }

  // Update order status
  const handleStatusChange = async (orderId, newStatus, currentStatus) => {
    if (newStatus === currentStatus) return

    if (newStatus === 'CANCELLED') {
      const confirmed = window.confirm(
        `Are you sure you want to cancel order #${orderId}?\n\n` +
          'The ordered products will be returned to inventory.'
      )

      if (!confirmed) return
    }

    try {
      setUpdatingOrderId(orderId)
      setError('')

      await productService.updateOrderStatus(orderId, newStatus)

      await fetchOrders()
    } catch (err) {
      console.error('Error updating order status:', err)

      setError(
        err.response?.data?.message ||
          'Unable to update order status. Please try again.'
      )
    } finally {
      setUpdatingOrderId(null)
    }
  }

  // Status label styling
  const getStatusClass = (status) => {
    switch (status) {
      case 'PLACED':
        return 'order-status placed'
      case 'CONFIRMED':
        return 'order-status confirmed'
      case 'SHIPPED':
        return 'order-status shipped'
      case 'DELIVERED':
        return 'order-status delivered'
      case 'CANCELLED':
        return 'order-status cancelled'
      default:
        return 'order-status'
    }
  }

  if (loading) {
    return (
      <section className="admin-orders">
        <p>Loading orders...</p>
      </section>
    )
  }

  return (
    <section className="admin-orders">
      <div className="dashboard-header">
        <div>
          <h1>Order Management</h1>
          <p>View customer orders and manage their status.</p>
        </div>

        <button
          type="button"
          className="refresh-orders-button"
          onClick={fetchOrders}
        >
          Refresh Orders
        </button>
      </div>

      {error && (
        <div className="orders-error" role="alert">
          {error}
        </div>
      )}

      <div className="orders-summary">
        <div className="stat-card">
          <h3>Total Orders</h3>
          <strong>{orders.length}</strong>
        </div>

        <div className="stat-card">
          <h3>Placed</h3>
          <strong>
            {orders.filter((order) => order.orderStatus === 'PLACED').length}
          </strong>
        </div>

        <div className="stat-card">
          <h3>Confirmed</h3>
          <strong>
            {orders.filter((order) => order.orderStatus === 'CONFIRMED').length}
          </strong>
        </div>

        <div className="stat-card">
          <h3>Cancelled</h3>
          <strong>
            {orders.filter((order) => order.orderStatus === 'CANCELLED').length}
          </strong>
        </div>
      </div>

      {orders.length === 0 ? (
        <div className="dashboard-section">
          <p>No orders have been placed yet.</p>
        </div>
      ) : (
        <div className="orders-list">
          {orders.map((order) => {
            const isTerminal =
              order.orderStatus === 'CANCELLED' ||
              order.orderStatus === 'DELIVERED'

            return (
              <article className="order-card" key={order.orderId}>
                <div className="order-card-header">
                  <div>
                    <h2>Order #{order.orderId}</h2>
                    <p>{formatDate(order.createdAt)}</p>
                  </div>

                  <span className={getStatusClass(order.orderStatus)}>
                    {order.orderStatus}
                  </span>
                </div>

                <div className="order-customer-details">
                  <h3>Customer Details</h3>

                  <p>
                    <strong>Name:</strong> {order.customerName}
                  </p>
                  <p>
                    <strong>Email:</strong> {order.email}
                  </p>
                  <p>
                    <strong>Phone:</strong> {order.phone}
                  </p>
                  <p>
                    <strong>Delivery Address:</strong> {order.deliveryAddress}
                  </p>
                  <p>
                    <strong>Payment:</strong> {order.paymentMethod}
                  </p>
                </div>

                <div className="order-items">
                  <h3>Order Items</h3>

                  <div className="table-wrapper">
                    <table>
                      <thead>
                        <tr>
                          <th>Product</th>
                          <th>Product ID</th>
                          <th>Quantity</th>
                          <th>Unit Price</th>
                          <th>Subtotal</th>
                        </tr>
                      </thead>

                      <tbody>
                        {order.items.map((item, index) => (
                          <tr key={`${item.productId}-${index}`}>
                            <td>{item.productName}</td>
                            <td>{item.productId}</td>
                            <td>{item.quantity}</td>
                            <td>{formatCurrency(item.unitPrice)}</td>
                            <td>{formatCurrency(item.subtotal)}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                </div>

                <div className="order-card-footer">
                  <div>
                    <span>Total Amount</span>
                    <strong>{formatCurrency(order.totalAmount)}</strong>
                  </div>

                  <div className="order-status-control">
                    <label htmlFor={`status-${order.orderId}`}>
                      Update Status
                    </label>

                    <select
                      id={`status-${order.orderId}`}
                      value={order.orderStatus}
                      disabled={
                        isTerminal || updatingOrderId === order.orderId
                      }
                      onChange={(event) =>
                        handleStatusChange(
                          order.orderId,
                          event.target.value,
                          order.orderStatus
                        )
                      }
                    >
                      <option value="PLACED">Placed</option>
                      <option value="CONFIRMED">Confirmed</option>
                      <option value="SHIPPED">Shipped</option>
                      <option value="DELIVERED">Delivered</option>
                      <option value="CANCELLED">Cancelled</option>
                    </select>

                    {updatingOrderId === order.orderId && (
                      <small>Updating...</small>
                    )}

                    {isTerminal && (
                      <small>
                        {order.orderStatus === 'CANCELLED'
                          ? 'Cancelled orders cannot be changed.'
                          : 'Delivered orders cannot be changed.'}
                      </small>
                    )}
                  </div>
                </div>
              </article>
            )
          })}
        </div>
      )}
    </section>
  )
}

export default AdminOrders
