package fr.myticket.moov.checker.ui.scan_qr

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ajicreative.dtc.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import fr.myticket.moov.checker.Enums.EnumTags
import fr.myticket.moov.checker.R
import fr.myticket.moov.checker.databinding.ActivityBarcodeScanningBinding
import fr.myticket.moov.checker.models.DetailsModel
import fr.myticket.moov.checker.models.EventUI
import fr.myticket.moov.checker.ui.qr_code_details.QrCodeDetailsActivity
import fr.myticket.moov.checker.ui.scan_qr.analyzer.MLKitBarcodeAnalyzer
import fr.myticket.moov.checker.ui.scan_qr.analyzer.ScanningResultListener
import fr.myticket.moov.checker.ui.scan_qr.viewModel.ScannerViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

@AndroidEntryPoint
class BarcodeScanningActivity : AppCompatActivity() {
    private val scannerViewModel: ScannerViewModel by viewModels<ScannerViewModel>()

    @Inject
    lateinit var dataStore: fr.myticket.moov.checker.utils.Preferences

    private val CAMERA_PERMISSION_REQUEST = 1001

    lateinit var details: DetailsModel

    companion object {
        @JvmStatic
        fun start(context: Activity) {
            val starter = Intent(context, BarcodeScanningActivity::class.java).apply {

            }
            context.startActivity(starter)
            context.goForwardAnimation()
        }
    }

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var binding: ActivityBarcodeScanningBinding

    private lateinit var cameraExecutor: ExecutorService
    private var flashEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeScanningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        collectData()
        checkAndRequestCameraPermission()
    }

    private fun collectData() {
        collectLatestLifecycleFlow(scannerViewModel.sharedFlowDetails) {
            when (it) {
                is EventUI.OnLoading -> {
                    updateLoading(it.isShowing)
                }
                is EventUI.OnSuccess -> {
                    details = it.data ?: DetailsModel()
                    scannerViewModel.getEvent(it.data?.post_id ?: -1)
                }
                is EventUI.OnError -> {
                    Snackbar.make(binding.root, it.message, 1500)
                        .setTextColor(ContextCompat.getColor(this, R.color.textColor1))
                        .setBackgroundTint(ContextCompat.getColor(this, R.color.textColor3))
                        .show()
                    when (it.statusCode) {
                        in 500..599 -> {

                        }
                        403 -> {
                            details = it.details
                            scannerViewModel.getEvent(it.details.post_id ?: -1)
                        }
                        else -> {

                        }
                    }
                }
            }
        }

        collectLatestLifecycleFlow(scannerViewModel.sharedFlowEvent) {
            when (it) {
                is EventUI.OnLoading -> {
                    updateLoading(it.isShowing)
                }
                is EventUI.OnSuccess -> {
                    details.ticket_title = it.data?.title ?: ""
                    val intent = Intent(this, QrCodeDetailsActivity::class.java)
                    intent.putExtra(EnumTags.DETAILS.value, Gson().toJson(details))
                    startActivity(intent)
                    goForwardAnimation()
                }
                is EventUI.OnError -> {
                    Snackbar.make(binding.root, it.message, 1500)
                        .setTextColor(ContextCompat.getColor(this, R.color.textColor1))
                        .setBackgroundTint(ContextCompat.getColor(this, R.color.textColor3))
                        .show()
                }
            }
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.ivLoading.visible()
        } else {
            binding.ivLoading.gone()

        }
    }

    private fun initCameraXScanner() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        binding.overlay.post {
            binding.overlay.setViewFinder()
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {

        if (isDestroyed || isFinishing) {
            //This check is to avoid an exception when trying to re-bind use cases but user closes the activity.
            //java.lang.IllegalArgumentException: Trying to create use case mediator with destroyed lifecycle.
            return
        }

        cameraProvider?.unbindAll()

        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(binding.cameraPreview.width, binding.cameraPreview.height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val orientationEventListener = object : OrientationEventListener(this as Context) {
            override fun onOrientationChanged(orientation: Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation: Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageAnalysis.targetRotation = rotation
            }
        }
        orientationEventListener.enable()

        //switch the analyzers here, i.e. MLKitBarcodeAnalyzer, ZXingBarcodeAnalyzer
        class ScanningListener : ScanningResultListener {
            override fun onScanned(result: String) {
                runOnUiThread {
                    imageAnalysis.clearAnalyzer()
                    cameraProvider?.unbindAll()
                    Log.e("result_scan", result)

                    if (Patterns.WEB_URL.matcher(result).matches()) {
                        val uri: Uri = Uri.parse(result)
                        val ticketId = uri.getQueryParameter(EnumTags.TICKET_ID.value)
                        val securityCode = uri.getQueryParameter(EnumTags.SECURITY_CODE.value)
                        val eventId = uri.getQueryParameter(EnumTags.EVENT_ID.value)
                        val apiKey = dataStore.getApiKey() ?: ""

                        if (
                            ticketId?.isEmpty() == true ||
                            securityCode?.isEmpty() == true ||
                            eventId?.isEmpty() == true || apiKey.isEmpty()
                        ) {
                            return@runOnUiThread
                        }

                        scannerViewModel.getDetails(ticketId, securityCode, eventId, apiKey)
                    }
                }
            }
        }

        val analyzer: ImageAnalysis.Analyzer = MLKitBarcodeAnalyzer(ScanningListener())



        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)

        preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

        val camera =
            cameraProvider?.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)

        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            binding.ivFlashControl.visibility = View.VISIBLE

            binding.ivFlashControl.setOnClickListener {
                camera.cameraControl.enableTorch(!flashEnabled)
            }

            camera.cameraInfo.torchState.observe(this) {
                it?.let { torchState ->
                    if (torchState == TorchState.ON) {
                        flashEnabled = true
                        binding.ivFlashControl.setImageResource(R.drawable.ic_flashlight_off)
                    } else {
                        binding.ivFlashControl.setImageResource(R.drawable.ic_flashlight_on_)
                        flashEnabled = false
                    }
                }
            }
        }

    }


    private fun checkCameraPermission(): Boolean {
        val cameraPermission = Manifest.permission.CAMERA
        val result = ContextCompat.checkSelfPermission(this, cameraPermission)
        return result == PackageManager.PERMISSION_GRANTED
    }

    // Request camera permission
    private fun requestCameraPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        ActivityCompat.requestPermissions(this, permissions, CAMERA_PERMISSION_REQUEST)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCameraXScanner()
                } else {
                    Snackbar.make(binding.root, getString(R.string.camera_permission), 1500)
                        .setTextColor(ContextCompat.getColor(this, R.color.textColor1))
                        .setBackgroundTint(ContextCompat.getColor(this, R.color.textColor3))
                        .show()
                }
            }
        }
    }

    // Check and request camera permission when needed
    private fun checkAndRequestCameraPermission() {
        if (!checkCameraPermission()) {
            requestCameraPermission()
        } else {
            initCameraXScanner()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        // Shut down our background executor
        if (this::cameraExecutor.isInitialized) {
            cameraExecutor.shutdown()
        }
    }

    override fun onStop() {
        super.onStop()
        if (this::cameraExecutor.isInitialized) {
            initCameraXScanner()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goBackAnimation()
    }

}