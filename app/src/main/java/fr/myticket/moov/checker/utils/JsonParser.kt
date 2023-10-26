package fr.myticket.moov.checker.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import fr.myticket.moov.checker.Enums.EnumTags
import fr.myticket.moov.checker.models.APIError
import fr.myticket.moov.checker.models.APIFormError
import fr.myticket.moov.checker.models.DetailsModel
import org.json.JSONObject

object JsonParser {

    fun getErrorHandler(json: JSONObject): APIError {

        val apiError = APIError()
        if (json.has(EnumTags.MESSAGE.value)) {
            apiError.message = json.getString(EnumTags.MESSAGE.value)
        }
        if (json.has(EnumTags.MSG.value)) {
            apiError.message = json.getString(EnumTags.MSG.value)
        }
        if (json.has(EnumTags.ERROR.value)) {
            apiError.error = json.getString(EnumTags.ERROR.value)
        }
        if (json.has(EnumTags.ATTENDEE.value)) {
            apiError.details = Gson().fromJson(
                json.getJSONObject(EnumTags.ATTENDEE.value).toString(),
                object : TypeToken<DetailsModel>() {}.type
            )
        }
//        if (json.has("code")) {
//            apiError.statusCode = json.getInt("code")
//        }
        if (json.has("errors")) {
            val listFields = mutableListOf<APIFormError>()
            val errorsArray = json.getJSONArray("errors")
            for (i in 0 until errorsArray.length()) {
                listFields.add(getError(errorsArray.getJSONObject(i)))
            }
            apiError.errors = listFields
        }

        return apiError
    }

    private fun getError(json: JSONObject): APIFormError {
        val apiFormError = APIFormError()
        if (json.has("field")) {
            apiFormError.field = json.getString("field")
        }
        if (json.has("message")) {
            apiFormError.message = json.getString("message")
        }

        return apiFormError
    }


}

