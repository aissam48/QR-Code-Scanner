package fr.myticket.moov.checker.models

data class Event(
    var id: Int = -1,
    var title: String = "",
    var rest_url: String = "",
    var description: String = "",
    var image: String = "",
)