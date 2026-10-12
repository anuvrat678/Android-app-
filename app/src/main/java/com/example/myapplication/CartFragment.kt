
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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class CartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.fragment_cart,
            container,
            false
        )

        val listCart =
            view.findViewById<ListView>(R.id.listCart)

        val tvEmpty =
            view.findViewById<TextView>(R.id.tvEmptyCart)

        val tvTotal =
            view.findViewById<TextView>(R.id.tvCartTotal)

        val btnCheckout =
            view.findViewById<Button>(R.id.btnCheckout)

        fun refreshCart() {
            val entries = CartManager.getEntries()

            tvEmpty.visibility =
                if (entries.isEmpty()) View.VISIBLE else View.GONE

            listCart.visibility =
                if (entries.isEmpty()) View.GONE else View.VISIBLE

            val rows = entries.map { entry ->
                "${entry.product.name}\n" +
                        "₹${entry.product.price} × ${entry.quantity} = " +
                        "₹${entry.product.price * entry.quantity}"
            }

            listCart.adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                rows
            )

            tvTotal.text = "Total: ₹${CartManager.total()}"
        }

        refreshCart()

        listCart.setOnItemClickListener { _, _, position, _ ->
            val entries = CartManager.getEntries()

            if (position !in entries.indices) {
                return@setOnItemClickListener
            }

            val selected = entries[position]

            val options = arrayOf(
                "Increase quantity",
                "Decrease quantity",
                "Remove item"
            )

            AlertDialog.Builder(requireContext())
                .setTitle(selected.product.name)
                .setItems(options) { _, choice ->
                    when (choice) {
                        0 -> CartManager.increase(selected.product)
                        1 -> CartManager.decrease(selected.product)
                        2 -> CartManager.remove(selected.product)
                    }

                    refreshCart()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnCheckout.setOnClickListener {
            val entries = CartManager.getEntries()

            if (entries.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Your cart is empty",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Order Summary")
                .setMessage(
                    "Items: ${entries.sumOf { it.quantity }}\n" +
                            "Total amount: ₹${CartManager.total()}\n\n" +
                            "Place this sample order?"
                )
                .setPositiveButton("Place Order") { _, _ ->
                    CartManager.clear()
                    refreshCart()

                    Toast.makeText(
                        requireContext(),
                        "Order placed successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        return view
    }
}
