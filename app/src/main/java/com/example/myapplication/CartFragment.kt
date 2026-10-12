package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
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
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        val cartItemsContainer = view.findViewById<LinearLayout>(R.id.cartItemsContainer)
        val emptyMessage = view.findViewById<TextView>(R.id.tvEmptyCart)
        val totalView = view.findViewById<TextView>(R.id.tvCartTotal)
        val checkout = view.findViewById<Button>(R.id.btnCheckout)

        fun refreshCart() {
            val lines = FoodStore.cartLines()
            cartItemsContainer.removeAllViews()
            emptyMessage.visibility = if (lines.isEmpty()) View.VISIBLE else View.GONE

            lines.forEach { line ->
                val row = inflater.inflate(R.layout.item_cart_row, cartItemsContainer, false)
                row.findViewById<TextView>(R.id.tvCartItemName).text = line.product.name
                row.findViewById<TextView>(R.id.tvCartItemDetails).text = getString(
                    R.string.p4_cart_line_details,
                    line.product.price,
                    line.quantity,
                    line.product.price * line.quantity
                )
                row.findViewById<TextView>(R.id.tvCartQuantity).text = line.quantity.toString()
                row.findViewById<Button>(R.id.btnCartDecrease).setOnClickListener {
                    FoodStore.decrease(line.product.id)
                    refreshCart()
                }
                row.findViewById<Button>(R.id.btnCartIncrease).setOnClickListener {
                    FoodStore.increase(line.product.id)
                    refreshCart()
                }
                row.findViewById<Button>(R.id.btnCartRemove).setOnClickListener {
                    FoodStore.remove(line.product.id)
                    refreshCart()
                }
                cartItemsContainer.addView(row)
            }
            totalView.text = getString(R.string.p4_cart_total, FoodStore.total())
        }

        refreshCart()

        checkout.setOnClickListener {
            val lines = FoodStore.cartLines()
            if (lines.isEmpty()) {
                Toast.makeText(requireContext(), R.string.p4_cart_empty_toast, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle(R.string.p4_order_summary)
                .setMessage(getString(R.string.p4_order_confirmation, FoodStore.itemCount(), FoodStore.total()))
                .setPositiveButton(R.string.p4_place_order) { _, _ ->
                    FoodStore.clearCart()
                    refreshCart()
                    Toast.makeText(requireContext(), R.string.p4_order_placed, Toast.LENGTH_LONG).show()
                }
                .setNegativeButton(R.string.p4_cancel, null)
                .show()
        }

        return view
    }
}
