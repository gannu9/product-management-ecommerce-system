
import { useEffect, useState } from 'react'
import productService from '../services/productService'

function Dashboard() {
  const [stats, setStats] = useState(null)
  const [lowStock, setLowStock] = useState([])
  const [outOfStock, setOutOfStock] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setLoading(true)
        setError('')

        const [statsResponse, lowStockResponse, outOfStockResponse] =
          await Promise.all([
            productService.getDashboardStats(),
            productService.getLowStockProducts(),
            productService.getOutOfStockProducts()
          ])

        setStats(statsResponse.data)
        setLowStock(lowStockResponse.data)
        setOutOfStock(outOfStockResponse.data)
      } catch (err) {
        console.error('Dashboard loading error:', err)
        setError('Unable to load dashboard data. Please try again.')
      } finally {
        setLoading(false)
      }
    }

    fetchDashboardData()
  }, [])

  if (loading) {
    return <p>Loading dashboard...</p>
  }

  if (error) {
    return <p role="alert">{error}</p>
  }

  if (!stats) {
    return <p>No dashboard data available.</p>
  }

  const formatCurrency = (value) =>
    `₹${Number(value).toLocaleString('en-IN', {
      maximumFractionDigits: 2
    })}`

  return (
    <section>
      <div className="dashboard-header">
        <div>
          <h1>Dashboard Overview</h1>
          <p>Monitor your products and inventory performance.</p>
        </div>
      </div>

      {/* Summary cards */}
      <div className="stats-grid">
        <div className="stat-card">
          <h3>Total Products</h3>
          <strong>{stats.totalProducts}</strong>
          <span>Products in inventory</span>
        </div>

        <div className="stat-card">
          <h3>Total Stock Units</h3>
          <strong>{stats.totalStockUnits}</strong>
          <span>Units currently available</span>
        </div>

        <div className="stat-card">
          <h3>Inventory Value</h3>
          <strong>{formatCurrency(stats.totalInventoryValue)}</strong>
          <span>Total inventory worth</span>
        </div>

        <div className="stat-card">
          <h3>Average Product Price</h3>
          <strong>{formatCurrency(stats.averageProductPrice)}</strong>
          <span>Average price per product</span>
        </div>
      </div>

      {/* Stock alerts */}
      <div className="dashboard-section">
        <div className="section-heading">
          <h2>Inventory Alerts</h2>
        </div>

        <h3>Out of Stock ({outOfStock.length})</h3>

        {outOfStock.length === 0 ? (
          <p>All products have available stock.</p>
        ) : (
          <ul>
            {outOfStock.map((product) => (
              <li key={product.prodId}>
                {product.prodName} — Product ID: {product.prodId}
              </li>
            ))}
          </ul>
        )}

        <h3>Low Stock ({lowStock.length})</h3>

        {lowStock.length === 0 ? (
          <p>No low-stock products.</p>
        ) : (
          <ul>
            {lowStock.map((product) => (
              <li key={product.prodId}>
                {product.prodName} — {product.quantity} units remaining
              </li>
            ))}
          </ul>
        )}
      </div>

      {/* Highest priced products */}
      <div className="dashboard-section">
        <div className="section-heading">
          <h2>Highest-Priced Products</h2>
        </div>

        {stats.highestPricedProducts?.length > 0 ? (
          <ul>
            {stats.highestPricedProducts.map((product) => (
              <li key={product.prodId}>
                {product.prodName} — {formatCurrency(product.price)}
              </li>
            ))}
          </ul>
        ) : (
          <p>No product data available.</p>
        )}
      </div>
    </section>
  )
}

export default Dashboard
