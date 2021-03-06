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

package com.here.msdkuiapp.common.mapselection

import android.app.Activity
import com.here.android.mpa.common.GeoCoordinate
import com.here.android.mpa.routing.RouteWaypoint
import com.here.android.mpa.search.*
import com.here.msdkui.routing.WaypointEntry
import com.here.msdkuiapp.CommonContracts
import com.here.msdkuiapp.R
import com.here.msdkuiapp.base.BaseContract
import com.here.msdkuiapp.common.AppActionBar
import com.here.msdkuiapp.common.Provider
import com.here.testutils.BaseTest
import com.here.testutils.anySafe
import com.here.testutils.argumentCaptor
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

/**
 * Tests for [WaypointSelectionPresenter].
 */
class WaypointSelectionPresenterTest : BaseTest() {

    private lateinit var presenter: WaypointSelectionPresenter

    @Mock
    private lateinit var mockContractImpl: BaseContract<CommonContracts.WaypointSelection>

    @Mock
    private lateinit var mockContract: CommonContracts.WaypointSelection

    @Mock
    private lateinit var mockProvider: Provider

    @Before
    override fun setUp() {
        super.setUp()
        MockitoAnnotations.initMocks(this)
        presenter = WaypointSelectionPresenter()
        presenter.provider = mockProvider
        `when`(mockContractImpl.getCurrentViewContract()).thenReturn(mockContract)
        presenter.onAttach(activityContext!!, mockContractImpl)
    }

    @Test
    fun testActionBar() {
        val captorBackClickListener = argumentCaptor<() -> Unit>()
        val captorRightIconClickListener = argumentCaptor<() -> Unit>()
        val actionBar = mock(AppActionBar::class.java)
        val mockActivity = mock(Activity::class.java)
        `when`(actionBar.activity).thenReturn(mockActivity)

        presenter.setUpActionBar(actionBar)
        verify(actionBar).setBack(anyBoolean(), anyInt(), captorBackClickListener.capture())
        verify(actionBar).setTitle(anyBoolean(), anyString(), anyBoolean())
        verify(actionBar).setRightIcon(anyBoolean(), anyInt(), anyString(), captorRightIconClickListener.capture())

        captorBackClickListener.value.invoke()
        verify(mockContract).onBackClicked(anySafe(), anySafe())
        verify(mockActivity).onBackPressed()

        captorRightIconClickListener.value.invoke()
        verify(mockContract).onRightIconClicked(anySafe(), anySafe())
    }

    @Test
    fun testUiUpdates() {
        var entry = WaypointEntry(R.string.msdkui_waypoint_select_location)
        presenter.updatePosition(index = 0, entry = entry)
        presenter.updateUI()

        entry = WaypointEntry("")
        entry.stringLabel = null
        presenter.updatePosition(index = 0, entry = entry)
        presenter.updateUI()

        verify(mockContract, times(2)).onUiUpdate(anyString(), eq(false))

        entry = WaypointEntry(mock(RouteWaypoint::class.java, RETURNS_DEEP_STUBS))
        entry.stringLabel = "somevalue"
        presenter.updatePosition(index = 0, entry = entry)
        presenter.updateUI()

        entry = WaypointEntry(mock(RouteWaypoint::class.java, RETURNS_DEEP_STUBS))
        entry.resourceIdLabel = R.string.msdkui_app_name_title
        presenter.updatePosition(index = 0, entry = entry)
        presenter.updateUI()

        entry = WaypointEntry(mock(RouteWaypoint::class.java, RETURNS_DEEP_STUBS), null)
        presenter.updatePosition(index = 0, entry = entry)
        presenter.updateUI()

        verify(mockContract, times(3)).onUiUpdate(anyString(), eq(true))
    }

    @Test
    fun testUpdateCord() {
        presenter.updatePosition(index = 0, entry = WaypointEntry(""))
        val errorCode = ErrorCode.NONE
        val location = mock(Location::class.java)
        val address = mock(Address::class.java)
        `when`(address.text).thenReturn("sometext")
        `when`(location.address).thenReturn(address)

        val mockRouteWaypoint = mock(RouteWaypoint::class.java)
        `when`(mockProvider.providesRouteWaypoint(anySafe())).thenReturn(mockRouteWaypoint)
        val request = mock(ReverseGeocodeRequest2::class.java)
        `when`(mockProvider.providesReverseGeocodeRequest(anySafe())).thenReturn(request)

        val cord = mock(GeoCoordinate::class.java)
        `when`(cord.isValid).thenReturn(true)
        presenter.setGeoCoordinateForWaypoint(cord)
        verify(mockContract).onProgress(eq(true))
        val captor = argumentCaptor<ResultListener<Location>>()
        verify(request).execute(captor.capture())

        captor.value.onCompleted(mock(Location::class.java, RETURNS_DEEP_STUBS), ErrorCode.NONE)
        verify(mockContract).onProgress(eq(false))
        verify(mockContract).onUiUpdate(anyString(), anyBoolean())
    }
}