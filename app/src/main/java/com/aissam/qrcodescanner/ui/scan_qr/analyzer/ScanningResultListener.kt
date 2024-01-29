package com.aissam.qrcodescanner.ui.scan_qr.analyzer

interface ScanningResultListener {
    fun onScanned(result: String)
}