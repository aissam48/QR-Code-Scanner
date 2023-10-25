package fr.myticket.moov.checker.ui.login

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.myticket.moov.checker.Enums.EnumTags
import fr.myticket.moov.checker.R
import fr.myticket.moov.checker.models.EventRepo
import fr.myticket.moov.checker.models.EventUI
import fr.myticket.moov.checker.repository.ApiServicesImpl
import fr.myticket.moov.checker.utils.Preferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiServicesImpl: ApiServicesImpl,
    private val appContext: Application,
    private val dataStore: Preferences
) :
    AndroidViewModel(appContext) {

    private val _sharedFlowLogin = MutableSharedFlow<EventUI<Boolean>>()
    val sharedFlowLogin = _sharedFlowLogin.asSharedFlow()
    var username = ""
    var password = ""

    fun login() {

        if (!validationInputsLogin())
            return

        val params = hashMapOf<String, Any>()
        params[EnumTags.USERNAME.value] = username
        params[EnumTags.PASSWORD.value] = password

        viewModelScope.launch {

            _sharedFlowLogin.emit(EventUI.OnLoading(true))
            apiServicesImpl.login(params).onEach { result ->
                _sharedFlowLogin.emit(EventUI.OnLoading(false))

                when (result) {
                    is EventRepo.Success -> {
                        if (result.data == true){
                            _sharedFlowLogin.emit(EventUI.OnSuccess(result.data))
                        }else{
                            _sharedFlowLogin.emit(EventUI.OnError(appContext.getString(R.string.error_login_failed)))
                        }
                    }
                    is EventRepo.Error -> {
                        when (result.apiError.statusCode) {
                            in (500..599) -> {
                                _sharedFlowLogin.emit(EventUI.OnError(appContext.getString(R.string.error_message_at_server)))
                            }
                            else -> {
                                if (result.apiError.errors.isEmpty()) {
                                    _sharedFlowLogin.emit(EventUI.OnError(result.apiError.message))
                                } else {
                                    when (result.apiError.errors[0].field) {
                                        EnumTags.USERNAME.value -> {
                                            _sharedFlowLogin.emit(
                                                EventUI.OnError(
                                                    result.apiError.errors[0].message,
                                                    field = EnumTags.USERNAME.value
                                                )
                                            )
                                        }
                                        EnumTags.PASSWORD.value -> {
                                            _sharedFlowLogin.emit(
                                                EventUI.OnError(
                                                    result.apiError.errors[0].message,
                                                    field = EnumTags.PASSWORD.value
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }

                    }
                }

            }.launchIn(this)
        }
    }

    private fun validationInputsLogin(): Boolean {
        if (username.isEmpty()) {
            viewModelScope.launch {
                _sharedFlowLogin.emit(
                    EventUI.OnError(
                        appContext.getString(R.string.error_message_add_username),
                        field = EnumTags.USERNAME.value
                    )
                )
            }
            return false
        }

        if (password.isEmpty()) {
            viewModelScope.launch {
                _sharedFlowLogin.emit(
                    EventUI.OnError(
                        appContext.getString(R.string.error_add_password),
                        field = EnumTags.PASSWORD.value
                    )
                )
            }
            return false
        }

        return true
    }

}

