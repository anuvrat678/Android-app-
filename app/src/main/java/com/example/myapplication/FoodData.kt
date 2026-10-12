
package com.example.myapplication

data class FoodItem(
    val name: String,
    val category: String,
    val price: Int
)

data class CartEntry(
    val product: FoodItem,
    val quantity: Int
)

object FoodRepository {

    val categories = listOf(
        "Pizza",
        "Burger",
        "Desserts",
        "Beverages"
    )

    val products = listOf(
        FoodItem("Margherita Pizza", "Pizza", 199),
        FoodItem("Farmhouse Pizza", "Pizza", 299),
        FoodItem("Cheese Pizza", "Pizza", 249),

        FoodItem("Classic Veg Burger", "Burger", 129),
        FoodItem("Cheese Burger", "Burger", 159),
        FoodItem("Paneer Burger", "Burger", 179),

        FoodItem("Chocolate Brownie", "Desserts", 99),
        FoodItem("Vanilla Ice Cream", "Desserts", 79),
        FoodItem("Cheesecake", "Desserts", 149),

        FoodItem("Cold Coffee", "Beverages", 99),
        FoodItem("Lemonade", "Beverages", 59),
        FoodItem("Soft Drink", "Beverages", 49)
    )
}

object CartManager {

    private val cart = linkedMapOf<FoodItem, Int>()

    fun add(product: FoodItem) {
        cart[product] = (cart[product] ?: 0) + 1
    }

    fun increase(product: FoodItem) {
        add(product)
    }

    fun decrease(product: FoodItem) {
        val quantity = cart[product] ?: return

        if (quantity <= 1) {
            cart.remove(product)
        } else {
            cart[product] = quantity - 1
        }
    }

    fun remove(product: FoodItem) {
        cart.remove(product)
    }

    fun getEntries(): List<CartEntry> {
        return cart.map { (product, quantity) ->
            CartEntry(product, quantity)
        }
    }

    fun total(): Int {
        return cart.entries.sumOf { (product, quantity) ->
            product.price * quantity
        }
    }

    fun clear() {
        cart.clear()
    }
}
