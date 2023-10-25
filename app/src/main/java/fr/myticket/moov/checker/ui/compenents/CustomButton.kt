package fr.myticket.moov.checker.ui.compenents

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import fr.myticket.moov.checker.utils.goneWithFade
import fr.myticket.moov.checker.utils.visibleWithFade
import fr.myticket.moov.checker.R
import fr.myticket.moov.checker.databinding.CustomButtonBinding

class CustomButton(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    lateinit var binding: CustomButtonBinding
    lateinit var attributes: TypedArray
    init {
        binding = CustomButtonBinding.inflate(LayoutInflater.from(context), this, false)
        addView(binding.root)
        attributes = context.obtainStyledAttributes(attributeSet, R.styleable.CustomButton)

        binding.tvTitle.text = attributes.getString(R.styleable.CustomButton_buttonTitle)

    }

    fun setLoading(b:Boolean){
        if (b){
            binding.tvTitle.goneWithFade()
            binding.prLoading.visibleWithFade()
        }else{
            binding.prLoading.goneWithFade()
            binding.tvTitle.visibleWithFade()
        }
    }

    fun setCustomBackground(background:Int){
        binding.root.background = ContextCompat.getDrawable(context, background)
    }

    fun setButtonTitle(s:String) {
        binding.tvTitle.text = s
    }

}