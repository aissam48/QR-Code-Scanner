package com.android.qrcodescanner.ui.scan_qr.analyzer

interface ScanningResultListener {
    fun onScanned(result: String)
}