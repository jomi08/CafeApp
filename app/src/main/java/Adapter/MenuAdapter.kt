package Adapter




import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeapp.R          // ← explicit R import — this is the fix
import org.json.JSONArray
import org.json.JSONObject

class MenuAdapter(
    private val context: Context,
    private val items: List<MenuItem>,
    private val onCartUpdated: () -> Unit
) : RecyclerView.Adapter<MenuAdapter.VH>() {

    data class MenuItem(
        val name: String,
        val desc: String,
        val price: Int,
        val image: Int
    )

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView  = v.findViewById(R.id.img)
        val name: TextView  = v.findViewById(R.id.name)
        val desc: TextView  = v.findViewById(R.id.desc)
        val price: TextView = v.findViewById(R.id.price)
        val btnAdd: Button  = v.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_menu, parent, false))

    override fun onBindViewHolder(h: VH, i: Int) {
        val item = items[i]
        h.img.setImageResource(item.image)
        h.name.text  = item.name
        h.desc.text  = item.desc
        h.price.text = "₹${item.price}"

        h.btnAdd.setOnClickListener {
            addToCart(item)
            Toast.makeText(context, "${item.name} added to cart ☕", Toast.LENGTH_SHORT).show()
            onCartUpdated()
        }
    }

    override fun getItemCount(): Int = items.size

    private fun addToCart(item: MenuItem) {
        val sp: SharedPreferences = context.getSharedPreferences("Cart", Context.MODE_PRIVATE)
        val arr = JSONArray(sp.getString("cart_items", "[]"))

        var found = false
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            if (obj.getString("name") == item.name) {
                obj.put("quantity", obj.getInt("quantity") + 1)
                found = true
                break
            }
        }

        if (!found) {
            val obj = JSONObject()
            obj.put("name", item.name)
            obj.put("price", item.price)
            obj.put("image", item.image)
            obj.put("quantity", 1)
            arr.put(obj)
        }

        sp.edit().putString("cart_items", arr.toString()).apply()
    }
}