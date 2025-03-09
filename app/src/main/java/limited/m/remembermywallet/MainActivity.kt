package limited.m.remembermywallet

import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import limited.m.remembermywallet.navigation.NavGraph
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val handler = Handler(Looper.getMainLooper())
    private var isNetworkBlocked by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")

        if (!isLaunchedBySystemOrUser()) {
            Log.w("MainActivity", "Unauthorized launch attempt detected!")
            finish()
            return
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Block all network access
        blockAllNetworkAccess()
        startNetworkCheck()

        setContent {
            NetworkIsolationUI(isNetworkBlocked)
        }
    }

    private fun startNetworkCheck() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                isNetworkBlocked = testAllNetworkAccess()
                handler.postDelayed(this, 5000) // Repeat every 5 seconds
            }
        }, 5000)
    }

    /**
     * Checks if the activity was launched by the system or user.
     */
    private fun isLaunchedBySystemOrUser(): Boolean {
        val caller = callingPackage
        return caller == null || caller == packageName
    }

    /**
     * Blocks all network communication for this app by detaching it from any network.
     */
    private fun blockAllNetworkAccess() {
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager?.bindProcessToNetwork(null)
        Log.d("MainActivity", "Network access blocked for this app.")
    }

    /**
     * Tests ALL possible network access methods and returns true if all are blocked.
     */
    private fun testAllNetworkAccess(): Boolean {
        return testHttpRequest() &&
                testSocketConnection() &&
                testUdpConnection() &&
                testWebSocketConnection() &&
                testPing()
    }

    /**
     * Tests an HTTP request to verify if network access is blocked.
     */
    private fun testHttpRequest(): Boolean {
        return try {
            val connection = URL("https://www.google.com").openConnection() as HttpURLConnection
            connection.connectTimeout = 3000
            connection.connect()
            Log.w("NetworkTest", "❌ HTTP access is available!")
            false
        } catch (e: Exception) {
            Log.d("NetworkTest", "✅ HTTP is blocked.")
            true
        }
    }

    /**
     * Tests a socket connection to Google's DNS server.
     */
    private fun testSocketConnection(): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress("8.8.8.8", 53), 2000)
            Log.w("NetworkTest", "❌ Socket access is available!")
            socket.close()
            false
        } catch (e: Exception) {
            Log.d("NetworkTest", "✅ Socket is blocked.")
            true
        }
    }

    /**
     * Tests a UDP connection to Google's public DNS.
     */
    private fun testUdpConnection(): Boolean {
        return try {
            val socket = DatagramSocket()
            val address = InetAddress.getByName("8.8.8.8")
            val packet = DatagramPacket(ByteArray(1), 1, address, 53)
            socket.send(packet)
            Log.w("NetworkTest", "❌ UDP access is available!")
            socket.close()
            false
        } catch (e: Exception) {
            Log.d("NetworkTest", "✅ UDP is blocked.")
            true
        }
    }

    /**
     * Tests a WebSocket connection.
     */
    private fun testWebSocketConnection(): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress("echo.websocket.org", 80), 2000)
            Log.w("NetworkTest", "❌ WebSocket access is available!")
            socket.close()
            false
        } catch (e: Exception) {
            Log.d("NetworkTest", "✅ WebSocket is blocked.")
            true
        }
    }

    /**
     * Tests if ICMP ping works (may fail due to Android restrictions).
     */
    private fun testPing(): Boolean {
        return try {
            val address = InetAddress.getByName("8.8.8.8")
            if (address.isReachable(3000)) {
                Log.w("NetworkTest", "❌ ICMP Ping is available!")
                false
            } else {
                Log.d("NetworkTest", "✅ ICMP Ping is blocked.")
                true
            }
        } catch (e: Exception) {
            Log.d("NetworkTest", "✅ ICMP Ping is blocked.")
            true
        }
    }
}

@Composable
fun NetworkIsolationUI(isNetworkBlocked: Boolean) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        NavGraph()

        // Small network isolation indicator in the bottom-right corner
        Icon(
            imageVector = if (isNetworkBlocked) Icons.Filled.Lock else Icons.Filled.Warning,
            contentDescription = "Network Isolation Status",
            tint = if (isNetworkBlocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(24.dp) // Smaller size
                .clickable {
                    val message = if (isNetworkBlocked) {
                        "✅ Network is fully isolated."
                    } else {
                        "❌ Warning! Network access is available."
                    }
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
                .alpha(0.7f) // Slightly transparent for subtle effect
        )
    }
}
