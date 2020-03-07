package com.coltdaily.toplevelnavigation.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.coltdaily.toplevelnavigation.R

/**
 * A simple [Fragment] subclass.
 */
class SettingDetail : Fragment() {

    private val args: SettingDetailArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting_detail, container, false)
        view.findViewById<TextView>(R.id.text_setting).text = args.setting
        return view
    }

}
