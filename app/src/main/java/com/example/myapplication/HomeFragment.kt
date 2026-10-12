package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

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
        val category = arguments?.getString(ARG_CATEGORY)
        val title = view.findViewById<TextView>(R.id.tvHomeSectionTitle)
        val subtitle = view.findViewById<TextView>(R.id.tvHomeSectionSubtitle)
        val clearFilter = view.findViewById<Button>(R.id.btnClearFilter)
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

        val shownProducts = if (category.isNullOrBlank()) {
            FoodStore.products
        } else {
            FoodStore.products.filter { it.category == category }
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
                FoodStore.add(product)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.p4_added_to_cart, product.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
            productContainer.addView(card)
        }

        return view
    }
}
