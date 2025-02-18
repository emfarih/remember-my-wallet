package limited.m.remembermywallet

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import limited.m.remembermywallet.navigation.NavGraph
import java.net.HttpURLConnection
import java.net.Socket
import java.net.URL

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
            NavGraph()
        }

        // Block all network traffic for this app
        blockAllNetworkAccess()

        // Check if the network is successfully blocked
        checkNetworkAccess()

        // Test HTTP network request (should fail if blocked)
        testHttpRequest()

        // Test socket connection (should fail if blocked)
        testSocketConnection()
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
     * Checks if the app is still connected to a network and logs the status.
     */
    private fun checkNetworkAccess() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // Get the current active network
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        if (networkCapabilities == null || !networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            Log.d("MainActivity", "No network connection available. Network access is blocked.")
        } else {
            Log.d("MainActivity", "Network connection available. Access not blocked.")
        }
    }


    /**
     * Tests an HTTP network request to a known URL to verify if network access is blocked.
     */
    private fun testHttpRequest() {
        Thread {
            val url = "https://www.google.com"
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000 // Set a timeout to avoid long delays

                connection.connect()
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("NetworkTest", "HTTP Request succeeded! Connection to $url.")
                } else {
                    Log.d("NetworkTest", "HTTP Request failed with response code: ${connection.responseCode}")
                }
            } catch (e: Exception) {
                Log.e("NetworkTest", "HTTP Request failed: ${e.message}") // Expected: network should be blocked
            }
        }.start()
    }

    /**
     * Tests a socket connection to Google's DNS server to check if socket communication is blocked.
     */
    private fun testSocketConnection() {
        Thread {
            val host = "8.8.8.8"  // Google's public DNS IP address
            val port = 53         // DNS port
            try {
                val socket = Socket(host, port)
                Log.d("NetworkTest", "Socket connection succeeded to $host:$port")
                socket.close()
            } catch (e: Exception) {
                Log.e("NetworkTest", "Socket connection failed: ${e.message}")  // Expected: socket should be blocked
            }
        }.start()
    }
}
