
function ProductForm({
  prodId,
  setProdId,
  prodName,
  setProdName,
  price,
  setPrice,
  quantity,
  setQuantity,
  category,
  setCategory,
  description,
  setDescription,
  imageUrl,
  setImageUrl,
  features,
  setFeatures,
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

      <select
        value={category}
        onChange={(e) => setCategory(e.target.value)}
        required
      >
        <option value="">Select Category</option>
        <option value="Smartphones">Smartphones</option>
        <option value="Laptops">Laptops</option>
        <option value="Tablets">Tablets</option>
        <option value="Accessories">Accessories</option>
      </select>

      <textarea
        placeholder="Product Description"
        value={description}
        onChange={(e) => setDescription(e.target.value)}
        maxLength="1000"
      />

      <input
        type="text"
        placeholder="Product Image URL"
        value={imageUrl}
        onChange={(e) => setImageUrl(e.target.value)}
      />

      <textarea
        placeholder="Product Features (e.g. 128GB, OLED Display, 12MP Camera)"
        value={features}
        onChange={(e) => setFeatures(e.target.value)}
        maxLength="2000"
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
