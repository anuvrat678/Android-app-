package com.example.myapplication

import android.content.ContentUris
import android.content.ContentValues
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

data class OrderItem(
    val id: Long,
    val itemName: String,
    val quantity: Int,
    val price: Double,
    val status: String
)

class OrdersFragment : Fragment() {

    private lateinit var listView: ListView
    private lateinit var emptyTextView: TextView
    private lateinit var btnAddOrder: Button
    private val ordersList = mutableListOf<OrderItem>()
    private lateinit var adapter: ArrayAdapter<OrderItem>

    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean) {
            loadOrdersFromContentProvider()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)
        listView = view.findViewById(R.id.lvOrders)
        emptyTextView = view.findViewById(R.id.tvEmptyOrders)
        btnAddOrder = view.findViewById(R.id.btnAddOrder)

        setupAdapter(inflater)

        btnAddOrder.setOnClickListener {
            showAddOrderDialog()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        context?.contentResolver?.registerContentObserver(
            OrderContract.OrderEntry.CONTENT_URI,
            true,
            contentObserver
        )
        loadOrdersFromContentProvider()
    }

    override fun onStop() {
        super.onStop()
        context?.contentResolver?.unregisterContentObserver(contentObserver)
    }

    private fun setupAdapter(inflater: LayoutInflater) {
        adapter = object : ArrayAdapter<OrderItem>(
            requireContext(),
            R.layout.item_order_row,
            ordersList
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = convertView ?: inflater.inflate(R.layout.item_order_row, parent, false)
                val item = getItem(position) ?: return row

                row.findViewById<TextView>(R.id.tvOrderItemName).text = item.itemName
                row.findViewById<TextView>(R.id.tvOrderStatus).text = item.status
                row.findViewById<TextView>(R.id.tvOrderDetails).text =
                    "Qty: ${item.quantity}  |  Price: ₹${item.price}  |  Total: ₹${item.quantity * item.price}"

                row.findViewById<Button>(R.id.btnEditOrder).setOnClickListener {
                    showEditOrderDialog(item)
                }

                row.findViewById<Button>(R.id.btnDeleteOrder).setOnClickListener {
                    confirmDeleteOrder(item)
                }

                return row
            }
        }
        listView.adapter = adapter
    }

    private fun loadOrdersFromContentProvider() {
        ordersList.clear()
        val cursor = context?.contentResolver?.query(
            OrderContract.OrderEntry.CONTENT_URI,
            null,
            null,
            null,
            "${OrderContract.OrderEntry.COLUMN_ID} DESC"
        )

        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(OrderContract.OrderEntry.COLUMN_ID)
            val nameIndex = it.getColumnIndexOrThrow(OrderContract.OrderEntry.COLUMN_ITEM_NAME)
            val qtyIndex = it.getColumnIndexOrThrow(OrderContract.OrderEntry.COLUMN_QUANTITY)
            val priceIndex = it.getColumnIndexOrThrow(OrderContract.OrderEntry.COLUMN_PRICE)
            val statusIndex = it.getColumnIndexOrThrow(OrderContract.OrderEntry.COLUMN_STATUS)

            while (it.moveToNext()) {
                ordersList.add(
                    OrderItem(
                        id = it.getLong(idIndex),
                        itemName = it.getString(nameIndex),
                        quantity = it.getInt(qtyIndex),
                        price = it.getDouble(priceIndex),
                        status = it.getString(statusIndex)
                    )
                )
            }
        }

        emptyTextView.visibility = if (ordersList.isEmpty()) View.VISIBLE else View.GONE
        adapter.notifyDataSetChanged()
    }

    private fun showAddOrderDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_order, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        tvTitle.text = "Add New Order"

        val etName = dialogView.findViewById<EditText>(R.id.etOrderItemName)
        val etQty = dialogView.findViewById<EditText>(R.id.etOrderQuantity)
        val etPrice = dialogView.findViewById<EditText>(R.id.etOrderPrice)
        val etStatus = dialogView.findViewById<EditText>(R.id.etOrderStatus)
        etStatus.setText("Placed")

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                val qty = etQty.text.toString().toIntOrNull() ?: 1
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                val status = etStatus.text.toString().trim().ifEmpty { "Placed" }

                if (name.isNotEmpty()) {
                    val values = ContentValues().apply {
                        put(OrderContract.OrderEntry.COLUMN_ITEM_NAME, name)
                        put(OrderContract.OrderEntry.COLUMN_QUANTITY, qty)
                        put(OrderContract.OrderEntry.COLUMN_PRICE, price)
                        put(OrderContract.OrderEntry.COLUMN_STATUS, status)
                    }

                    val uri = context?.contentResolver?.insert(OrderContract.OrderEntry.CONTENT_URI, values)
                    if (uri != null) {
                        Toast.makeText(requireContext(), "Order added via Content Provider", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Item name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditOrderDialog(item: OrderItem) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_order, null)
        val tvTitle = dialogView.findViewById<TextView>(R.id.tvDialogTitle)
        tvTitle.text = "Edit Order"

        val etName = dialogView.findViewById<EditText>(R.id.etOrderItemName)
        val etQty = dialogView.findViewById<EditText>(R.id.etOrderQuantity)
        val etPrice = dialogView.findViewById<EditText>(R.id.etOrderPrice)
        val etStatus = dialogView.findViewById<EditText>(R.id.etOrderStatus)

        etName.setText(item.itemName)
        etQty.setText(item.quantity.toString())
        etPrice.setText(item.price.toString())
        etStatus.setText(item.status)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val name = etName.text.toString().trim()
                val qty = etQty.text.toString().toIntOrNull() ?: item.quantity
                val price = etPrice.text.toString().toDoubleOrNull() ?: item.price
                val status = etStatus.text.toString().trim().ifEmpty { item.status }

                if (name.isNotEmpty()) {
                    val values = ContentValues().apply {
                        put(OrderContract.OrderEntry.COLUMN_ITEM_NAME, name)
                        put(OrderContract.OrderEntry.COLUMN_QUANTITY, qty)
                        put(OrderContract.OrderEntry.COLUMN_PRICE, price)
                        put(OrderContract.OrderEntry.COLUMN_STATUS, status)
                    }

                    val itemUri = ContentUris.withAppendedId(OrderContract.OrderEntry.CONTENT_URI, item.id)
                    val rows = context?.contentResolver?.update(itemUri, values, null, null) ?: 0
                    if (rows > 0) {
                        Toast.makeText(requireContext(), "Order updated via Content Provider", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Item name cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun confirmDeleteOrder(item: OrderItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Order")
            .setMessage("Are you sure you want to delete '${item.itemName}' from Content Provider store?")
            .setPositiveButton("Delete") { _, _ ->
                val itemUri = ContentUris.withAppendedId(OrderContract.OrderEntry.CONTENT_URI, item.id)
                val rows = context?.contentResolver?.delete(itemUri, null, null) ?: 0
                if (rows > 0) {
                    Toast.makeText(requireContext(), "Order deleted via Content Provider", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
