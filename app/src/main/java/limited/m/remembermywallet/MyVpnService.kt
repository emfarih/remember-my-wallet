package limited.m.remembermywallet

import android.app.*
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat

class MyVpnService : VpnService() {
    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_VPN) {
            Log.d("MyVpnService", "Received STOP_VPN action, stopping service...")

            stopVpnService()
            return START_NOT_STICKY
        }

        Log.d("MyVpnService", "Starting VPN service...")
        startForeground(NOTIFICATION_ID, createNotification())

        val builder = Builder()
        builder.addAddress("10.0.0.2", 32) // Fake VPN address
        builder.addRoute("0.0.0.0", 0) // Block ALL internet traffic

        vpnInterface = builder.establish() // Activate VPN
        return START_STICKY
    }

    override fun onDestroy() {
        Log.d("MyVpnService", "onDestroy() called - Stopping VPN Service")
        stopVpnService()
        super.onDestroy()
    }

    private fun stopVpnService() {
        vpnInterface?.let {
            try {
                it.close() // Close VPN tunnel
                Log.d("MyVpnService", "VPN Interface closed successfully")
            } catch (e: Exception) {
                Log.e("MyVpnService", "Error closing VPN Interface", e)
            }
        }

        vpnInterface = null

        // Stop foreground service correctly (Handle API 34+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // API 34+
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }

        stopSelf()
    }

    private fun createNotification(): Notification {
        val channelId = "vpn_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "VPN Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("VPN Active")
            .setContentText("Your internet traffic is blocked while the app is active.")
            .setSmallIcon(R.drawable.ic_vpn_lock)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        Log.d("MyVpnService", "App removed from recent tasks, stopping VPN...")

        stopVpnService() // Stop VPN
        stopSelf() // Stop the service
        super.onTaskRemoved(rootIntent)
    }


    companion object {
        private const val NOTIFICATION_ID = 1
        const val ACTION_STOP_VPN = "STOP_VPN"
    }
}
