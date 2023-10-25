package fr.myticket.moov.checker.models

data class CheckinDetails(
    var author: Int = -1,
    var date: String="",
    var date_details: DateDetails = DateDetails(),
    var source: String=""
)