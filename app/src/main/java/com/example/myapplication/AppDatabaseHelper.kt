package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class User(
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String = "",
    val address: String = "",
    val contact: String = "",
    val dob: String = ""
)

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "yummcart.db"
        private const val DATABASE_VERSION = 1

        // Users Table
        private const val TABLE_USERS = "users"
        private const val USER_COLUMN_ID = "id"
        private const val USER_COLUMN_NAME = "name"
        private const val USER_COLUMN_EMAIL = "email"
        private const val USER_COLUMN_PASSWORD = "password"
        private const val USER_COLUMN_ADDRESS = "address"
        private const val USER_COLUMN_CONTACT = "contact"
        private const val USER_COLUMN_DOB = "dob"

        // Foods Table
        private const val TABLE_FOODS = "foods"
        private const val FOOD_COLUMN_ID = "id"
        private const val FOOD_COLUMN_NAME = "name"
        private const val FOOD_COLUMN_CATEGORY = "category"
        private const val FOOD_COLUMN_DESCRIPTION = "description"
        private const val FOOD_COLUMN_PRICE = "price"

        // Cart Table
        private const val TABLE_CART = "cart"
        private const val CART_COLUMN_ID = "id"
        private const val CART_COLUMN_FOOD_ID = "food_id"
        private const val CART_COLUMN_FOOD_NAME = "food_name"
        private const val CART_COLUMN_FOOD_CATEGORY = "food_category"
        private const val CART_COLUMN_FOOD_DESCRIPTION = "food_description"
        private const val CART_COLUMN_FOOD_PRICE = "food_price"
        private const val CART_COLUMN_QUANTITY = "quantity"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createUsersTable = "CREATE TABLE $TABLE_USERS (" +
                "$USER_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$USER_COLUMN_NAME TEXT, " +
                "$USER_COLUMN_EMAIL TEXT UNIQUE, " +
                "$USER_COLUMN_PASSWORD TEXT, " +
                "$USER_COLUMN_ADDRESS TEXT, " +
                "$USER_COLUMN_CONTACT TEXT, " +
                "$USER_COLUMN_DOB TEXT)"

        val createFoodsTable = "CREATE TABLE $TABLE_FOODS (" +
                "$FOOD_COLUMN_ID TEXT PRIMARY KEY, " +
                "$FOOD_COLUMN_NAME TEXT, " +
                "$FOOD_COLUMN_CATEGORY TEXT, " +
                "$FOOD_COLUMN_DESCRIPTION TEXT, " +
                "$FOOD_COLUMN_PRICE INTEGER)"

        val createCartTable = "CREATE TABLE $TABLE_CART (" +
                "$CART_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$CART_COLUMN_FOOD_ID TEXT UNIQUE, " +
                "$CART_COLUMN_FOOD_NAME TEXT, " +
                "$CART_COLUMN_FOOD_CATEGORY TEXT, " +
                "$CART_COLUMN_FOOD_DESCRIPTION TEXT, " +
                "$CART_COLUMN_FOOD_PRICE INTEGER, " +
                "$CART_COLUMN_QUANTITY INTEGER)"

        db?.execSQL(createUsersTable)
        db?.execSQL(createFoodsTable)
        db?.execSQL(createCartTable)

        seedDefaultData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FOODS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CART")
        onCreate(db)
    }

    private fun seedDefaultData(db: SQLiteDatabase?) {
        val userValues = ContentValues().apply {
            put(USER_COLUMN_NAME, "Mahendra Rajpurohit")
            put(USER_COLUMN_EMAIL, "mahendra@gmail.com")
            put(USER_COLUMN_PASSWORD, "12345678")
            put(USER_COLUMN_ADDRESS, "123 Main Street")
            put(USER_COLUMN_CONTACT, "9876543210")
            put(USER_COLUMN_DOB, "15/08/1998")
        }
        db?.insert(TABLE_USERS, null, userValues)

        val initialFoods = listOf(
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

        initialFoods.forEach { food ->
            val foodValues = ContentValues().apply {
                put(FOOD_COLUMN_ID, food.id)
                put(FOOD_COLUMN_NAME, food.name)
                put(FOOD_COLUMN_CATEGORY, food.category)
                put(FOOD_COLUMN_DESCRIPTION, food.description)
                put(FOOD_COLUMN_PRICE, food.price)
            }
            db?.insert(TABLE_FOODS, null, foodValues)
        }
    }

    // ------------------------------------
    // 1. Sign Up & Sign In Operations
    // ------------------------------------

    fun insertUser(user: User): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(USER_COLUMN_NAME, user.name)
            put(USER_COLUMN_EMAIL, user.email)
            put(USER_COLUMN_PASSWORD, user.password)
            put(USER_COLUMN_ADDRESS, user.address)
            put(USER_COLUMN_CONTACT, user.contact)
            put(USER_COLUMN_DOB, user.dob)
        }
        val result = db.insert(TABLE_USERS, null, values)
        db.close()
        return result != -1L
    }

    fun checkUserCredentials(email: String, password: String): User? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $USER_COLUMN_EMAIL = ? AND $USER_COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))
        var user: User? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(USER_COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_NAME))
            val userEmail = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_EMAIL))
            val userPass = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_PASSWORD))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_ADDRESS)) ?: ""
            val contact = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_CONTACT)) ?: ""
            val dob = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_DOB)) ?: ""
            user = User(id, name, userEmail, userPass, address, contact, dob)
        }
        cursor.close()
        db.close()
        return user
    }

    // ------------------------------------
    // 2. Account Details Operations
    // ------------------------------------

    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $USER_COLUMN_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        var user: User? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(USER_COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_NAME))
            val userEmail = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_EMAIL))
            val userPass = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_PASSWORD))
            val address = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_ADDRESS)) ?: ""
            val contact = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_CONTACT)) ?: ""
            val dob = cursor.getString(cursor.getColumnIndexOrThrow(USER_COLUMN_DOB)) ?: ""
            user = User(id, name, userEmail, userPass, address, contact, dob)
        }
        cursor.close()
        db.close()
        return user
    }

    fun updateUser(user: User): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(USER_COLUMN_NAME, user.name)
            put(USER_COLUMN_EMAIL, user.email)
            put(USER_COLUMN_ADDRESS, user.address)
            put(USER_COLUMN_CONTACT, user.contact)
            put(USER_COLUMN_DOB, user.dob)
        }
        val whereClause = "$USER_COLUMN_ID = ?"
        val whereArgs = arrayOf(user.id.toString())
        val result = db.update(TABLE_USERS, values, whereClause, whereArgs)
        db.close()
        return result > 0
    }

    // ------------------------------------
    // 3. Fetch Foods Data Operations
    // ------------------------------------

    fun getAllFoods(): List<FoodProduct> {
        val foodsList = mutableListOf<FoodProduct>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_FOODS"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(FOOD_COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(FOOD_COLUMN_NAME))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(FOOD_COLUMN_CATEGORY))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(FOOD_COLUMN_DESCRIPTION))
            val price = cursor.getInt(cursor.getColumnIndexOrThrow(FOOD_COLUMN_PRICE))
            foodsList.add(FoodProduct(id, name, category, description, price))
        }
        cursor.close()
        db.close()
        return foodsList
    }

    fun getFoodsByCategory(categoryName: String): List<FoodProduct> {
        val foodsList = mutableListOf<FoodProduct>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_FOODS WHERE $FOOD_COLUMN_CATEGORY = ?"
        val cursor = db.rawQuery(query, arrayOf(categoryName))
        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndexOrThrow(FOOD_COLUMN_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(FOOD_COLUMN_NAME))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(FOOD_COLUMN_CATEGORY))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(FOOD_COLUMN_DESCRIPTION))
            val price = cursor.getInt(cursor.getColumnIndexOrThrow(FOOD_COLUMN_PRICE))
            foodsList.add(FoodProduct(id, name, category, description, price))
        }
        cursor.close()
        db.close()
        return foodsList
    }

    fun getAllCategories(): List<String> {
        val categories = mutableListOf<String>()
        val db = readableDatabase
        val query = "SELECT DISTINCT $FOOD_COLUMN_CATEGORY FROM $TABLE_FOODS"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val category = cursor.getString(cursor.getColumnIndexOrThrow(FOOD_COLUMN_CATEGORY))
            categories.add(category)
        }
        cursor.close()
        db.close()
        return categories
    }

    fun insertFoodProduct(food: FoodProduct): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(FOOD_COLUMN_ID, food.id)
            put(FOOD_COLUMN_NAME, food.name)
            put(FOOD_COLUMN_CATEGORY, food.category)
            put(FOOD_COLUMN_DESCRIPTION, food.description)
            put(FOOD_COLUMN_PRICE, food.price)
        }
        val result = db.insertWithOnConflict(TABLE_FOODS, null, values, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
        return result != -1L
    }

    // ------------------------------------
    // 4. Add to Cart Operations
    // ------------------------------------

    fun addToCart(product: FoodProduct) {
        val db = writableDatabase
        val query = "SELECT $CART_COLUMN_QUANTITY FROM $TABLE_CART WHERE $CART_COLUMN_FOOD_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(product.id))
        var currentQty = 0
        if (cursor.moveToFirst()) {
            currentQty = cursor.getInt(cursor.getColumnIndexOrThrow(CART_COLUMN_QUANTITY))
        }
        cursor.close()

        if (currentQty > 0) {
            val values = ContentValues().apply {
                put(CART_COLUMN_QUANTITY, currentQty + 1)
            }
            db.update(TABLE_CART, values, "$CART_COLUMN_FOOD_ID = ?", arrayOf(product.id))
        } else {
            val values = ContentValues().apply {
                put(CART_COLUMN_FOOD_ID, product.id)
                put(CART_COLUMN_FOOD_NAME, product.name)
                put(CART_COLUMN_FOOD_CATEGORY, product.category)
                put(CART_COLUMN_FOOD_DESCRIPTION, product.description)
                put(CART_COLUMN_FOOD_PRICE, product.price)
                put(CART_COLUMN_QUANTITY, 1)
            }
            db.insert(TABLE_CART, null, values)
        }
        db.close()
    }

    fun increaseCartQuantity(foodId: String) {
        val db = writableDatabase
        val query = "SELECT $CART_COLUMN_QUANTITY FROM $TABLE_CART WHERE $CART_COLUMN_FOOD_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(foodId))
        var currentQty = 0
        if (cursor.moveToFirst()) {
            currentQty = cursor.getInt(cursor.getColumnIndexOrThrow(CART_COLUMN_QUANTITY))
        }
        cursor.close()

        val values = ContentValues().apply {
            put(CART_COLUMN_QUANTITY, currentQty + 1)
        }
        db.update(TABLE_CART, values, "$CART_COLUMN_FOOD_ID = ?", arrayOf(foodId))
        db.close()
    }

    fun decreaseCartQuantity(foodId: String) {
        val db = writableDatabase
        val query = "SELECT $CART_COLUMN_QUANTITY FROM $TABLE_CART WHERE $CART_COLUMN_FOOD_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(foodId))
        var currentQty = 0
        if (cursor.moveToFirst()) {
            currentQty = cursor.getInt(cursor.getColumnIndexOrThrow(CART_COLUMN_QUANTITY))
        }
        cursor.close()

        if (currentQty > 1) {
            val values = ContentValues().apply {
                put(CART_COLUMN_QUANTITY, currentQty - 1)
            }
            db.update(TABLE_CART, values, "$CART_COLUMN_FOOD_ID = ?", arrayOf(foodId))
        } else {
            db.delete(TABLE_CART, "$CART_COLUMN_FOOD_ID = ?", arrayOf(foodId))
        }
        db.close()
    }

    fun removeFromCart(foodId: String) {
        val db = writableDatabase
        db.delete(TABLE_CART, "$CART_COLUMN_FOOD_ID = ?", arrayOf(foodId))
        db.close()
    }

    fun getAllCartLines(): List<CartLine> {
        val cartList = mutableListOf<CartLine>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_CART"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val foodId = cursor.getString(cursor.getColumnIndexOrThrow(CART_COLUMN_FOOD_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(CART_COLUMN_FOOD_NAME))
            val category = cursor.getString(cursor.getColumnIndexOrThrow(CART_COLUMN_FOOD_CATEGORY))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(CART_COLUMN_FOOD_DESCRIPTION))
            val price = cursor.getInt(cursor.getColumnIndexOrThrow(CART_COLUMN_FOOD_PRICE))
            val quantity = cursor.getInt(cursor.getColumnIndexOrThrow(CART_COLUMN_QUANTITY))

            val product = FoodProduct(foodId, name, category, description, price)
            cartList.add(CartLine(product, quantity))
        }
        cursor.close()
        db.close()
        return cartList
    }

    fun getCartItemCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM($CART_COLUMN_QUANTITY) FROM $TABLE_CART", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun getCartTotal(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT SUM($CART_COLUMN_FOOD_PRICE * $CART_COLUMN_QUANTITY) FROM $TABLE_CART", null)
        var total = 0
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return total
    }

    fun clearCart() {
        val db = writableDatabase
        db.delete(TABLE_CART, null, null)
        db.close()
    }
}
