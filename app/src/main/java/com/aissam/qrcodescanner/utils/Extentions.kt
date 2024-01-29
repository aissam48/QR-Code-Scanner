package com.aissam.qrcodescanner.utils

import android.app.Activity
import android.view.View
import com.aissam.qrcodescanner.R


fun Activity.goForwardAnimation() {
    overridePendingTransition(R.anim.right_in, R.anim.stable)
}


fun Activity.goBackAnimation() {
    overridePendingTransition(0, R.anim.left_out)
}


fun View.visible() {
    this.visibility = View.VISIBLE
}


fun View.gone() {
    this.visibility = View.GONE
}

fun String.capitalizeFirstCharacter(): String {
    return this[0].uppercase() + this.substring(1)
}
