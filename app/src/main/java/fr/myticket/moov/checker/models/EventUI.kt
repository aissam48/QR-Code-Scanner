package fr.myticket.moov.checker.models

sealed class EventUI<T>() {
    class OnLoading<T>(val isShowing: Boolean) : EventUI<T>()
    class OnSuccess<T>(val data: T?) : EventUI<T>()
    class OnError<T>(
        val message: String = "",
        val error: String = "",
        val details:DetailsModel = DetailsModel(),
        val field: String? = null,
        val statusCode: Int = 500
    ) : EventUI<T>()

    class OnUpload<T>(val total: Long, val progress: Long) : EventUI<T>()

}