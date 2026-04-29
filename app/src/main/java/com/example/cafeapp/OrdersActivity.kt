package com.example.cafeapp

import Adapter.OrderAdapter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import org.json.JSONArray
import org.json.JSONException

class OrdersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        findViewById<TextView>(R.id.btnBack).setOnClickListener { finish() }

        val sp = getSharedPreferences("Orders", MODE_PRIVATE)
        val orders = mutableListOf<OrderAdapter.OrderItem>()

        try {
            val arr = JSONArray(sp.getString("order_list", "[]"))
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                orders.add(
                    OrderAdapter.OrderItem(
                        id     = obj.getString("id"),
                        total  = obj.getInt("total"),
                        items  = obj.getString("items"),
                        status = obj.getString("status")
                    )
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val tvNoOrders = findViewById<TextView>(R.id.tvNoOrders)
        val rv         = findViewById<RecyclerView>(R.id.ordersRecyclerView)

        if (orders.isEmpty()) {
            tvNoOrders.visibility = View.VISIBLE
            rv.visibility         = View.GONE
        } else {
            tvNoOrders.visibility = View.GONE
            rv.visibility         = View.VISIBLE
            rv.layoutManager      = LinearLayoutManager(this)
            rv.adapter            = OrderAdapter(orders)
        }
    }
}