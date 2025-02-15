package limited.m.remembermywallet

import android.app.AlertDialog
import android.content.Intent
import android.net.VpnService
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val vpnPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                startVpnService() // Start VPN if permission granted
            } else {
                showVpnDeniedWarning() // Show warning if user denies permission
            }
        }

    /**
     * Checks if the activity was launched by the system or user.
     */
    private fun isLaunchedBySystemOrUser(): Boolean {
        val caller = callingPackage
        return caller == null || caller == packageName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")

        // Prevent unauthorized launch attempts
        if (!isLaunchedBySystemOrUser()) {
            Log.w("MainActivity", "Unauthorized launch attempt detected!")
            finish() // Close activity immediately
            return
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MyNavigation()
        }

        // Check if VPN permission is needed
        val vpnIntent = VpnService.prepare(this)
        if (vpnIntent != null) {
            showVpnPermissionDialog(vpnIntent)
        } else {
            startVpnService() // Permission already granted
        }
    }

    private fun showVpnPermissionDialog(vpnIntent: Intent) {
        AlertDialog.Builder(this)
            .setTitle("VPN Permission Required")
            .setMessage("This app needs VPN permission to block all communication and ensure maximum security. If denied, the app will not be able to block unwanted network activity.")
            .setPositiveButton("Continue") { _, _ -> vpnPermissionLauncher.launch(vpnIntent) }
            .setNegativeButton("Cancel") { _, _ -> showVpnDeniedWarning() }
            .setCancelable(false)
            .show()
    }

    private fun showVpnDeniedWarning() {
        AlertDialog.Builder(this)
            .setTitle("VPN Permission Denied")
            .setMessage("Without VPN permission, the app cannot block network activity, reducing security. You can enable it later in settings.")
            .setPositiveButton("OK", null)
            .show()
    }

    private fun startVpnService() {
        val intent = Intent(this, MyVpnService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }


    private fun stopVpnService() {
        val stopVpnIntent = Intent(this, MyVpnService::class.java).apply {
            action = MyVpnService.ACTION_STOP_VPN
        }
        this.startService(stopVpnIntent) // Send stop command

        this.stopService(Intent(this, MyVpnService::class.java)) // Force stop

    }

    override fun onDestroy() {
        super.onDestroy()

        if (isFinishing) {
            Log.d("MainActivity", "App is finishing, stopping VPN service...")
            stopVpnService()
        } else {
            Log.d("MainActivity", "Activity is being recreated, VPN service remains active.")
        }
    }

}
