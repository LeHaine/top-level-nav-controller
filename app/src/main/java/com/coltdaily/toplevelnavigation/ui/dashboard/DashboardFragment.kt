package com.coltdaily.toplevelnavigation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.coltdaily.toplevelnavigation.R

class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        root.findViewById<Button>(R.id.btn_next).setOnClickListener {
            findNavController().navigate(R.id.action_navigation_dashboard_to_dashboardPageFragment)
        }
        return root
    }
}
