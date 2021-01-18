package com.android.sidewalk.views.authentication

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import com.android.sidewalk.R
import com.android.sidewalk.databinding.ActivityMyAccountsBinding
import com.android.sidewalk.utils.BaseActivity
import com.android.sidewalk.viewmodels.MyAccountsViewModel

class MyAccountsActivity : BaseActivity() {
    lateinit var binding : ActivityMyAccountsBinding
    private var accountsViewModel : MyAccountsViewModel? =
        null

    override fun getLayoutId() : Int {
        return R.layout.activity_my_accounts
    }

    override fun initViews() {
        binding = viewDataBinding as ActivityMyAccountsBinding
        accountsViewModel = ViewModelProviders.of(this)
            .get(MyAccountsViewModel::class.java)
        binding.myaccountsViewModel = accountsViewModel
        binding.toolbarCommon.imgToolbarText.text = getString(R.string.my_account)
        accountsViewModel!!.isClick().observe(
            this, Observer<String>(function =
            fun(it : String?) {
                when (it) {
                    "tv_change_password" -> {
                        val intent1 = Intent(
                            this,
                            ChangePasswrodActivity::class.java
                        )
                        startActivity(intent1)
                    }
                }

            })
        )

    }
}
