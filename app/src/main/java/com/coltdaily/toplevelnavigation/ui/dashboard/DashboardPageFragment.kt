package com.coltdaily.toplevelnavigation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.coltdaily.toplevelnavigation.R
import com.coltdaily.toplevelnavigation.ui.findTopLevelNavController
import com.coltdaily.toplevelnavigation.ui.notifications.NotificationDetailFragmentArgs

/**
 * Created by Colt Daily on 3/6/20.
 */
class DashboardPageFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard_page, container, false)

        root.findViewById<Button>(R.id.btn_next).setOnClickListener {
            findTopLevelNavController().navigate(
                R.navigation.notifications_navigation,
                R.id.notificationDetailFragment,
                NotificationDetailFragmentArgs("3").toBundle()
            )
        }
        return root
    }
}