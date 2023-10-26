package fr.myticket.moov.checker.models

sealed class EventRepo<T>() {
    class Success<T>(val data: T?) : EventRepo<T>()
    class Error<T>(val apiError: APIError) : EventRepo<T>()
    class Upload<T>(val total: Long, val progress: Long) : EventRepo<T>()
}
