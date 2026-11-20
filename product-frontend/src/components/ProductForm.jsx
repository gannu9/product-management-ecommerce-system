
function ProductForm({
  prodId,
  setProdId,
  prodName,
  setProdName,
  price,
  setPrice,
  quantity,
  setQuantity,
  handleAdd,
  handleUpdate,
  isEditing,
  clearForm
}) {
  return (
    <form onSubmit={isEditing ? handleUpdate : handleAdd}>
      <input
        type="number"
        placeholder="Product ID"
        value={prodId}
        onChange={(e) => setProdId(e.target.value)}
        disabled={isEditing}
        min="1"
        required
      />

      <input
        type="text"
        placeholder="Product Name"
        value={prodName}
        onChange={(e) => setProdName(e.target.value)}
        minLength="2"
        maxLength="100"
        required
      />

      <input
        type="number"
        placeholder="Price"
        value={price}
        onChange={(e) => setPrice(e.target.value)}
        min="0"
        required
      />

      <input
        type="number"
        placeholder="Quantity"
        value={quantity}
        onChange={(e) => setQuantity(e.target.value)}
        min="0"
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
  )
}

export default ProductForm
