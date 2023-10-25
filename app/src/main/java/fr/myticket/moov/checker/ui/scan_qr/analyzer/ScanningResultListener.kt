package fr.myticket.moov.checker.ui.scan_qr.analyzer

interface ScanningResultListener {
    fun onScanned(result: String)
}