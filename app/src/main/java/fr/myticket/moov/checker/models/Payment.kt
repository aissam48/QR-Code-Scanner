package fr.myticket.moov.checker.models

data class Payment(
    var currency: String = "",
    var date: String = "",
    var date_details: DateDetails = DateDetails(),
    var price: String = "",
    var provider: String = ""
)