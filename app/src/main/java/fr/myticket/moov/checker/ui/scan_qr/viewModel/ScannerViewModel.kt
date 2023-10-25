package fr.myticket.moov.checker.ui.scan_qr.viewModel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.myticket.moov.checker.Enums.EnumTags
import fr.myticket.moov.checker.R
import fr.myticket.moov.checker.models.DetailsModel
import fr.myticket.moov.checker.models.Event
import fr.myticket.moov.checker.models.EventRepo
import fr.myticket.moov.checker.models.EventUI
import fr.myticket.moov.checker.repository.ApiServicesImpl
import fr.myticket.moov.checker.utils.Preferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val preferences: Preferences,
    private val apiServicesImpl: ApiServicesImpl,
    private val appContext: Application
) : ViewModel() {

    private val _sharedFlowDetails = MutableSharedFlow<EventUI<DetailsModel>>()
    val sharedFlowDetails = _sharedFlowDetails.asSharedFlow()

    fun getDetails(
        ticketId: String?,
        securityCode: String?,
        eventId: String?,
        apiKey: String
    ) {

        val params = hashMapOf<String, Any>()
        params[EnumTags.TICKET_ID.value] = ticketId.toString()
        params[EnumTags.SECURITY_CODE.value] = securityCode.toString()
        params[EnumTags.EVENT_ID.value] = eventId.toString()
        params[EnumTags.API_KEY.value] = apiKey.toString()

        viewModelScope.launch {
            _sharedFlowDetails.emit(EventUI.OnLoading(true))
            apiServicesImpl.getDetails(params).collect {
                _sharedFlowDetails.emit(EventUI.OnLoading(false))

                when (it) {
                    is EventRepo.Success -> {
                        _sharedFlowDetails.emit(EventUI.OnSuccess(it.data))
                    }
                    is EventRepo.Error -> {
                        when (it.apiError.statusCode) {
                            in (500..599) -> {
                                _sharedFlowDetails.emit(EventUI.OnError(message = appContext.getString(R.string.error_message_at_server)))
                            }
                            403->{
                                val details = it.apiError.details
                                details.message = it.apiError.message
                                details.error = it.apiError.error
                                _sharedFlowDetails.emit(
                                    EventUI.OnError(
                                        message = it.apiError.message,
                                        error = it.apiError.error,
                                        details = it.apiError.details,
                                        statusCode = 403
                                    )
                                )
                            }
                            else -> {
                                _sharedFlowDetails.emit(
                                    EventUI.OnError(
                                        message = it.apiError.message,
                                        error = it.apiError.error,
                                    )
                                )
                            }
                        }

                    }
                }
            }
        }

    }

    private val _sharedFlowEvent = MutableSharedFlow<EventUI<Event>>()
    val sharedFlowEvent = _sharedFlowEvent.asSharedFlow()

    fun getEvent(id:Int) {

        val params = hashMapOf<String, Any>()
        params[EnumTags.ID.value] = id

        viewModelScope.launch {
            _sharedFlowEvent.emit(EventUI.OnLoading(true))
            apiServicesImpl.getEvent(params).collect {
                _sharedFlowEvent.emit(EventUI.OnLoading(false))

                when (it) {
                    is EventRepo.Success -> {
                        _sharedFlowEvent.emit(EventUI.OnSuccess(it.data))
                    }
                    is EventRepo.Error -> {
                        _sharedFlowEvent.emit(EventUI.OnError(it.apiError.message))
                        when (it.apiError.statusCode) {
                            in (500..599) -> {
                                _sharedFlowEvent.emit(EventUI.OnError(appContext.getString(R.string.error_message_at_server)))
                            }

                            else -> {
                                _sharedFlowEvent.emit(
                                    EventUI.OnError(
                                        message = it.apiError.message,
                                        error = it.apiError.error,
                                    )
                                )
                            }
                        }

                    }
                }
            }
        }

    }

}