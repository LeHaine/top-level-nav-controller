package com.coltdaily.toplevelnavigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.ui.setupActionBarWithNavController
import com.coltdaily.toplevelnavigation.ui.TopLevelNavigator
import com.coltdaily.toplevelnavigation.ui.navigation.TopLevelNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), TopLevelNavigator {

    private val navGraphIds = listOf(
        R.navigation.home_navigation,
        R.navigation.dashboard_navigation,
        R.navigation.notifications_navigation,
        R.navigation.settings_navigation,
        R.navigation.profile_navigation
    )
    private lateinit var topLevelNav: TopLevelNavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        val navView: NavigationView = findViewById(R.id.navigation_view)

        topLevelNav = TopLevelNavController(
            navGraphIds,
            supportFragmentManager,
            R.id.nav_host_container,
            intent
        )
        topLevelNav.setupWithBottomNavigation(bottomNavView)
        topLevelNav.setupNavigationView(navView)

        topLevelNav.selectedNavController.observe(this, Observer { navController ->
            setupActionBarWithNavController(navController)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return topLevelNav.selectedNavController.value?.navigateUp() ?: false
    }

    override fun getTopLevelNavController() = topLevelNav
}
