package fr.myticket.moov.checker.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.graphics.*
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import fr.myticket.moov.checker.repository.ApiBaseUrl
import fr.myticket.moov.checker.repository.AppEnvironment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.signature.ObjectKey
import fr.myticket.moov.checker.BuildConfig
import fr.myticket.moov.checker.R
import fr.myticket.moov.checker.repository.BaseUrl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.text.SimpleDateFormat


fun String.toApiUrl(jsonFile: String): String {
    if (BuildConfig.DEBUG) {

        when (Constants.appEnvironment) {
            AppEnvironment.Prod -> {
                return "${ApiBaseUrl.Production.type}$this"
            }
            AppEnvironment.Dev -> {
                return "${ApiBaseUrl.Dev.type}$this" //ApiBaseUrl.Dev.type
            }
            AppEnvironment.Demo -> {
                if (jsonFile == "") {
                    return "${ApiBaseUrl.Dev.type}$this"
                }
                return jsonFile
            }
        }
    } else {
        return "${ApiBaseUrl.Production.type}$this"
    }
}

fun String.baseUrl(): String {
    return if (BuildConfig.DEBUG) {
        when (Constants.appEnvironment) {
            AppEnvironment.Prod -> {
                "${BaseUrl.Prod.type}/$this"
            }
            AppEnvironment.Dev -> {
                "${BaseUrl.Dev.type}/$this"
            }
            AppEnvironment.Demo -> {
                "${BaseUrl.Dev.type}/$this"
            }
        }
    } else {
        "${BaseUrl.Prod.type}/$this"
    }
}

fun socketBaseUrl(): String {
    return if (BuildConfig.DEBUG) {
        when (Constants.appEnvironment) {
            AppEnvironment.Prod -> {
                BaseUrl.Prod.type
            }
            AppEnvironment.Dev -> {
                BaseUrl.Dev.type
            }
            AppEnvironment.Demo -> {
                BaseUrl.Dev.type
            }
        }
    } else {
        BaseUrl.Prod.type
    }
}

fun <T> ComponentActivity.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        flow.collectLatest(collect)
    }
}


private val json = Json {
    ignoreUnknownKeys = true
}

internal inline fun <reified R : Any> String.convertToDataClass() =
    json.decodeFromString<R>(this)

internal inline fun <reified R> dataClassToJsonString(model: R): String {
    val jsonData = json.encodeToJsonElement(model)
    return jsonData.toString()
}

//Loading Image URL into ImageView Using Glide Library
fun ImageView.loadImageWithGlide(id: String, name: String, context: Context) {
    val url = "/media/$id".toApiUrl("")
    Log.e("imageUrl", url)
    val token = Preferences(context).getToken()

    val headers = LazyHeaders.Builder()
        .addHeader("Authorization", "Bearer $token")
        .build()

    val glideUrl = GlideUrl(url, headers)

    Glide.with(this).load(glideUrl)
        //.placeholder(R.drawable.placeholder)
        .signature(ObjectKey(name))
        .into(this)
}

@SuppressLint("SimpleDateFormat")
fun String.timeConverter(format: String): String {
    return try {
        val sdf = SimpleDateFormat(Constants.FORMATTED_DATE).parse(this)
        return SimpleDateFormat(format).format(sdf.time)

    } catch (e: Exception) {
        Log.e("timeConverter", e.message.toString())
        ""
    }
}

fun Context.goForwardAnimation() {
    activity()?.goForwardAnimation()
}

fun Context.goBackAnimation() {
    activity()?.goBackAnimation()
}

tailrec fun Context.activity(): Activity? = when (this) {
    is Activity -> this
    else -> (this as? ContextWrapper)?.baseContext?.activity()
}

fun Activity.goForwardAnimation() {
    overridePendingTransition(R.anim.right_in, R.anim.stable)
}

fun Activity.goForwardAnimationSlidUp() {
    overridePendingTransition(R.anim.slide_up_in, R.anim.stable)
}

fun Activity.goBackAnimation() {
    overridePendingTransition(0, R.anim.left_out)
}


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.goneWithFade() {
    UtilsAnimation.fadeIn(this, View.GONE, 0)
}

fun View.invisibleWithFade() {
    UtilsAnimation.fadeIn(this, View.INVISIBLE, 0)
}

fun View.visibleWithFade() {
    UtilsAnimation.fadeOut(this, 0)
}

fun String.toHtmlText(): Spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_COMPACT);
} else {
    Html.fromHtml(this)
}

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

fun Context.pxToDp(px: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, resources.displayMetrics).toInt()

fun Context.dpToPx(dp: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()

fun String.capitalizeFirstCharacter(): String {
    return this[0].uppercase() + this.substring(1)
}
