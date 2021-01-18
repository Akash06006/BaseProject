package com.android.sidewalk.views.contactus

import androidx.lifecycle.ViewModelProviders
import com.android.sidewalk.R
import com.android.sidewalk.databinding.ActivityContactUsBinding
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.viewmodels.ContactUsViewModel

class ContactUsActivity : BaseActivity() {
    lateinit var binding : ActivityContactUsBinding
    private var contactUsViewModel : ContactUsViewModel? = null

    override fun initViews() {
        binding = viewDataBinding as ActivityContactUsBinding
        contactUsViewModel = ViewModelProviders.of(this)
            .get(ContactUsViewModel::class.java)
        binding.contactUsViewModel = contactUsViewModel
        binding.toolbarCommon.imgToolbarText.text = getString(R.string.contact_us)

    }

    override fun getLayoutId() : Int {
        return R.layout.activity_contact_us
    }

}
