package fr.myticket.moov.checker.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.myticket.moov.checker.utils.goForwardAnimation
import fr.myticket.moov.checker.databinding.ActivityMainBinding
import fr.myticket.moov.checker.ui.scan_qr.BarcodeScanningActivity

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUI()
        setUpClicks()
    }

    private fun setUpClicks() {
        binding.buttonScan.setOnClickListener {
            val intent = Intent(this, BarcodeScanningActivity::class.java)
            startActivity(intent)
            goForwardAnimation()
        }
    }

    private fun setUpUI() {

    }
}