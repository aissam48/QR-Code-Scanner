package fr.myticket.moov.checker.repository


import android.content.Context
import android.util.Log
import com.ajicreative.dtc.utils.Constants
import fr.myticket.moov.checker.utils.JsonParser
import com.ajicreative.dtc.utils.JsonResponses
import fr.myticket.moov.checker.utils.Preferences
import fr.myticket.moov.checker.models.APIError
import fr.myticket.moov.checker.models.EventRepo
import fr.myticket.moov.checker.R
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.*
import org.json.JSONObject
import org.json.JSONTokener
import javax.inject.Inject


class ApiManager @Inject constructor(
    private val client: HttpClient,
    val jsonResponses: JsonResponses,
    private val sharedPreferences: Preferences,
    val appContext: Context
) {


    fun List<*>.toJsonElement(): JsonElement {
        val list: MutableList<JsonElement> = mutableListOf()
        this.forEach { value ->
            when (value) {
                null -> list.add(JsonNull)
                is Map<*, *> -> list.add(value.toJsonElement())
                is List<*> -> list.add(value.toJsonElement())
                is Boolean -> list.add(JsonPrimitive(value))
                is Number -> list.add(JsonPrimitive(value))
                is String -> list.add(JsonPrimitive(value))
                is Enum<*> -> list.add(JsonPrimitive(value.toString()))
                else -> throw IllegalStateException("Can't serialize unknown collection type: $value")
            }
        }
        return JsonArray(list)
    }

    private fun Map<*, *>.toJsonElement(): JsonElement {
        val map: MutableMap<String, JsonElement> = mutableMapOf()
        this.forEach { (key, value) ->
            key as String
            when (value) {
                null -> map[key] = JsonNull
                is Map<*, *> -> map[key] = value.toJsonElement()
                is List<*> -> map[key] = value.toJsonElement()
                is Boolean -> map[key] = JsonPrimitive(value)
                is Number -> map[key] = JsonPrimitive(value)
                is String -> map[key] = JsonPrimitive(value)
                is Enum<*> -> map[key] = JsonPrimitive(value.toString())
                else -> throw IllegalStateException("Can't serialize unknown type: $value")
            }
        }
        return JsonObject(map)
    }

    suspend fun makeRequest(
        url: String,
        reqMethod: HttpMethod?,
        useBearer: Boolean = true,
        bodyMap: HashMap<String, Any>? = null,
        multiPartForm: MultiPartFormDataContent? = null,
        parameterFormData: ArrayList<Pair<String, Any>>? = null,
        successCallback: suspend (response: JSONObject) -> Unit,
        failureCallback: suspend (error: EventRepo.Error<*>) -> Unit,
        progressCallback: suspend (total: Long, progress: Long) -> Unit
    ) {

        Log.e("url", url.toString())
        if (Constants.appEnvironment == AppEnvironment.Demo) {
            val response = jsonResponses.getJSONObject(url)
            delay(2000)
            successCallback(response)
            return
        }

        try {

            val httpResponse: HttpResponse? =
                when (reqMethod) {
                    HttpMethod.Post -> {
                        client.post(url) {
                            if (multiPartForm != null) {
                                body = multiPartForm
                            }
                            if (bodyMap != null) {
                                contentType(ContentType.Application.Json)
                                body = bodyMap.toJsonElement()
                            }
                            onUpload { bytesSentTotal, contentLength ->
                                progressCallback(contentLength, bytesSentTotal)
                            }
                            if (parameterFormData != null) {
                                formData {
                                    for (param in parameterFormData) {
                                        parameter(param.first, param.second)
                                    }
                                }
                            }
                            if (useBearer)
                                headers {
                                    append(
                                        HttpHeaders.Authorization,
                                        "Bearer " + sharedPreferences.getToken()
                                    )
                                }
                        }
                    }

                    HttpMethod.Put -> {
                        client.put(url) {
                            if (multiPartForm != null) {
                                body = multiPartForm
                            }

                            if (bodyMap != null) {
                                contentType(ContentType.Application.Json)
                                body = bodyMap.toJsonElement()
                            }
                            if (parameterFormData != null) {
                                formData {
                                    for (param in parameterFormData) {
                                        parameter(param.first, param.second)
                                    }
                                }
                            }
                            if (useBearer)
                                headers {
                                    append(
                                        HttpHeaders.Authorization,
                                        "Bearer " + sharedPreferences.getToken()
                                    )
                                }
                        }
                    }

                    HttpMethod.Patch -> {
                        client.patch(url) {
                            if (multiPartForm != null) {
                                body = multiPartForm
                            }
                            if (bodyMap != null) {
                                contentType(ContentType.Application.Json)
                                body = bodyMap.toJsonElement()
                            }
                            if (parameterFormData != null) {
                                formData {
                                    for (param in parameterFormData) {
                                        parameter(param.first, param.second)
                                    }
                                }
                            }
                            if (useBearer)
                                headers {
                                    append(
                                        HttpHeaders.Authorization,
                                        "Bearer " + sharedPreferences.getToken()
                                    )
                                }
                        }
                    }

                    HttpMethod.Get -> {
                        client.get(url) {
                            contentType(ContentType.Application.Json)
                            if (parameterFormData != null) {
                                formData {
                                    for (param in parameterFormData) {
                                        parameter(param.first, param.second)
                                    }
                                }
                            }
                            if (useBearer)
                                headers {
                                    append(
                                        HttpHeaders.Authorization,
                                        "Bearer " + sharedPreferences.getToken()
                                    )
                                }
                        }
                    }

                    HttpMethod.Delete -> {
                        client.delete(url) {
                            contentType(ContentType.Application.Json)
                            if (parameterFormData != null) {
                                formData {
                                    for (param in parameterFormData) {
                                        parameter(param.first, param.second)
                                    }
                                }
                            }
                            if (useBearer)
                                headers {
                                    append(
                                        HttpHeaders.Authorization,
                                        "Bearer " + sharedPreferences.getToken()
                                    )
                                }
                        }
                    }

                    else -> {
                        null
                    }
                }

            val response: String? = httpResponse?.receive()
            val jsonObject = JSONTokener(response).nextValue() as JSONObject

            successCallback(jsonObject)
            Log.e("apiManager 200", jsonObject.toString())


        } catch (e: RedirectResponseException) {
            // 3xx - Response
            Log.e("apiManager 300", e.message.toString())
            failureCallback(
                EventRepo.Error<Any>(
                    apiError = APIError(
                        e.response.status.value,
                        e.response.content.toString(),
                    )
                )
            )
        } catch (e: ClientRequestException) {
            // 4xx - Response
            Log.e("apiManager 4xx e.response", e.response.toString())
            val json = JSONObject(e.response.receive<String>())
            Log.e("apiManager 4xx", json.toString())
            Log.e("apiManager code 4xx", e.response.status.value.toString())
            val errorHandler = JsonParser.getErrorHandler(json)
            errorHandler.statusCode = e.response.status.value

            failureCallback(
                EventRepo.Error<Any>(
                    apiError = errorHandler
                )
            )

        } catch (e: ServerResponseException) {
            // 5xx - Response
            Log.e("apiManager 500 s", e.message.toString())
            failureCallback(
                EventRepo.Error<Any>(
                    apiError = APIError(
                        e.response.status.value,
                        appContext.getString(R.string.error_message_at_server),
                    )
                )
            )
        } catch (e: Exception) {
            Log.e("apiManager 500 last", e.message.toString())

            failureCallback(
                EventRepo.Error<Any>(
                    apiError = APIError(500, appContext.getString(R.string.error_message_at_server))
                )
            )
        }
    }
}

enum class ApiBaseUrl(val type: String) {
    Production(BaseUrl.Prod.type),
    Dev(BaseUrl.Dev.type),
    Demo(""),
}

enum class AppEnvironment(val type: String) {
    Prod("Prod"),
    Dev("Dev"),
    Demo("Demo"),
}

enum class BaseUrl(val type: String) {
    Prod("https://my-ticket-moov.fr"),
    Dev("https://mtm.ajicreative.club"),
    Demo(""),
}

enum class Endpoint(val type: String) {
    LOGIN("/wp-json/aji/v1/login"),
    QR("/wp-json/tribe/tickets/v1/qr"),
    EVENT("/wp-json/tribe/events/v1/events"),
}