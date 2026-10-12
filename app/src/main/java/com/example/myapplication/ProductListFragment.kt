
package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProductListFragment : Fragment() {

    companion object {
        private const val ARG_CATEGORY = "category"

        fun newInstance(category: String): ProductListFragment {
            return ProductListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.fragment_products,
            container,
            false
        )

        val category =
            arguments?.getString(ARG_CATEGORY) ?: "Pizza"

        val title =
            view.findViewById<TextView>(R.id.tvCategoryTitle)

        val listProducts =
            view.findViewById<ListView>(R.id.listProducts)

        val btnBack =
            view.findViewById<Button>(R.id.btnBackToCategories)

        title.text = category

        val filteredProducts = FoodRepository.products.filter {
            it.category == category
        }

        val productNames = filteredProducts.map {
            "${it.name}  •  ₹${it.price}"
        }

        listProducts.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            productNames
        )

        listProducts.setOnItemClickListener { _, _, position, _ ->
            val product = filteredProducts[position]
            CartManager.add(product)

            Toast.makeText(
                requireContext(),
                "${product.name} added to cart",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
