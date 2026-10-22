package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private lateinit var db: AppDatabaseHelper

    companion object {
        private const val ARG_CATEGORY = "selected_category"

        fun newInstance(category: String? = null): HomeFragment {
            return HomeFragment().apply {
                arguments = Bundle().apply { putString(ARG_CATEGORY, category) }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        db = AppDatabaseHelper(requireContext())

        val category = arguments?.getString(ARG_CATEGORY)
        val title = view.findViewById<TextView>(R.id.tvHomeSectionTitle)
        val subtitle = view.findViewById<TextView>(R.id.tvHomeSectionSubtitle)
        val clearFilter = view.findViewById<Button>(R.id.btnClearFilter)
        val btnAddFoodToDb = view.findViewById<Button>(R.id.btnAddFoodToDb)
        val productContainer = view.findViewById<LinearLayout>(R.id.productCardsContainer)

        title.text = if (category.isNullOrBlank()) {
            getString(R.string.p4_featured_food)
        } else {
            getString(R.string.p4_category_products, category)
        }
        subtitle.text = if (category.isNullOrBlank()) {
            getString(R.string.p4_home_caption)
        } else {
            getString(R.string.p4_filter_caption, category)
        }
        clearFilter.visibility = if (category.isNullOrBlank()) View.GONE else View.VISIBLE
        clearFilter.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        fun loadProductsFromDatabase() {
            val shownProducts = if (category.isNullOrBlank()) {
                db.getAllFoods()
            } else {
                db.getFoodsByCategory(category)
            }

            productContainer.removeAllViews()
            shownProducts.forEach { product ->
                val card = inflater.inflate(R.layout.item_product_card, productContainer, false)
                val foodIcon = when (product.category) {
                    "Pizza" -> R.drawable.ic_food_pizza
                    "Burger" -> R.drawable.ic_food_burger
                    "Desserts" -> R.drawable.ic_food_dessert
                    "Beverages" -> R.drawable.ic_food_drink
                    else -> R.drawable.ic_yummcart_food
                }
                card.findViewById<ImageView>(R.id.ivFoodIcon).setImageResource(foodIcon)
                card.findViewById<TextView>(R.id.tvFoodName).text = product.name
                card.findViewById<TextView>(R.id.tvFoodDescription).text = product.description
                card.findViewById<TextView>(R.id.tvFoodPrice).text = getString(R.string.p4_price, product.price)

                card.findViewById<Button>(R.id.btnAddToCart).setOnClickListener {
                    db.addToCart(product)
                    Toast.makeText(
                        requireContext(),
                        "${product.name} added to Cart in SQLite DB!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                productContainer.addView(card)
            }
        }

        loadProductsFromDatabase()

        btnAddFoodToDb.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_food, null)
            val etName = dialogView.findViewById<EditText>(R.id.etAddFoodName)
            val etCat = dialogView.findViewById<EditText>(R.id.etAddFoodCategory)
            val etDesc = dialogView.findViewById<EditText>(R.id.etAddFoodDescription)
            val etPrice = dialogView.findViewById<EditText>(R.id.etAddFoodPrice)

            if (!category.isNullOrBlank()) {
                etCat.setText(category)
            }

            AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("Add to DB") { _, _ ->
                    val name = etName.text.toString().trim()
                    val cat = etCat.text.toString().trim().ifEmpty { "Pizza" }
                    val desc = etDesc.text.toString().trim().ifEmpty { "Delicious item" }
                    val price = etPrice.text.toString().toIntOrNull() ?: 149

                    if (name.isNotEmpty()) {
                        val foodId = "custom_${System.currentTimeMillis()}"
                        val product = FoodProduct(foodId, name, cat, desc, price)
                        val success = db.insertFoodProduct(product)
                        if (success) {
                            Toast.makeText(requireContext(), "Food product added to SQLite database!", Toast.LENGTH_SHORT).show()
                            loadProductsFromDatabase()
                        } else {
                            Toast.makeText(requireContext(), "Failed to add food product", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Food name is required", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        return view
    }
}
