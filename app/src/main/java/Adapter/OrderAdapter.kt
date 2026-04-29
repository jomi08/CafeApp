package Adapter




import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cafeapp.R

class OrderAdapter(
    private val orders: List<OrderItem>
) : RecyclerView.Adapter<OrderAdapter.VH>() {

    data class OrderItem(
        val id: String,
        val total: Int,
        val items: String,
        val status: String
    )

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvOrderId: TextView     = v.findViewById(R.id.tvOrderId)
        val tvOrderTotal: TextView  = v.findViewById(R.id.tvOrderTotal)
        val tvOrderItems: TextView  = v.findViewById(R.id.tvOrderItems)
        val tvOrderStatus: TextView = v.findViewById(R.id.tvOrderStatus)
        val orderProgress: ProgressBar = v.findViewById(R.id.orderProgress)
        val tvTimer: TextView       = v.findViewById(R.id.tvTimer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false))

    override fun onBindViewHolder(h: VH, i: Int) {
        val order = orders[i]
        h.tvOrderId.text    = order.id
        h.tvOrderTotal.text = "₹${order.total}"
        h.tvOrderItems.text = order.items
        h.tvOrderStatus.text = order.status

        // Simulate a 5-minute preparation countdown
        val totalMillis = 5 * 60 * 1000L
        object : CountDownTimer(totalMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                val minutes = secondsLeft / 60
                val seconds = secondsLeft % 60
                h.tvTimer.text = "Ready in ${minutes}m ${seconds}s"

                val progress = ((totalMillis - millisUntilFinished).toFloat() / totalMillis * 100).toInt()
                h.orderProgress.progress = progress
            }

            override fun onFinish() {
                h.tvOrderStatus.text = "Ready! ✅"
                h.tvOrderStatus.setBackgroundResource(R.drawable.chip_ready)
                h.orderProgress.progress = 100
                h.tvTimer.text = "Your order is ready!"
            }
        }.start()
    }

    override fun getItemCount(): Int = orders.size
}