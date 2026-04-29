package com.example.cafeapp

import Adapter.MenuAdapter
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONException

class HomeActivity : AppCompatActivity() {

    private lateinit var cartBadge: TextView
    private lateinit var sp: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sp = getSharedPreferences("UserData", MODE_PRIVATE)

        val tvGreet = findViewById<TextView>(R.id.tvGreeting)
        tvGreet.text = "Hey, ${sp.getString("loggedInName", "Guest")} 👋"

        cartBadge = findViewById(R.id.cartBadge)

        // ── Logout ── works whether btnLogout is a Button or TextView in XML
        findViewById<View>(R.id.btnLogout).setOnClickListener {
            sp.edit().clear().commit()
            getSharedPreferences("Cart", MODE_PRIVATE).edit().clear().commit()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        val menu = listOf(
            MenuAdapter.MenuItem("Cappuccino",  "Rich espresso with milk foam",   120, R.drawable.coffee_img),
            MenuAdapter.MenuItem("Latte",        "Smooth milk with espresso",      140, R.drawable.latte_img),
            MenuAdapter.MenuItem("Cold Coffee",  "Chilled coffee with ice cream",  160, R.drawable.coldcoffee_img),
            MenuAdapter.MenuItem("Espresso",     "Strong single-shot espresso",     90, R.drawable.coffee_img),
            MenuAdapter.MenuItem("Mocha",        "Chocolate meets espresso",       150, R.drawable.latte_img),
            MenuAdapter.MenuItem("Frappe",       "Blended iced coffee delight",    170, R.drawable.coldcoffee_img)
        )

        val rv = findViewById<RecyclerView>(R.id.menuRecyclerView)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = MenuAdapter(this, menu) { refreshBadge() }

        findViewById<View>(R.id.navCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        findViewById<View>(R.id.navOrders).setOnClickListener {
            startActivity(Intent(this, OrdersActivity::class.java))
        }
        findViewById<View>(R.id.cartIconFrame).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshBadge()
    }

    private fun refreshBadge() {
        val sp = getSharedPreferences("Cart", MODE_PRIVATE)
        try {
            val arr = JSONArray(sp.getString("cart_items", "[]"))
            var count = 0
            for (i in 0 until arr.length()) {
                count += arr.getJSONObject(i).getInt("quantity")
            }
            if (count > 0) {
                cartBadge.text = count.toString()
                cartBadge.visibility = View.VISIBLE
            } else {
                cartBadge.visibility = View.GONE
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}