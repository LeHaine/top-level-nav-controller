package com.coltdaily.toplevelnavigation.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.coltdaily.toplevelnavigation.R
import com.coltdaily.toplevelnavigation.ui.findTopLevelNavController
import com.coltdaily.toplevelnavigation.ui.settings.SettingDetailArgs

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        view.findViewById<Button>(R.id.profile_btn).setOnClickListener {
            findTopLevelNavController().navigate(R.navigation.profile_navigation)
        }

        view.findViewById<Button>(R.id.setting_btn).setOnClickListener {
            findTopLevelNavController().navigate(
                R.navigation.settings_navigation,
                R.id.settingDetail,
                SettingDetailArgs("Setting 2").toBundle()
            )
        }
        return view
    }
}
