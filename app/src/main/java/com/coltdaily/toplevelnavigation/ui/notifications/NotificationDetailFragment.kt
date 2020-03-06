package com.coltdaily.toplevelnavigation.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.coltdaily.toplevelnavigation.R

/**
 * Created by Colt Daily on 3/6/20.
 */
class NotificationDetailFragment : Fragment() {

    val args: NotificationDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notification_detail, container, false)
        root.findViewById<TextView>(R.id.text_notification).text = args.notificationText
        return root
    }
}
