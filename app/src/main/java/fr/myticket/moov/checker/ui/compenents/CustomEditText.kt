package fr.myticket.moov.checker.ui.compenents

import android.content.Context
import android.content.res.TypedArray
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import fr.myticket.moov.checker.R
import fr.myticket.moov.checker.databinding.CustomEditTextBinding

class CustomEditText(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    lateinit var binding: CustomEditTextBinding
    lateinit var attributes: TypedArray
    init {
        binding = CustomEditTextBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding.root)
        attributes = context.obtainStyledAttributes(attributeSet, R.styleable.CustomEditText)

        binding.etInput.hint = attributes.getString(R.styleable.CustomEditText_putHint)

        handleFocus()
    }

    private fun handleFocus() {
        binding.etInput.setOnFocusChangeListener { view, b ->
            if (b){
                binding.etInput.background = ContextCompat.getDrawable(context, R.drawable.bg_edit_text_focused)
            }else{
                binding.etInput.background = ContextCompat.getDrawable(context, R.drawable.bg_edit_text)
            }
        }
    }

    fun setCustomBackground(background: Int) {
        binding.etInput.background = ContextCompat.getDrawable(context, background)
    }
}