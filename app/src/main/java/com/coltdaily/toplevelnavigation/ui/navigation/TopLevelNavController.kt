package com.coltdaily.toplevelnavigation.ui.navigation


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources.NotFoundException
import android.os.Bundle
import android.os.Handler
import android.util.SparseArray
import android.util.SparseIntArray
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.core.util.forEach
import androidx.core.util.set
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.coltdaily.toplevelnavigation.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

/**
 * Created by Colt Daily on 3/6/20.
 */
class TopLevelNavController(
    private val navGraphIds: List<Int>,
    private val fragmentManager: FragmentManager,
    private val containerId: Int,
    private val intent: Intent
) {

    private val _selectedNavController = MutableLiveData<NavController>()
    val selectedNavController: LiveData<NavController>
        get() = _selectedNavController

    private val graphIdToTagMap = SparseArray<String>()
    private val navGraphIdToGraphId = SparseIntArray()

    private var selectedItemTag = ""
    private var firstFragmentGraphId = 0
    private var isOnFirstFragment = false

    private var bottomNavView: BottomNavigationView? = null
    private var navigationView: NavigationView? = null

    init {
        // Create a NavHostFragment for each NavGraph ID
        navGraphIds.forEachIndexed { index, navGraphId ->
            val fragmentTag = getFragmentTag(index)
            val navHostFragment = obtainNavHostFragment(
                fragmentTag,
                navGraphId
            )
            val graphId = navHostFragment.navController.graph.id

            if (index == 0) {
                firstFragmentGraphId = graphId
            }
            graphIdToTagMap[graphId] = fragmentTag
            navGraphIdToGraphId[navGraphId] = graphId

            if (index == 0) {
                attachNavHostFragment(navHostFragment)
            } else {
                detachNavHostFragment(navHostFragment)
            }
        }

        setupDeepLinks()
    }

    @SuppressLint("ResourceType")
    fun navigate(@NavigationRes navGraphId: Int, args: Bundle? = null) {
        navigate(navGraphId, navGraphId, args)
    }

    fun navigate(@NavigationRes navGraphId: Int, @IdRes destinationId: Int, args: Bundle? = null) {
        check(navGraphIds.contains(navGraphId)) {
            "The navGraphId must have been initialized with the top level list of graph ids"
        }

        val newlySelectedItemTag = graphIdToTagMap[navGraphIdToGraphId[navGraphId]]

        // grab the selected fragment if it already exists and if so hide it
        var selectedFragment =
            fragmentManager.findFragmentByTag(newlySelectedItemTag) as NavHostFragment?
        selectedFragment?.let {
            fragmentManager.beginTransaction().hide(it).commitNow()
        }
        // attach the fragment without change the current nav controller
        attachNoNavControllerChange(navGraphIdToGraphId[navGraphId])
        if (selectedFragment == null) selectedFragment =
            fragmentManager.findFragmentByTag(newlySelectedItemTag) as NavHostFragment

        val navController = selectedFragment.navController
        val graph = navController.graph

        // pop to beginning of graph and navigate to the destination
        Handler().post {
            if (destinationId != navGraphId) {
                graph.findNode(destinationId)
                    ?: throw IllegalStateException(
                        "unknown destination during top level navigation: "
                                + getDisplayName(selectedFragment.context, destinationId)
                    )
                navController.navigate(
                    destinationId, args, NavOptions.Builder()
                        .setPopUpTo(graph.startDestination, false)
                        .build()
                )
            } else {
                navController.navigate(
                    graph.startDestination, args, NavOptions.Builder()
                        .setPopUpTo(graph.startDestination, true)
                        .build()
                )
            }
            fragmentManager.beginTransaction().show(selectedFragment).commit()
            _selectedNavController.value = navController
        }

        // check to see if the graph is part of the bottom nav view and if so let it handle it
        bottomNavView?.apply {
            val menuItem = menu.findItem(graph.id)
            if (menuItem != null && selectedItemId != menuItem.itemId) {
                menuItem.isChecked = true
            } else {
                uncheckBottomNavView()
            }
        }

    }

    fun setupWithBottomNavigation(view: BottomNavigationView) {
        bottomNavView = view
        initializeBottomNavView()
    }

    fun setupNavigationView(view: NavigationView) {
        navigationView = view
        initializeNavigationView()
    }

    private fun setupDeepLinks() {
        navGraphIds.forEachIndexed { index, navGraphId ->
            val fragmentTag = getFragmentTag(index)

            val navHostFragment = obtainNavHostFragment(
                fragmentTag,
                navGraphId
            )
            // Handle Intent
            if (navHostFragment.navController.handleDeepLink(intent)) {
                bottomNavView?.apply {
                    val menuItem = menu.findItem(navHostFragment.navController.graph.id)
                    if (menuItem != null && selectedItemId != menuItem.itemId) {
                        selectedItemId = menuItem.itemId
                    }
                }
            }
        }
    }

    private fun initializeBottomNavView() {
        checkNotNull(bottomNavView) { "You must set bottomNavView before initializing" }
        bottomNavView?.apply {
            selectedItemTag = graphIdToTagMap[selectedItemId]
            val firstFragmentTag = graphIdToTagMap[firstFragmentGraphId]
            isOnFirstFragment = selectedItemTag == firstFragmentTag

            val navHostFragment = obtainNavHostFragment(selectedItemTag, selectedItemId)
            attachNavHostFragment(navHostFragment)
            _selectedNavController.value = navHostFragment.navController

            setOnNavigationItemSelectedListener { item ->
                // Don't do anything if the state is state has already been saved.
                if (fragmentManager.isStateSaved) {
                    false
                } else {
                    attach(item.itemId)
                }
            }

            setOnNavigationItemReselectedListener {
                if (!it.isChecked) {
                    it.isChecked = true
                    attach(it.itemId)
                } else {
                    popBackStack(it.itemId)
                }
            }
            fragmentManager.addOnBackStackChangedListener {
                if (!isOnFirstFragment && !fragmentManager.isOnBackStack(firstFragmentTag)) {
                    this.selectedItemId = firstFragmentGraphId
                }

                // Reset the graph if the currentDestination is not valid (happens when the back
                // stack is popped after using the back button).
                selectedNavController.value?.let { controller ->
                    if (controller.currentDestination == null) {
                        controller.navigate(controller.graph.id)
                    }
                }
            }
        }
    }

    private fun initializeNavigationView() {
        checkNotNull(navigationView) { "You must set navigationView before initializing" }
        navigationView?.apply {
            setNavigationItemSelectedListener { item ->
                // Don't do anything if the state is state has already been saved.
                if (fragmentManager.isStateSaved) {
                    false
                } else {
                    // uncheck all in bottom nav view if applicable
                    uncheckBottomNavView()

                    isOnFirstFragment = false
                    obtainNavHostFragment(selectedItemTag, item.itemId)
                    attach(item.itemId)
                }
            }

            fragmentManager.addOnBackStackChangedListener {
                selectedNavController.value?.let { controller ->
                    if (controller.currentDestination == null) {
                        controller.navigate(controller.graph.id)
                    }
                }
            }
        }
    }

    private fun uncheckBottomNavView() {
        bottomNavView?.apply {
            menu.setGroupCheckable(0, true, false)
            for (i in 0 until menu.size()) {
                menu.getItem(i).isChecked = false
            }
            menu.setGroupCheckable(0, true, true)
            selectedItemId = 0
        }
    }

    private fun attach(graphId: Int): Boolean {
        val selectedFragment = attachNoNavControllerChange(graphId)
        selectedFragment?.let {
            _selectedNavController.value = it.navController
            return true
        }
        return false
    }

    private fun attachNoNavControllerChange(graphId: Int): NavHostFragment? {
        val firstFragmentTag = graphIdToTagMap[firstFragmentGraphId]
        val newlySelectedItemTag = graphIdToTagMap[graphId]
        if (selectedItemTag != newlySelectedItemTag) {
            // Pop everything above the first fragment (the "fixed start destination")
            fragmentManager.popBackStack(
                firstFragmentTag,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )
            val selectedFragment = fragmentManager.findFragmentByTag(newlySelectedItemTag)
                    as NavHostFragment

            // Exclude the first fragment tag because it's always in the back stack.
            if (firstFragmentTag != newlySelectedItemTag) {
                // Commit a transaction that cleans the back stack and adds the first fragment
                // to it, creating the fixed started destination.
                fragmentManager.beginTransaction()
                    .setCustomAnimations(
                        R.anim.nav_default_enter_anim,
                        R.anim.nav_default_exit_anim,
                        R.anim.nav_default_pop_enter_anim,
                        R.anim.nav_default_pop_exit_anim
                    )
                    .attach(selectedFragment)
                    .setPrimaryNavigationFragment(selectedFragment)
                    .apply {
                        // Detach all other Fragments
                        graphIdToTagMap.forEach { _, fragmentTagIter ->
                            if (fragmentTagIter != newlySelectedItemTag) {
                                detach(fragmentManager.findFragmentByTag(firstFragmentTag)!!)
                            }
                        }
                    }
                    .addToBackStack(firstFragmentTag)
                    .setReorderingAllowed(true)
                    .commit()
            }
            selectedItemTag = newlySelectedItemTag
            isOnFirstFragment = selectedItemTag == firstFragmentTag
            return selectedFragment
        }
        return null
    }

    private fun detachNavHostFragment(
        navHostFragment: NavHostFragment
    ) {
        fragmentManager.beginTransaction()
            .detach(navHostFragment)
            .commitNow()
    }

    private fun attachNavHostFragment(
        navHostFragment: NavHostFragment
    ) {
        fragmentManager.beginTransaction()
            .attach(navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment)
            .commitNow()
    }

    private fun obtainNavHostFragment(
        fragmentTag: String,
        navGraphId: Int
    ): NavHostFragment {
        // If the Nav Host fragment exists, return it
        val existingFragment = fragmentManager.findFragmentByTag(fragmentTag) as NavHostFragment?
        existingFragment?.let { return it }

        // Otherwise, create it and return it.
        val navHostFragment = NavHostFragment.create(navGraphId)
        fragmentManager.beginTransaction()
            .add(containerId, navHostFragment, fragmentTag)
            .commitNow()
        return navHostFragment
    }

    private fun FragmentManager.isOnBackStack(backStackName: String): Boolean {
        val backStackCount = backStackEntryCount
        for (index in 0 until backStackCount) {
            if (getBackStackEntryAt(index).name == backStackName) {
                return true
            }
        }
        return false
    }

    private fun popBackStack(graphId: Int) {
        val newlySelectedItemTag = graphIdToTagMap[graphId]
        val selectedFragment = fragmentManager.findFragmentByTag(newlySelectedItemTag)
                as NavHostFragment
        val navController = selectedFragment.navController
        // Pop the back stack to the start destination of the current navController graph
        navController.popBackStack(
            navController.graph.startDestination, false
        )
    }

    private fun getFragmentTag(index: Int) = "topLevelNavControllerDestination#$index"


    private fun getDisplayName(context: Context?, id: Int): String {
        return if (id <= 0x00FFFFFF) {
            id.toString()
        } else try {
            context?.resources?.getResourceName(id) ?: id.toString()
        } catch (e: NotFoundException) {
            id.toString()
        }
    }

}