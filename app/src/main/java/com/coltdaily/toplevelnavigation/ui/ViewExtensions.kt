package com.coltdaily.toplevelnavigation.ui

import androidx.fragment.app.Fragment
import com.coltdaily.toplevelnavigation.ui.navigation.TopLevelNavController

/**
 * Created by Colt Daily on 3/6/20.
 */

fun Fragment.findTopLevelNavController(): TopLevelNavController {
    val currentActivity = activity
    check(currentActivity is TopLevelNavigator) { "Activity is not a top level navigator!" }
    return currentActivity.getTopLevelNavController()
}