import android.app.Activity
import android.os.Build
import androidx.compose.runtime.*
import androidx.navigation.compose.*
import androidx.hilt.navigation.compose.hiltViewModel
import limited.m.remembermywallet.ui.seedinput.SeedInputScreen
import limited.m.remembermywallet.viewmodel.SeedInputViewModel
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import androidx.compose.ui.platform.LocalContext
import limited.m.remembermywallet.ui.quizgame.QuizGameScreen

@Composable
fun MyNavigation(viewModel: SeedInputViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val window = (context as? Activity)?.window

    SideEffect {
        window?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                it.insetsController?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                ) // Dark icons on light backgrounds
            } else {
                @Suppress("DEPRECATION")
                it.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    val navController = rememberNavController()

    // Observe the stored seed state
    val isSeedStored by viewModel.isSeedStored.collectAsState(initial = false)

    // Log the seed state
    Log.d("MyNavigation", "isSeedStored = $isSeedStored")

    // Determine the start destination dynamically
    val startDestination = if (isSeedStored) "quiz_game" else "seed_input"

    NavHost(navController, startDestination = startDestination) {
        composable("seed_input") {
            SeedInputScreen(
                onSeedStored = {
                    Log.d("MyNavigation", "Navigating to quiz_game")
                    navController.navigate("quiz_game")
                }
            )
        }
        composable("quiz_game") {
            Log.d("MyNavigation", "limited.m.remembermywallet.ui.quizgame.QuizGameScreen loaded")
            QuizGameScreen()
        }
    }
}