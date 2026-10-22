package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class CategoriesFragment : Fragment() {

    private lateinit var db: AppDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_categories, container, false)
        db = AppDatabaseHelper(requireContext())

        val categoryContainer = view.findViewById<LinearLayout>(R.id.categoriesContainer)
        categoryContainer.removeAllViews()

        val categoryPresentation = mapOf(
            "Pizza" to (R.drawable.ic_food_pizza to R.string.p4_category_pizza_caption),
            "Burger" to (R.drawable.ic_food_burger to R.string.p4_category_burger_caption),
            "Desserts" to (R.drawable.ic_food_dessert to R.string.p4_category_desserts_caption),
            "Beverages" to (R.drawable.ic_food_drink to R.string.p4_category_beverages_caption)
        )

        val categories = db.getAllCategories()

        categories.forEach { category ->
            val row = inflater.inflate(R.layout.item_category_card, categoryContainer, false)
            val presentation = categoryPresentation[category]
                ?: (R.drawable.ic_yummcart_food to R.string.p4_brand_tagline)

            val (iconRes, captionRes) = presentation
            row.findViewById<ImageView>(R.id.ivCategoryIcon).setImageResource(iconRes)
            row.findViewById<TextView>(R.id.tvCategoryName).text = category
            row.findViewById<TextView>(R.id.tvCategoryCaption).text = getString(captionRes)
            row.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, HomeFragment.newInstance(category))
                    .addToBackStack("category_filter")
                    .commit()
            }
            categoryContainer.addView(row)
        }
        return view
    }
}
