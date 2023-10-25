package fr.myticket.moov.checker.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.ajicreative.dtc.utils.collectLatestLifecycleFlow
import com.ajicreative.dtc.utils.goForwardAnimation
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import fr.myticket.moov.checker.Enums.EnumTags
import fr.myticket.moov.checker.R
import fr.myticket.moov.checker.databinding.ActivityLoginBinding
import fr.myticket.moov.checker.models.EventUI
import fr.myticket.moov.checker.ui.MainActivity


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpUI()
        setUpInputs()
        collectData()
        setUpClicks()
    }

    private fun setUpUI() {
        binding.etEmailLogin.binding.etInput.inputType = InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
        binding.etPasswordLogin.binding.etInput.transformationMethod = PasswordTransformationMethod()
    }

    private fun setUpClicks() {
        binding.buttonLogin.setOnClickListener {
            viewModel.login()
        }
    }

    private fun collectData() {
        collectLatestLifecycleFlow(viewModel.sharedFlowLogin){
            when(it){
                is EventUI.OnLoading->{
                    updateLoading(it.isShowing)
                }
                is EventUI.OnSuccess->{
                    goToHome()
                }
                is EventUI.OnError->{
                    Snackbar.make(binding.root, it.message, 1500)
                        .setTextColor(ContextCompat.getColor(this, R.color.textColor1))
                        .setBackgroundTint(ContextCompat.getColor(this, R.color.textColor3))
                        .show()
                    when(it.field){
                        EnumTags.USERNAME.value->{
                            binding.etEmailLogin.setCustomBackground(R.drawable.bg_edit_text_error)
                        }
                        EnumTags.PASSWORD.value->{
                            binding.etPasswordLogin.setCustomBackground(R.drawable.bg_edit_text_error)
                        }
                    }
                }
            }
        }

    }

    private fun goToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        goForwardAnimation()
    }

    private fun updateLoading(showing: Boolean) {
        binding.buttonLogin.isEnabled = !showing
        binding.buttonLogin.setLoading(showing)
    }


    private fun setUpInputs() {
        binding.etEmailLogin.binding.etInput.isSingleLine = true
        binding.etEmailLogin.binding.etInput.doOnTextChanged { text, start, before, count ->
            viewModel.username = text.toString()
            if (text.toString().isNotEmpty()){
                binding.etEmailLogin.setCustomBackground(R.drawable.bg_edit_text_focused)
            }else{
                binding.etEmailLogin.setCustomBackground(R.drawable.bg_edit_text_error)
            }
        }

        binding.etPasswordLogin.binding.etInput.doOnTextChanged { text, start, before, count ->
            viewModel.password = text.toString()
            if (text.toString().length >= 6){
                binding.etPasswordLogin.setCustomBackground(R.drawable.bg_edit_text_focused)
            }else{
                binding.etPasswordLogin.setCustomBackground(R.drawable.bg_edit_text_error)
            }
        }

    }

}