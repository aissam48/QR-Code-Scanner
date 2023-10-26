package fr.myticket.moov.checker.utils

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class JsonResponses (val context: Context) {

      fun getJSONObject(jsonPath: String): JSONObject {
          val jsonString: String = try {
                context.assets.open(jsonPath)
                    .bufferedReader()
                    .use { it.readText() }
            } catch (ioException: IOException) {
                Log.e("ioException", ioException.message.toString())
                "{}"
            }

        return JSONObject(jsonString)
    }

    fun getJSONArray(jsonPath: String): JSONArray {
        val jsonString: String = try {
            context.assets.open(jsonPath)
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            Log.e("ioException", ioException.message.toString())
            "{}"
        }

        return JSONArray(jsonString)
    }
}
