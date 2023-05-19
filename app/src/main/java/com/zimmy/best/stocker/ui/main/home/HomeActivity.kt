package com.zimmy.best.stocker.ui.main.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import com.google.android.material.navigation.NavigationBarView
import com.zimmy.best.stocker.R
import com.zimmy.best.stocker.databinding.ActivityHomeBinding
import com.zimmy.best.stocker.ui.main.home.ui.HomeFragment
import com.zimmy.best.stocker.ui.main.home.ui.ProfileFragment
import com.zimmy.best.stocker.ui.main.home.ui.TradeFragment
import com.zimmy.best.stocker.ui.main.home.ui.WalletFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.commit {
                        replace(binding.container.id, HomeFragment.newInstance())
                    }
                }

                R.id.navigation_wallet -> {
                    supportFragmentManager.commit {
                        replace(binding.container.id, WalletFragment.newInstance())
                    }
                }

                R.id.navigation_trade -> {
                    supportFragmentManager.commit {
                        replace(binding.container.id, TradeFragment.newInstance())
                    }
                }

                R.id.navigation_profile -> {
                    supportFragmentManager.commit {
                        replace(binding.container.id, ProfileFragment.newInstance())
                    }
                }
            }
            true
        }
    }
}