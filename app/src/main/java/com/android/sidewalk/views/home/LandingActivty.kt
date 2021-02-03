package com.android.sidewalk.views.home

import androidx.fragment.app.Fragment
import com.android.sidewalk.R
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.databinding.ActivityLandingActivtyBinding
import com.android.sidewalk.views.events.EventsListFragment
import com.android.sidewalk.views.home.fragments.HomeFragment
import com.android.sidewalk.views.profile.ProfileFragment
import com.android.sidewalk.views.trucks.TruckListFragment
import com.google.android.material.tabs.TabLayout

class LandingActivty : BaseActivity() {
    private lateinit var activityLandingActivtyBinding : ActivityLandingActivtyBinding
    var pos = 0
    override fun getLayoutId() : Int {
        return R.layout.activity_landing_activty
    }

    override fun onBackPressed() {
        // super.onBackPressed()
        if (pos == 0) {
            finish()
        } else {
            val fragment = HomeFragment()
            this.callFragments(
                fragment,
                supportFragmentManager,
                false,
                "send_data",
                ""
            )
            activityLandingActivtyBinding!!.tablayout.getTabAt(0)?.select()

        }
    }

    override fun initViews() {
        activityLandingActivtyBinding = viewDataBinding as ActivityLandingActivtyBinding
        val fragment = HomeFragment()
        callFragments(fragment, supportFragmentManager, false, "send_data", "")
        activityLandingActivtyBinding!!.tablayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab : TabLayout.Tab?) {
                activityLandingActivtyBinding!!.tablayout!!.getTabAt(0)!!
                    .setIcon(resources.getDrawable(R.drawable.ic_home_tab))
                activityLandingActivtyBinding!!.tablayout!!.getTabAt(1)!!
                    .setIcon(resources.getDrawable(R.drawable.ic_truck_tab))
                activityLandingActivtyBinding!!.tablayout!!.getTabAt(2)!!
                    .setIcon(resources.getDrawable(R.drawable.ic_event_tab))
                activityLandingActivtyBinding!!.tablayout!!.getTabAt(3)!!
                    .setIcon(resources.getDrawable(R.drawable.ic_setting_tab))
                var fragment : Fragment? = null
                fragment =
                    HomeFragment()
                //   activityDashboardBinding!!.toolbarCommon.imgRight.visibility = View.GONE
                when (tab!!.position) {
                    0 -> {
                        pos = 0
                        activityLandingActivtyBinding!!.tablayout!!.getTabAt(0)!!
                            .setIcon(resources.getDrawable(R.drawable.ic_home_tab_selected))
                        fragment =
                            HomeFragment()
                    }
                    1 -> {
                        pos = 1
                        fragment =
                            TruckListFragment()
                        activityLandingActivtyBinding!!.tablayout!!.getTabAt(1)!!
                            .setIcon(resources.getDrawable(R.drawable.ic_truck_tab_selected))
                    }
                    2 -> {
                        pos = 2
                        fragment =
                            EventsListFragment()
                        activityLandingActivtyBinding!!.tablayout!!.getTabAt(2)!!
                            .setIcon(resources.getDrawable(R.drawable.ic_event_tab_selected))
                    }
                    3 -> {
                        pos = 3
                        fragment =
                            ProfileFragment()
                        activityLandingActivtyBinding!!.tablayout!!.getTabAt(3)!!
                            .setIcon(resources.getDrawable(R.drawable.ic_setting_tab_selected))
                    }
                }
                callFragments(fragment, supportFragmentManager, false, "send_data", "")
                /* Handler().postDelayed({
                     setHeadings()
                 }, 300)*/

            }

            override fun onTabUnselected(tab : TabLayout.Tab?) {

            }

            override fun onTabReselected(tab : TabLayout.Tab?) {
                //var fragment : Fragment? = null
                //Not In use
            }
        })

    }

    fun callTruckFragment() {
        pos = 1
        val fragment =
            TruckListFragment()
        callFragments(fragment, supportFragmentManager, false, "send_data", "")
        activityLandingActivtyBinding!!.tablayout.getTabAt(1)!!.select()
    }

    fun callEventsFragment() {
        pos = 2
        val fragment =
            EventsListFragment()
        callFragments(fragment, supportFragmentManager, false, "send_data", "")
        activityLandingActivtyBinding!!.tablayout.getTabAt(2)!!.select()
    }

}
