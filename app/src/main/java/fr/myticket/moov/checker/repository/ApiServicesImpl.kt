package fr.myticket.moov.checker.repository

import android.app.Application
import android.content.Context
import fr.myticket.moov.checker.Enums.EnumTags
import fr.myticket.moov.checker.utils.JsonResponses
import fr.myticket.moov.checker.utils.Preferences
import fr.myticket.moov.checker.utils.toApiUrl
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import fr.myticket.moov.checker.models.DetailsModel
import fr.myticket.moov.checker.models.Event
import fr.myticket.moov.checker.models.EventRepo
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import javax.inject.Inject
import kotlin.collections.HashMap

class ApiServicesImpl @Inject constructor(
    private val apiManager: ApiManager,
    private val dataStore: Preferences,
    private val jsonResponses: JsonResponses
) : ApiServices {

    @Inject
    @ApplicationContext
    lateinit var appContext: Context

    @Inject
    lateinit var application: Application

    override suspend fun login(params: HashMap<String, Any>): Flow<EventRepo<Boolean>> = flow {

        apiManager.makeRequest(
            url = Endpoint.LOGIN.type.toApiUrl(""),
            bodyMap = params,
            reqMethod = HttpMethod.Post,
            parameterFormData = null,
            failureCallback = { error ->
                emit(EventRepo.Error(error.apiError))
            },
            successCallback = {
                var accessToken = ""
                var apiKay = ""
                var message = ""

                if (it.has(EnumTags.ACCESS_TOKEN.value)) {
                    accessToken = it.getString(EnumTags.ACCESS_TOKEN.value)
                    dataStore.saveToken(accessToken)
                }

                if (it.has(EnumTags.API_KEY.value)) {
                    apiKay = it.getString(EnumTags.API_KEY.value)
                    dataStore.saveApiKey(apiKay)
                }

                if (it.has(EnumTags.MESSAGE.value)) {
                    message = it.getString(EnumTags.MESSAGE.value)
                }

                if (apiKay.isEmpty()){
                    emit(EventRepo.Success(false))
                }else{
                    emit(EventRepo.Success(true))
                }
            },
            progressCallback = { total: Long, progress: Long ->

            }
        )
    }

    override suspend fun getDetails(params: HashMap<String, Any>): Flow<EventRepo<DetailsModel>>  =
        flow {

            val parameters = arrayListOf<Pair<String, Any>>()
            for ((key, value) in params) {
                parameters.add(Pair(key, value))
            }

            apiManager.makeRequest(
                url = Endpoint.QR.type.toApiUrl(""),
                bodyMap = null,
                reqMethod = HttpMethod.Get,
                parameterFormData = parameters,
                failureCallback = { error ->
                    emit(EventRepo.Error(error.apiError))
                },
                successCallback = {
                    var message = ""
                    var error = ""
                    var detailsObj = JSONObject()


                    if (it.has(EnumTags.MESSAGE.value)) {
                        message = it.getString(EnumTags.MESSAGE.value)
                    }

                    if (it.has(EnumTags.ERROR.value)) {
                        error = it.getString(EnumTags.ERROR.value)
                    }

                    if (it.has(EnumTags.ATTENDEE.value)) {
                        detailsObj = it.getJSONObject(EnumTags.ATTENDEE.value)
                    }

                    val detail = Gson().fromJson<DetailsModel>(detailsObj.toString(), object :TypeToken<DetailsModel>(){}.type)

                    detail.message = message
                    detail.error = error
                    emit(EventRepo.Success(detail))
                },
                progressCallback = { total: Long, progress: Long ->

                }
            )

        }

    override suspend fun getEvent(params: HashMap<String, Any>): Flow<EventRepo<Event>> =
        flow {

            val id = params[EnumTags.ID.value]

            apiManager.makeRequest(
                url = "${Endpoint.EVENT.type}/$id".toApiUrl(""),
                bodyMap = null,
                reqMethod = HttpMethod.Get,
                parameterFormData = null,
                failureCallback = { error ->
                    emit(EventRepo.Error(error.apiError))
                },
                successCallback = {
                    val event = Event()

                    if (it.has(EnumTags.ID.value)){
                        event.id = it.getInt(EnumTags.ID.value)
                    }

                    if (it.has(EnumTags.TITLE.value)){
                        event.title = it.getString(EnumTags.TITLE.value)
                    }

                    if (it.has(EnumTags.DESCRIPTION.value)){
                        event.description = it.getString(EnumTags.DESCRIPTION.value)
                    }

                    if (it.has(EnumTags.REST_URL.value)){
                        event.rest_url = it.getString(EnumTags.REST_URL.value)
                    }

                    if (it.has(EnumTags.IMAGE.value)){
                        val imageObj = it.getJSONObject(EnumTags.IMAGE.value)
                        if (imageObj.has(EnumTags.URL.value)){
                            event.image = imageObj.getString(EnumTags.URL.value)
                        }
                    }

                    emit(EventRepo.Success(event))
                },
                progressCallback = { total: Long, progress: Long ->

                }
            )

        }
}