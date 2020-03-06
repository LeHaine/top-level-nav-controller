package com.coltdaily.toplevelnavigation.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.coltdaily.toplevelnavigation.R

class NotificationsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notifications, container, false)

        val viewAdapter = MyAdapter(Array(10) { "Notification ${it + 1}" })

        view.findViewById<RecyclerView>(R.id.notifications_list).run {
            setHasFixedSize(true)
            adapter = viewAdapter
        }
        return view
    }

}

class MyAdapter(private val myDataset: Array<String>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    class ViewHolder(val item: View) : RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_notification, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.findViewById<TextView>(R.id.text_notification).text = myDataset[position]

        holder.item.setOnClickListener {
            holder.item.findNavController().navigate(
                NotificationsFragmentDirections.actionNavigationNotificationsToNotificationDetailFragment(
                    myDataset[position]
                )
            )
        }
    }

    override fun getItemCount() = myDataset.size
}

