
function ProductForm({
    prodId,
    setProdId,
    prodName,
    setProdName,
    price,
    setPrice,
    handleAdd,
    handleUpdate,
    isEditing,
    clearForm
  }) {
    return (
      <div>
        <h2>{isEditing ? 'Update Product' : 'Add Product'}</h2>
  
        <form onSubmit={isEditing ? handleUpdate : handleAdd}>
          <input
            type="number"
            placeholder="Product ID"
            value={prodId}
            onChange={(e) => setProdId(e.target.value)}
            disabled={isEditing}
            required
          />
  
          <input
            type="text"
            placeholder="Product Name"
            value={prodName}
            onChange={(e) => setProdName(e.target.value)}
            required
          />
  
          <input
            type="number"
            placeholder="Price"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            required
          />
  
          <button type="submit">
            {isEditing ? 'Update Product' : 'Add Product'}
          </button>
  
          {isEditing && (
            <button type="button" onClick={clearForm}>
              Cancel
            </button>
          )}
        </form>
      </div>
    )
  }
  
  export default ProductForm
  