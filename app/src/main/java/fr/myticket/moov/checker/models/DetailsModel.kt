package fr.myticket.moov.checker.models

import org.json.JSONObject

data class DetailsModel(
    var author: String = "",
    var ticket_title: String = "",
    var checked_in: Boolean = false,
    var checkin_details: CheckinDetails = CheckinDetails(),
    var date: String = "",
    var date_utc: String = "",
    var email: String = "",
    var global_id: String = "",
    var global_id_lineage: List<String> = listOf(),
    var id: Int = -1,
    var is_purchaser: Boolean = false,
    var is_subscribed: Boolean = false,
    var modified: String = "",
    var modified_utc: String = "",
    var optout: Boolean = false,
    var order: String = "",
    var payment: Payment = Payment(),
    var post_id: Int = -1,
    var provider: String = "",
    var rest_url: String = "",
    var sku: String = "",
    var status: String = "",
    var ticket: Ticket = Ticket(),
    var ticket_id: Int = -1,
    var title: String = "",
    var message: String = "",
    var error: String = "",
)