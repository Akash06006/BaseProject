package com.android.sidewalk.views.home

import androidx.fragment.app.Fragment
import com.android.sidewalk.R
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.databinding.ActivityLandingActivtyBinding
import com.android.sidewalk.views.home.fragments.HomeFragment
import com.android.sidewalk.views.profile.ProfileFragment
import com.android.sidewalk.views.trucks.TruckListFragment
import com.google.android.material.tabs.TabLayout

class LandingActivty : BaseActivity() {
    private lateinit var activityOtpVerificationBinding : ActivityLandingActivtyBinding

    override fun getLayoutId() : Int {
        return R.layout.activity_landing_activty
    }

    override fun initViews() {
        activityOtpVerificationBinding = viewDataBinding as ActivityLandingActivtyBinding
        val fragment = HomeFragment()
        callFragments(fragment, supportFragmentManager, false, "send_data", "")
        activityOtpVerificationBinding!!.tablayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab : TabLayout.Tab?) {
                var fragment : Fragment? = null
                fragment =
                    HomeFragment()
                //   activityDashboardBinding!!.toolbarCommon.imgRight.visibility = View.GONE
                when (tab!!.position) {
                    0 -> {
                        /* activityOtpVerificationBinding!!.toolbarCommon.imgToolbarText.setText(
                             resources.getString(
                                 R.string.home
                             )
                         )*/
                        fragment =
                            HomeFragment()
                    }
                    1 -> {
                        fragment =
                            TruckListFragment()
                    }
                    2 -> {
                        showToastSuccess("Coming Soon")
                    }
                    3 -> {
                        /*  activityOtpVerificationBinding!!.toolbarCommon.imgToolbarText.setText(
                              resources.getString(
                                  R.string.profile
                              )
                          )*/
                        fragment =
                            ProfileFragment()
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

}
