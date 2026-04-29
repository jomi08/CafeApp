package Adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeapp.R

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val onQtyChanged: (MutableList<CartItem>) -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    data class CartItem(
        val name: String,
        val price: Int,
        val image: Int,
        var quantity: Int
    )

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView         = v.findViewById(R.id.cartItemImage)
        val name: TextView         = v.findViewById(R.id.cartItemName)
        val subtotal: TextView     = v.findViewById(R.id.cartItemSubtotal)
        val qty: TextView          = v.findViewById(R.id.cartItemQty)
        val btnIncrease: Button    = v.findViewById(R.id.btnIncrease)
        val btnDecrease: Button    = v.findViewById(R.id.btnDecrease)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false))

    override fun onBindViewHolder(h: VH, i: Int) {
        val item = items[i]
        h.img.setImageResource(item.image)
        h.name.text    = item.name
        h.subtotal.text = "₹${item.price * item.quantity}"
        h.qty.text     = item.quantity.toString()

        h.btnIncrease.setOnClickListener {
            item.quantity++
            notifyItemChanged(i)
            onQtyChanged(items)
        }

        h.btnDecrease.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                notifyItemChanged(i)
            } else {
                items.removeAt(i)
                notifyItemRemoved(i)
                notifyItemRangeChanged(i, items.size)
            }
            onQtyChanged(items)
        }
    }

    override fun getItemCount(): Int = items.size
}