package fr.myticket.moov.checker.ui.qr_code_details

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import fr.myticket.moov.checker.Enums.EnumTags
import fr.myticket.moov.checker.R
import fr.myticket.moov.checker.databinding.ActivityQrCodeDetailsBinding
import fr.myticket.moov.checker.models.DetailsModel
import fr.myticket.moov.checker.ui.MainActivity
import fr.myticket.moov.checker.utils.Constants
import fr.myticket.moov.checker.utils.goBackAnimation
import fr.myticket.moov.checker.utils.gone
import fr.myticket.moov.checker.utils.visible
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class QrCodeDetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityQrCodeDetailsBinding
    lateinit var details: DetailsModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrCodeDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpIntent()
        setUpClicks()
    }

    private fun setUpClicks() {
        binding.ivBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
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
            details = Gson().fromJson(detailsObj, object :TypeToken<DetailsModel>(){}.type)
            setUpUI()
        }
    }

    private fun setUpUI() {

        when(details.error){
            EnumTags.ALREADY_CHECKED.value->{
                binding.ivError.visible()
            }
            else->{
                binding.ivError.gone()
            }
        }

        binding.tvTitle.text = details.ticket_title
        binding.tvChecked.text = details.message

        binding.tvName.text = details.title
        binding.tvTicketId.text = "#${details.ticket.id}"
        binding.tvTarif.text = details.ticket.title
        binding.tvPrice.text = "$${details.ticket.raw_price}"
        binding.tvEmail.text = details.email

        val mDate = SimpleDateFormat(Constants.FORMATTED_DATE2).parse(details.date)

        val outputDateFormat = SimpleDateFormat(
            "EEEE dd MMMM",
            Locale("fr", "FR")
        )

        val createdAt = outputDateFormat.format(mDate)

        binding.tvDate.text = createdAt

        if (details.is_purchaser){
            binding.tvCompleted.text = getString(R.string.completed)
        }else{
            binding.tvCompleted.text = getString(R.string.not_completed)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goBackAnimation()
    }
}