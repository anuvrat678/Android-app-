package com.example.myapplication

import android.graphics.Bitmap

data class FoodProduct(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val price: Int
)

data class CartLine(
    val product: FoodProduct,
    val quantity: Int
)

object FoodStore {
    val categories = listOf("Pizza", "Burger", "Desserts", "Beverages")

    val products = listOf(
        FoodProduct("pizza_margherita", "Margherita Pizza", "Pizza", "Classic cheese and tomato", 199),
        FoodProduct("pizza_farmhouse", "Farmhouse Pizza", "Pizza", "Garden vegetables and cheese", 299),
        FoodProduct("pizza_paneer", "Paneer Tikka Pizza", "Pizza", "Tandoori paneer with capsicum", 279),
        FoodProduct("burger_classic", "Classic Veg Burger", "Burger", "Crispy patty, lettuce and sauce", 129),
        FoodProduct("burger_cheese", "Cheese Burger", "Burger", "Veg patty with melted cheese", 159),
        FoodProduct("burger_paneer", "Paneer Burger", "Burger", "Spiced paneer patty", 179),
        FoodProduct("dessert_brownie", "Chocolate Brownie", "Desserts", "Rich chocolate brownie", 99),
        FoodProduct("dessert_icecream", "Vanilla Ice Cream", "Desserts", "Creamy vanilla scoop", 79),
        FoodProduct("dessert_cheesecake", "Cheesecake", "Desserts", "Smooth and creamy slice", 149),
        FoodProduct("drink_coffee", "Cold Coffee", "Beverages", "Chilled coffee with milk", 99),
        FoodProduct("drink_lemonade", "Lemonade", "Beverages", "Fresh lemon cooler", 59),
        FoodProduct("drink_juice", "Orange Juice", "Beverages", "Refreshing orange juice", 89)
    )

    private val quantities = linkedMapOf<String, Int>()

    fun add(product: FoodProduct) {
        quantities[product.id] = (quantities[product.id] ?: 0) + 1
    }

    fun increase(productId: String) {
        val product = products.firstOrNull { it.id == productId } ?: return
        add(product)
    }

    fun decrease(productId: String) {
        val current = quantities[productId] ?: return
        if (current <= 1) quantities.remove(productId) else quantities[productId] = current - 1
    }

    fun remove(productId: String) {
        quantities.remove(productId)
    }

    fun cartLines(): List<CartLine> = quantities.mapNotNull { (id, quantity) ->
        products.firstOrNull { it.id == id }?.let { CartLine(it, quantity) }
    }

    fun itemCount(): Int = quantities.values.sum()

    fun total(): Int = cartLines().sumOf { it.product.price * it.quantity }

    fun clearCart() = quantities.clear()
}

object ProfilePhotoStore {
    var bitmap: Bitmap? = null
}
