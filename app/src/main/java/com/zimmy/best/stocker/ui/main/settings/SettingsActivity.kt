package com.zimmy.best.stocker.ui.main.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zimmy.best.stocker.R
import com.zimmy.best.stocker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}