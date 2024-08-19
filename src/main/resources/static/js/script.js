document.addEventListener('DOMContentLoaded', function() {
    const addProductBtn = document.getElementById('addProductBtn');
    const productDetails = document.getElementById('productDetails');
    const productInfo = document.getElementById('productInfo');
    const editProductBtn = document.getElementById('editProductBtn');
    const deleteProductBtn = document.getElementById('deleteProductBtn');

    addProductBtn.addEventListener('click', function() {
        alert('Adicionar produto');
    });

    editProductBtn.addEventListener('click', function() {
        alert('Editar produto');
    });

    deleteProductBtn.addEventListener('click', function() {
        alert('Excluir produto');
    });

    function loadProductDetails(productId) {
        productInfo.innerHTML = `<p>Detalhes do produto ${productId}</p>`;
        productDetails.classList.remove('d-none');
    }

    loadProductDetails(1);
});