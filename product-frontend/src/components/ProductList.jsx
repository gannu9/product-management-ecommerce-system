
function ProductList({
  products,
  selectProduct,
  handleDelete,
  handleAddStock,
  handleReduceStock
}) {
  return (
    <div className="table-wrapper">
      <table>
        <thead>
          <tr>
            <th>Product ID</th>
            <th>Product Name</th>
            <th>Price</th>
            <th>Quantity</th>
            <th>Actions</th>
          </tr>
        </thead>

        <tbody>
          {products.length === 0 ? (
            <tr>
              <td colSpan="5">No products found.</td>
            </tr>
          ) : (
            products.map((product) => (
              <tr key={product.prodId}>
                <td>{product.prodId}</td>
                <td>{product.prodName}</td>
                <td>
                  ₹{Number(product.price).toLocaleString('en-IN')}
                </td>
                <td>{product.quantity}</td>

                <td>
  <div className="action-buttons">
    <button onClick={() => selectProduct(product)}>
      Edit
    </button>

    <button onClick={() => handleDelete(product.prodId)}>
      Delete
    </button>

    <button onClick={() => handleAddStock(product.prodId)}>
      Add Stock
    </button>

    <button
      onClick={() => handleReduceStock(product.prodId)}
      disabled={product.quantity === 0}
    >
      Reduce Stock
    </button>
  </div>
</td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  )
}

export default ProductList

