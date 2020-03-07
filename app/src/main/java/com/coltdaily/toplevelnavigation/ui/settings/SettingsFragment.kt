package com.coltdaily.toplevelnavigation.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.coltdaily.toplevelnavigation.R

/**
 * A simple [Fragment] subclass.
 */
class SettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        val viewAdapter = MyAdapter(Array(10) { "Setting ${it + 1}" })

        view.findViewById<RecyclerView>(R.id.settings_list).run {
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
            .inflate(R.layout.list_item_setting, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.findViewById<TextView>(R.id.text_setting).text = myDataset[position]

        holder.item.setOnClickListener {
            holder.item.findNavController().navigate(
                SettingsFragmentDirections.actionSettingsFragmentToSettingDetail(
                    myDataset[position]
                )
            )
        }
    }

    override fun getItemCount() = myDataset.size
}

