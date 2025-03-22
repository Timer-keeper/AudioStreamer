class PermissionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(mgr.createScreenCaptureIntent(), 1)
    }

    override fun onActivityResult(code: Int, result: Int, data: Intent?) {
        val serviceIntent = Intent(this, AudioCaptureService::class.java).apply {
            putExtra("resultCode", result)
            putExtra("data", data)
        }
        startForegroundService(serviceIntent)
        finish()
    }
}