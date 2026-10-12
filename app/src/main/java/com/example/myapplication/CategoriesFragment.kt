
package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment

class CategoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.fragment_categories,
            container,
            false
        )

        val listCategories =
            view.findViewById<ListView>(R.id.listCategories)

        listCategories.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            FoodRepository.categories
        )

        listCategories.setOnItemClickListener { _, _, position, _ ->
            val category = FoodRepository.categories[position]

            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    ProductListFragment.newInstance(category)
                )
                .addToBackStack("products")
                .commit()
        }

        return view
    }
}
