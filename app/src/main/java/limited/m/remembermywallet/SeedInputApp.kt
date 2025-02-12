import androidx.compose.runtime.*
import androidx.navigation.compose.*
import androidx.hilt.navigation.compose.hiltViewModel
import limited.m.remembermywallet.ui.seedinput.SeedInputScreen

@Composable
fun SeedInputApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "seed_input") {
        composable("seed_input") {
            SeedInputScreen(
                viewModel = hiltViewModel(),
                onSeedStored = { navController.navigate("next_screen") }
            )
        }
        composable("next_screen") {
            // TODO: Replace with your next screen composable
        }
    }
}
