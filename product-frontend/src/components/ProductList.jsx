
function ProductList({ products, selectProduct, handleDelete }) {
    return (
      <div>
        <h2>Product List</h2>
  
        <table border="1" cellPadding="10">
          <thead>
            <tr>
              <th>Product ID</th>
              <th>Product Name</th>
              <th>Price</th>
              <th>Actions</th>
            </tr>
          </thead>
  
          <tbody>
            {products.map((product) => (
              <tr key={product.prodId}>
                <td>{product.prodId}</td>
                <td>{product.prodName}</td>
                <td>{product.price}</td>
  
                <td>
                  <button onClick={() => selectProduct(product)}>
                    Edit
                  </button>
  
                  <button onClick={() => handleDelete(product.prodId)}>
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    )
  }
  
  export default ProductList
  