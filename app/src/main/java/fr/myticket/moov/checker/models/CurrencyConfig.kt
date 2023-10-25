package fr.myticket.moov.checker.models

data class CurrencyConfig(
    var decimal_point: String = "",
    var number_of_decimals: Int = -1,
    var placement: String = "",
    var symbol: String = "",
    var thousands_sep: String = ""
)