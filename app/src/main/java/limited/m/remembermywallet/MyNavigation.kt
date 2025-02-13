package limited.m.remembermywallet

import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import limited.m.remembermywallet.ui.quizgame.QuizGameScreen
import limited.m.remembermywallet.ui.seedinput.SeedInputScreen
import limited.m.remembermywallet.viewmodel.SeedPhraseViewModel

@Composable
fun MyNavigation(viewModel: SeedPhraseViewModel = hiltViewModel()) {
    @Suppress("LocalVariableName") val TAG = "MyNavigation"
    val context = LocalContext.current
    val activity = context as? Activity
    val window = activity?.window

    var showExitDialog by remember { mutableStateOf(false) }
    val isSeedStored by viewModel.isSeedStored.collectAsState(initial = null)

    Log.d(TAG, "MyNavigation Composable Loaded, isSeedStored: $isSeedStored")

    // Status bar appearance
    SideEffect {
        window?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                it.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
            } else {
                @Suppress("DEPRECATION")
                it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    val navController = rememberNavController()

    // Show loading until isSeedStored is determined
    if (isSeedStored == null) {
        Log.d(TAG, "Showing Loading Screen")
        LoadingScreen()
        return
    }

    val startDestination = if (isSeedStored == true) "quiz_game" else "seed_input"
    Log.d(TAG, "Start destination resolved: $startDestination")

    NavHost(navController, startDestination = startDestination) {
        composable("seed_input") {
            Log.d(TAG, "Navigating to SeedInputScreen")
            SeedInputScreen(
                onSeedStored = {
                    Log.d(TAG, "Seed stored, navigating to QuizGameScreen")
                    navController.navigate("quiz_game") {
                        popUpTo("seed_input") { inclusive = true }
                    }
                }
            )
        }
        composable("quiz_game") {
            Log.d(TAG, "Navigating to QuizGameScreen")
            QuizGameScreen(
                onSeedCleared = {
                    Log.d(TAG, "Seed cleared, navigating to SeedInputScreen")
                    navController.navigate("seed_input") {
                        popUpTo("quiz_game") { inclusive = true }
                    }
                }
            )
        }
    }

    // Handle back press
    BackHandler {
        Log.d(TAG, "Back button pressed, showing exit confirmation dialog")
        showExitDialog = true
    }

    // Exit confirmation dialog
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Exit App") },
            text = { Text("Are you sure you want to exit?") },
            confirmButton = {
                Button(onClick = { activity?.finish() }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                Button(onClick = { showExitDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
