package fr.myticket.moov.checker.models


data class APIError(
    var statusCode: Int = -1,
    var message: String = "",
    var error: String = "",
    var details: DetailsModel = DetailsModel(),
    var errors: List<APIFormError> = emptyList()
)

data class APIFormError(
    var field:String = "",
    var message: String = ""
)
