package com.android.qrcodescanner.ui.qr_code_details

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.qrcodescanner.Enums.EnumTags
import dagger.hilt.android.AndroidEntryPoint
import com.android.qrcodescanner.databinding.ActivityQrCodeDetailsBinding
import com.android.qrcodescanner.utils.goBackAnimation
import java.util.*

@AndroidEntryPoint
class QrCodeDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityQrCodeDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpIntent()
        setUpClicks()
    }

    private fun setUpClicks() {
        binding.ivBack.setOnClickListener {
            goBackAnimation()
            finish()
        }

        binding.buttonRescan.setOnClickListener {
            finish()
            goBackAnimation()
        }

    }


    private fun updateLoading(showing: Boolean) {

    }


    private fun setUpIntent() {
        if (intent.hasExtra(EnumTags.DETAILS.value)) {
            val detailsObj = intent.getStringExtra(EnumTags.DETAILS.value)
            setUpUI(detailsObj)
        }
    }

    private fun setUpUI(detailsObj: String?) {
        binding.tvTitle.text = detailsObj ?: ""
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goBackAnimation()
    }
}