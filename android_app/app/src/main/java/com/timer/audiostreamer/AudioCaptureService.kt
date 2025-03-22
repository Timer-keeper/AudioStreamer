import android.app.*
import android.content.Intent
import android.media.*
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread

class AudioCaptureService : Service() {
    private lateinit var mediaProjection: MediaProjection
    private lateinit var audioRecord: AudioRecord
    private var encoder: Long = 0
    private val NOTIFICATION_ID = 1234

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())

        // 从Intent获取权限结果
        intent?.let {
            if (it.hasExtra("resultCode") && it.hasExtra("data")) {
                val resultCode = it.getIntExtra("resultCode", 0)
                val data = it.getParcelableExtra<Intent>("data")
                handlePermissionResult(resultCode, data)
            }
        }
    }

    private fun handlePermissionResult(resultCode: Int, data: Intent?) {
        val mgr = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = mgr.getMediaProjection(resultCode, data!!)
        startCapture(mediaProjection)
        
        // 初始化 Opus 编码器（保持原有逻辑）
        encoder = OpusWrapper.initEncoder(48000, 1)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "audio_channel",
            "Audio Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Audio capture service"
        }
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        return Notification.Builder(this, "audio_channel")
            .setContentTitle("Audio Capture Service")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
    }

    // 以下保持原有音频捕获逻辑不变
    private fun startCapture(mediaProjection: MediaProjection) {
        val config = AudioPlaybackCaptureConfiguration.Builder(mediaProjection)
            .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        audioRecord = AudioRecord.Builder()
            .setAudioPlaybackCaptureConfig(config)
            .setAudioFormat(AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(48000)
                .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                .build())
            .build()

        audioRecord.startRecording()
        thread {
            val buffer = ShortArray(960)
            while (true) {
                audioRecord.read(buffer, 0, buffer.size)
                val encoded = OpusWrapper.encode(encoder, buffer, buffer.size)
                sendUdpPacket(encoded)
            }
        }
    }

    private fun sendUdpPacket(data: ByteArray) {
        DatagramSocket().use { socket ->
            val packet = DatagramPacket(
                data, data.size,
                InetAddress.getByName("192.168.1.100"), 5000
            )
            socket.send(packet)
        }
    }

    override fun onDestroy() {
        audioRecord.stop()
        audioRecord.release()
        super.onDestroy()
    }
}