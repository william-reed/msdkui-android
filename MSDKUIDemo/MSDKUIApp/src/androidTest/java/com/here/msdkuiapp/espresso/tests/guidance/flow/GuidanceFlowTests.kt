/*
 * Copyright (C) 2017-2019 HERE Europe B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.here.msdkuiapp.espresso.tests.guidance.flow

import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.here.msdkuiapp.R
import com.here.msdkuiapp.SplashActivity
import com.here.msdkuiapp.espresso.impl.annotation.FunctionalUITest
import com.here.msdkuiapp.espresso.impl.core.CoreActions
import com.here.msdkuiapp.espresso.impl.core.CoreMatchers
import com.here.msdkuiapp.espresso.impl.core.CoreMatchers.getText
import com.here.msdkuiapp.espresso.impl.core.CoreMatchers.waitForCondition
import com.here.msdkuiapp.espresso.impl.core.CoreView
import com.here.msdkuiapp.espresso.impl.core.CoreView.onRootView
import com.here.msdkuiapp.espresso.impl.testdata.Constants.GEO_POINT_5
import com.here.msdkuiapp.espresso.impl.testdata.Constants.ScreenOrientation
import com.here.msdkuiapp.espresso.impl.testdata.Constants.ScreenOrientation.PORTRAIT
import com.here.msdkuiapp.espresso.impl.views.drivenavigation.matchers.DriveNavigationMatchers
import com.here.msdkuiapp.espresso.impl.views.drivenavigation.matchers.DriveNavigationMatchers.checkCurrentStreetViewValueChanged
import com.here.msdkuiapp.espresso.impl.views.drivenavigation.matchers.DriveNavigationMatchers.checkManeuverPanelData
import com.here.msdkuiapp.espresso.impl.views.drivenavigation.matchers.DriveNavigationMatchers.checkManeuverPanelIsDestinationReached
import com.here.msdkuiapp.espresso.impl.views.drivenavigation.matchers.DriveNavigationMatchers.waitForETAChanged
import com.here.msdkuiapp.espresso.impl.views.drivenavigation.screens.DriveNavigationBarView.onDriveNavigationBarTitleView
import com.here.msdkuiapp.espresso.impl.views.drivenavigation.screens.DriveNavigationView.onRouteOverviewSeeManoeuvresNaviBtn
import com.here.msdkuiapp.espresso.impl.views.drivenavigation.screens.DriveNavigationView.onRouteOverviewStartNaviBtn
import com.here.msdkuiapp.espresso.impl.views.drivenavigation.useractions.DriveNavigationActions
import com.here.msdkuiapp.espresso.impl.views.drivenavigation.useractions.DriveNavigationBarActions
import com.here.msdkuiapp.espresso.impl.views.drivenavigation.utils.ManeuversActions
import com.here.msdkuiapp.espresso.impl.views.guidance.screens.GuidanceView.onGuidanceCurrentStreetInfoText
import com.here.msdkuiapp.espresso.impl.views.guidance.screens.GuidanceView.onGuidanceDashBoardPullLine
import com.here.msdkuiapp.espresso.impl.views.guidance.screens.GuidanceView.onGuidanceNextManeuverStreetNameInfo
import com.here.msdkuiapp.espresso.impl.views.guidance.useractions.GuidanceActions
import com.here.msdkuiapp.espresso.impl.views.map.screens.MapView
import com.here.msdkuiapp.espresso.impl.views.map.useractions.MapActions
import com.here.msdkuiapp.espresso.impl.views.routeplanner.useractions.RoutePlannerBarActions
import com.here.msdkuiapp.espresso.tests.TestBase
import org.junit.*
import org.junit.runners.MethodSorters

/**
 * UI flow tests for guidance
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class GuidanceFlowTests : TestBase<SplashActivity>(SplashActivity::class.java) {

    private val destination = GEO_POINT_5

    @Before
    fun prepare() {
        // Set current location for guidance test
        CoreActions().setCurrentLocation()
    }

    @After
    fun tearDown() {
        CoreActions().setCurrentLocation(true)
        CoreActions().changeOrientation(PORTRAIT)
    }

    /**
     * MSDKUI-573: Select Drive Navigation destination via tap
     */
    @Test
    @FunctionalUITest
    fun testSelectDriveNavigationDestinationViaTap() {
        enumValues<ScreenOrientation>().forEach {
            // Set screen orientation: PORTRAIT / LANDSCAPE
            CoreActions().changeOrientation(it)
            // Wait for drive navigation icon
            waitForCondition(CoreView.onLandingScreenDriverNavigationViewIcon)
            //Enter Drive Navigation
            CoreActions().enterDriveNavigation()
            // Check drive navigation view opened
            DriveNavigationBarActions.waitForDriveNavigationView()
            // Check drive navigation view title
            onDriveNavigationBarTitleView.check(matches(isDisplayed()))
            // Check until default destination label displayed on drive navigation
            DriveNavigationBarActions.waitForDestinationDisplayed().checkDestinationDefaultLabel()
            // Check map view displayed and tap anywhere on map view
            MapActions.waitForMapViewEnabled()
                    .tap(destination)
            // Wait until destination location selected
            DriveNavigationBarActions.waitForDestinationNotDisplayed()
            // Returns back to Landing screen
            CoreActions().pressBackButton()
        }
    }

    /**
     * MSDKUI-574: Select Drive Navigation destination via long press
     */
    @Test
    @FunctionalUITest
    fun testSelectDriveNavigationDestinationViaLongPress() {
        enumValues<ScreenOrientation>().forEach {
            // Set screen orientation: PORTRAIT / LANDSCAPE
            CoreActions().changeOrientation(it)
            // Wait for drive navigation icon
            waitForCondition(CoreView.onLandingScreenDriverNavigationViewIcon)
            //Enter Drive Navigation
            CoreActions().enterDriveNavigation()
            // Check drive navigation view opened
            DriveNavigationBarActions.waitForDriveNavigationView()
            // Check drive navigation view title
            onDriveNavigationBarTitleView.check(matches(isDisplayed()))
            // Check until default destination label displayed on drive navigation
            DriveNavigationBarActions.waitForDestinationDisplayed().checkDestinationDefaultLabel()
            // Tap anywhere on map view
            MapActions.waitForMapViewEnabled()
                    .longTap(destination)
            // Wait until destination location selected
            DriveNavigationBarActions.waitForDestinationNotDisplayed()
            // Returns back to Landing screen
            CoreActions().pressBackButton()
        }
    }

    /**
     * MSDKUI-661: Guidance route overview
     */
    @Test
    @FunctionalUITest
    fun testGuidanceRouteOverview() {
            //Enter Drive Navigation
            CoreActions().enterDriveNavigation()
        enumValues<ScreenOrientation>().forEach {
            // Set screen orientation: PORTRAIT / LANDSCAPE
            CoreActions().changeOrientation(it)
            // Wait for drive navigation icon
            waitForCondition(CoreView.onLandingScreenDriverNavigationViewIcon)
            // Check drive navigation view opened
            DriveNavigationBarActions.waitForDriveNavigationView()
                    .waitForDestinationDisplayed()
            // Tap anywhere on map view
            MapActions.waitForMapViewEnabled()
                    .tap(destination)
            // Tap on tick to confirm the first waypoint selection
            DriveNavigationBarActions.waitForDestinationNotDisplayed()
            val location = RoutePlannerBarActions.tapOnTickButton()
            // Check route description and destination items on guidance route overview
            DriveNavigationBarActions.waitForRouteOverView()
                    .waitForGuidanceDescriptionDisplayed()
                    .checkRouteOverviewDescription()
                    .checkRouteOverviewDestination(location)
            // Check 'See manoeuvres' & 'Start navigation' button displayed
            onRouteOverviewSeeManoeuvresNaviBtn.check(matches(isDisplayed()))
            onRouteOverviewStartNaviBtn.check(matches(isDisplayed()))
            // Returns back to the Drive Navigation
            CoreActions().pressBackButton()
            // Check drive navigation view opened
            DriveNavigationBarActions.waitForDriveNavigationView()
        }
        // Returns back to Landing screen
        CoreActions().pressBackButton()
    }

    /**
     * MSDKUI-576: Maneuver panel, switch orientation while navigation simulation is ongoing
     */
    @Test
    @FunctionalUITest
    fun testSwitchOrientationWhileNavigationSimulationIsOngoing() {
        //Enter Drive Navigation
        CoreActions().enterDriveNavigation()
        // Check drive navigation view opened
        DriveNavigationBarActions.waitForDriveNavigationView()
                .waitForDestinationDisplayed()
        // Tap anywhere on map view
        MapActions.waitForMapViewEnabled().tap(destination)
        // Tap on tick to confirm the first waypoint selection
        DriveNavigationBarActions.waitForDestinationNotDisplayed()
        RoutePlannerBarActions.tapOnTickButton()
        // Check guidance route overview view opened
        DriveNavigationBarActions.waitForRouteOverView()
                .waitForGuidanceDescriptionDisplayed()
        // Start navigation simulation
        DriveNavigationActions.startNavigationSimulation()
        enumValues<ScreenOrientation>().forEach {
            // Set screen orientation: PORTRAIT / LANDSCAPE
            CoreActions().changeOrientation(it)
            // Wait for map view
            waitForCondition(MapView.onMapFragmentWrapperView)
            // Wait for maneuver panel initial state to pass
            GuidanceActions.waitForManeuverInitialStateToPass()
            // Check maneuver panel and dashboard is displayed
            GuidanceActions.checkGuidanceManeuverPanelInfo()
            GuidanceActions.checkGuidanceDashBoardInfo()
        }
        GuidanceActions.tapOnStopNavigationBtn()
    }

    /**
     * MSDKUI-575: Maneuver panel
     */
    @Test
    @FunctionalUITest
    fun testManeuverPanel() {
        enumValues<ScreenOrientation>().forEach {
            // Set screen orientation: PORTRAIT / LANDSCAPE
            CoreActions().changeOrientation(it)
            // Wait for drive navigation icon
            waitForCondition(CoreView.onLandingScreenDriverNavigationViewIcon)
            //Enter Drive Navigation
            CoreActions().enterDriveNavigation()
            // Check drive navigation view opened
            DriveNavigationBarActions.waitForDriveNavigationView()
                    .waitForDestinationDisplayed()
            // Tap anywhere on map view to define destination
            MapActions.waitForMapViewEnabled().tap(destination)
            // Tap on tick to confirm the first waypoint selection
            DriveNavigationBarActions.waitForDestinationNotDisplayed()
            RoutePlannerBarActions.tapOnTickButton()
            // Check guidance route overview view opened
            DriveNavigationBarActions.waitForRouteOverView()
                    .waitForGuidanceDescriptionDisplayed()
            // Click "See maneuvers" to open maneuvers list
            DriveNavigationActions.tapOnSeeManeuversBtn()
            // Save maneuvers information from maneuvers list
            val maneuversDataList = ManeuversActions.getManeuversDataFromManeuversList()
            // Start simulation
            DriveNavigationActions.startNavigationSimulation(30)
            // Wait for maneuver panel initial state to pass
            GuidanceActions.waitForManeuverInitialStateToPass()
            // During simulation first maneuver is skipped,
            // so let's make sure that there is more than one maneuver.
            val maneuversCount = maneuversDataList.size
            if (maneuversCount > 1) {
                for (index in 1 until maneuversCount) {
                    val maneuverAddress = maneuversDataList[index].address
                    val maneuverIconTag = maneuversDataList[index].iconTag
                    checkManeuverPanelData(maneuverAddress, maneuverIconTag)
                }
                // Check is destination reached (by checking address text color)
                checkManeuverPanelIsDestinationReached(maneuversDataList[maneuversCount - 1].address)
            }
            // Stop navigation and enter navigation again to test maneuver panel on different screen orientation.
            GuidanceActions.tapOnStopNavigationBtn()
        }
    }

    /**
     * MSDKUI-1478: Current street label in Guidance
     */
    @Test
    @FunctionalUITest
    fun testCurrentStreetLabelInGuidanceSimulation() {
        enumValues<ScreenOrientation>().forEach {
        // Set screen orientation: PORTRAIT / LANDSCAPE
        CoreActions().changeOrientation(it)
        // Wait for drive navigation icon
        waitForCondition(CoreView.onLandingScreenDriverNavigationViewIcon)
        //Enter Drive Navigation
        CoreActions().enterDriveNavigation()
        // Check drive navigation view opened
        DriveNavigationBarActions.waitForDriveNavigationView()
                .waitForDestinationDisplayed()
        // Tap anywhere on map view
        MapActions.waitForMapViewEnabled().tap(destination)
        // Tap on tick to confirm the first waypoint selection
        DriveNavigationBarActions.waitForDestinationNotDisplayed()
        RoutePlannerBarActions.tapOnTickButton()
        // Check guidance route overview view opened
        DriveNavigationBarActions.waitForRouteOverView()
                .waitForGuidanceDescriptionDisplayed()
        // Start navigation simulation
        DriveNavigationActions.startNavigationSimulation()
            // Check that current street view is displayed and store its current value
            onGuidanceCurrentStreetInfoText.check(matches(isDisplayed()))
            val currentStreetData = getText(onGuidanceCurrentStreetInfoText)
            // Check that current street view value changed
            checkCurrentStreetViewValueChanged(currentStreetData)
            GuidanceActions.tapOnStopNavigationBtn()
        }
    }

    /**
     * MSDKUI-1251: Next-next maneuver panel
     */
    @Test
    @FunctionalUITest
    fun testNextNextManeuverPanel() {
        enumValues<ScreenOrientation>().forEach {
            // Set screen orientation: PORTRAIT / LANDSCAPE
            CoreActions().changeOrientation(it)
            // Wait for drive navigation icon
            waitForCondition(CoreView.onLandingScreenDriverNavigationViewIcon)
            //Enter Drive Navigation
            CoreActions().enterDriveNavigation()
            // Check drive navigation view opened
            DriveNavigationBarActions.waitForDriveNavigationView()
                    .waitForDestinationDisplayed()
            // Tap destination on map
            MapActions.waitForMapViewEnabled().tap(destination)
            // Tap on tick to confirm destination selection
            DriveNavigationBarActions.waitForDestinationNotDisplayed()
            RoutePlannerBarActions.tapOnTickButton()
            // Check guidance route overview view opened
            DriveNavigationBarActions.waitForRouteOverView()
                    .waitForGuidanceDescriptionDisplayed()
            // Start navigation simulation
            DriveNavigationActions.startNavigationSimulation()
            // Check next-next maneuver panel elements
            GuidanceActions.waitForGuidanceNextManeuverPanelDisplayed()
            DriveNavigationMatchers.checkNextManeuverPanelElementsDisplayed()
            // Wait for maneuver info to change
            val streetName = CoreMatchers.getText(onGuidanceNextManeuverStreetNameInfo)
            GuidanceActions.waitForGuidanceNextManeuverChanged(streetName)
            // Recheck next-next maneuver panel elements
            DriveNavigationMatchers.checkNextManeuverPanelElementsDisplayed()
            // Stop guidance
            GuidanceActions.tapOnStopNavigationBtn()
        }
    }

    /**
     * MSDKUI-1276: Dashboard overview in Guidance
     */
    @Test
    @FunctionalUITest
    fun testDashboardOverviewInGuidanceSimulation() {
            // Wait for drive navigation icon
            waitForCondition(CoreView.onLandingScreenDriverNavigationViewIcon)
            //Enter Drive Navigation
            CoreActions().enterDriveNavigation()
            // Check drive navigation view opened
            DriveNavigationBarActions.waitForDriveNavigationView()
                    .waitForDestinationDisplayed()
            // Tap anywhere on map view
            MapActions.waitForMapViewEnabled().tap(destination)
            // Tap on tick to confirm the first waypoint selection
            DriveNavigationBarActions.waitForDestinationNotDisplayed()
            RoutePlannerBarActions.tapOnTickButton()
            // Check guidance route overview view opened
            DriveNavigationBarActions.waitForRouteOverView()
                    .waitForGuidanceDescriptionDisplayed()
            // Start navigation simulation
            DriveNavigationActions.startNavigationSimulation()
        enumValues<ScreenOrientation>().forEach {
            // Set screen orientation: PORTRAIT / LANDSCAPE
            CoreActions().changeOrientation(it)
            // Check that dashboard components are visible
            GuidanceActions.checkGuidanceDashBoardInfo()
            // Expand dashboard by tap and check
            onGuidanceDashBoardPullLine.perform(click())
            DriveNavigationMatchers.checkGuidanceDashBoardExpanded()
            // Collapse dashboard by tap and check
            onGuidanceDashBoardPullLine.perform(click())
            DriveNavigationMatchers.checkGuidanceDashBoardExpanded(false)
            // Expand dashboard by swipe
            onGuidanceDashBoardPullLine.perform(ViewActions.swipeUp())
            DriveNavigationMatchers.checkGuidanceDashBoardExpanded()
            // Collapse dashboard by tap and check
            onGuidanceDashBoardPullLine.perform(swipeDown())
            DriveNavigationMatchers.checkGuidanceDashBoardExpanded(false)
        }
        // Stop the guidance
        GuidanceActions.tapOnStopNavigationBtn()
    }

    /**
     * MSDKUI-1274: Display the current speed and speed limit warning
     */
    @Ignore("FIXME: MSDKUI-1816")
    @Test
    @FunctionalUITest
    fun testDisplayCurrentSpeedAndSpeedLimitWarning() {
            for (isOverSpeedEnabled in listOf(true, false)) {
                enumValues<ScreenOrientation>().forEach {
                // Set screen orientation: PORTRAIT / LANDSCAPE
                CoreActions().changeOrientation(it)
                // Wait for drive navigation icon
                waitForCondition(CoreView.onLandingScreenDriverNavigationViewIcon)
                //Enter Drive Navigation
                CoreActions().enterDriveNavigation()
                // Check drive navigation view opened
                DriveNavigationBarActions.waitForDriveNavigationView()
                // Tap destination point
                MapActions.waitForMapViewEnabled().tap(destination)
                // Tap on tick to confirm the first waypoint selection
                DriveNavigationBarActions.waitForDestinationNotDisplayed()
                RoutePlannerBarActions.tapOnTickButton()
                // Check guidance route overview view opened
                DriveNavigationBarActions.waitForRouteOverView()
                        .waitForGuidanceDescriptionDisplayed()
                // Start navigation simulation
                if (isOverSpeedEnabled) {
                    DriveNavigationActions.startNavigationSimulation(30)
                } else {
                    DriveNavigationActions.startNavigationSimulation()
                }
                    // Wait for speed to display
                    GuidanceActions.waitForCurrentSpeed()
                DriveNavigationMatchers.checkCurrentSpeed(isOverSpeedEnabled, it)
                               .checkSpeedUnitsDisplayed(isOverSpeedEnabled, it)
                // Stop guidance
                GuidanceActions.tapOnStopNavigationBtn()
                }
            }
    }

    /**
     * MSDKUI-1476: Check Estimated arrival data during guidance
     */
    @Test
    @FunctionalUITest
    fun testEstimatedArrivalInformation() {
        //Enter Drive Navigation
        CoreActions().enterDriveNavigation()
        // Check drive navigation view opened
        DriveNavigationBarActions.waitForDriveNavigationView()
                .waitForDestinationDisplayed()
        // Tap anywhere on map view
        MapActions.waitForMapViewEnabled().tap(destination)
        // Tap on tick to confirm the first waypoint selection
        DriveNavigationBarActions.waitForDestinationNotDisplayed()
        RoutePlannerBarActions.tapOnTickButton()
        // Check guidance route overview view opened
        DriveNavigationBarActions.waitForRouteOverView()
                .waitForGuidanceDescriptionDisplayed()
        // Start navigation simulation
        DriveNavigationActions.startNavigationSimulation(30)
        // Wait for map view to be present
        waitForCondition(MapView.onMapFragmentWrapperView)
        // Wait for ETA to start displaying
        DriveNavigationActions.waitForETAData(true)
        // In case of miles used for distance, wait for it to pass
        onRootView.perform(CoreMatchers.waitForTextChange(withId(R.id.distance),
                "1" + CoreMatchers.getTextById(R.string.msdkui_unit_mile),
                checkInterval = 100))
        // Wait for ETA to change during guidance
        onRootView.perform(waitForETAChanged())
        // Wait for ETA to not display (guidance to finish)
        DriveNavigationActions.waitForETAData(false)
        GuidanceActions.tapOnStopNavigationBtn()
    }
}