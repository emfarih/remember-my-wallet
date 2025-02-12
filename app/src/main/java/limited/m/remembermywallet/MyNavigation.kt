import androidx.compose.runtime.*
import androidx.navigation.compose.*
import androidx.hilt.navigation.compose.hiltViewModel
import limited.m.remembermywallet.ui.seedinput.SeedInputScreen
import limited.m.remembermywallet.viewmodel.SeedInputViewModel
import android.util.Log
import limited.m.remembermywallet.ui.quizgame.QuizGameScreen

@Composable
fun MyNavigation(viewModel: SeedInputViewModel = hiltViewModel()) {
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