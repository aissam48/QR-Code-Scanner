package fr.myticket.moov.checker.models

data class Ticket(
    var currency_config: CurrencyConfig = CurrencyConfig(),
    var description: String = "",
    var end_sale: String = "",
    var formatted_price: String = "",
    var id: String = "",
    var raw_price: String = "",
    var start_sale: String = "",
    var title: String = ""
)