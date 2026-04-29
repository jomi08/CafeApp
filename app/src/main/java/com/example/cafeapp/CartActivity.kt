package com.example.cafeapp


import Adapter.CartAdapter
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    private lateinit var sp: SharedPreferences
    private lateinit var cartAdapter: CartAdapter
    private lateinit var tvTotal: TextView
    private lateinit var emptyState: LinearLayout
    private lateinit var cartRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sp = getSharedPreferences("Cart", MODE_PRIVATE)

        tvTotal           = findViewById(R.id.tvTotal)
        emptyState        = findViewById(R.id.emptyState)
        cartRecyclerView  = findViewById(R.id.cartRecyclerView)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        cartAdapter = CartAdapter(
            getCartItems(),
            onQtyChanged = { saveCart(it); refreshUI(it) }
        )

        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = cartAdapter

        refreshUI(getCartItems())

        findViewById<Button>(R.id.btnPlaceOrder).setOnClickListener {
            val items = getCartItems()
            if (items.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            placeOrder(items)
        }
    }

    private fun getCartItems(): MutableList<CartAdapter.CartItem> {
        val list = mutableListOf<CartAdapter.CartItem>()
        try {
            val arr = JSONArray(sp.getString("cart_items", "[]"))
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    CartAdapter.CartItem(
                        name     = obj.getString("name"),
                        price    = obj.getInt("price"),
                        image    = obj.getInt("image"),
                        quantity = obj.getInt("quantity")
                    )
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return list
    }

    private fun saveCart(items: List<CartAdapter.CartItem>) {
        val arr = JSONArray()
        for (item in items) {
            val obj = JSONObject()
            obj.put("name", item.name)
            obj.put("price", item.price)
            obj.put("image", item.image)
            obj.put("quantity", item.quantity)
            arr.put(obj)
        }
        sp.edit().putString("cart_items", arr.toString()).apply()
    }

    private fun refreshUI(items: List<CartAdapter.CartItem>) {
        val total = items.sumOf { it.price * it.quantity }
        tvTotal.text = "₹$total"

        if (items.isEmpty()) {
            emptyState.visibility       = View.VISIBLE
            cartRecyclerView.visibility = View.GONE
        } else {
            emptyState.visibility       = View.GONE
            cartRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun placeOrder(items: List<CartAdapter.CartItem>) {
        val orderSp = getSharedPreferences("Orders", MODE_PRIVATE)
        val ordersArr = JSONArray(orderSp.getString("order_list", "[]"))

        val order = JSONObject()
        val orderId = "Order #${(100..999).random()}"
        val total   = items.sumOf { it.price * it.quantity }
        val itemsSummary = items.joinToString(", ") { "${it.name} x${it.quantity}" }

        order.put("id", orderId)
        order.put("total", total)
        order.put("items", itemsSummary)
        order.put("status", "Preparing")
        ordersArr.put(order)

        orderSp.edit().putString("order_list", ordersArr.toString()).apply()

        // Clear cart
        sp.edit().putString("cart_items", "[]").apply()

        Toast.makeText(this, "Order placed! ☕ $orderId", Toast.LENGTH_LONG).show()
        finish()
    }
}