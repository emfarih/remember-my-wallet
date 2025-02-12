package limited.m.remembermywallet.ui.quizgame

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import limited.m.remembermywallet.viewmodel.QuizGameViewModel
import limited.m.remembermywallet.viewmodel.SeedInputViewModel

@Composable
fun QuizGameScreen(quizGameViewModel: QuizGameViewModel = hiltViewModel(),
                   seedInputViewModel: SeedInputViewModel = hiltViewModel(),
                   onSeedCleared: () -> Unit) {
    @Suppress("LocalVariableName") val TAG = "QuizGameScreen"
    val quizState by quizGameViewModel.quizState.collectAsState()

    Log.d(TAG, "QuizGameScreen Composable Loaded")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            quizState.currentQuestion?.let { question ->
                Log.d(TAG, "New Question Loaded: ${question.correctAnswer}")

                Text(text = "Select the correct word:", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))

                question.options.forEach { option ->
                    Button(
                        onClick = {
                            Log.d(TAG, "Answer Selected: $option")
                            quizGameViewModel.checkAnswer(option)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        Text(text = option)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Score: ${quizState.score}")
                Log.d(TAG, "Current Score: ${quizState.score}")
            }
        }

        // Properly position the FloatingActionButton at the bottom center
        FloatingActionButton(
            onClick = {
                Log.d(TAG, "Clear Stored Seed Phrase Triggered")
                seedInputViewModel.clearSeed()
                onSeedCleared()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            containerColor = MaterialTheme.colorScheme.error, // Red color for deletion action
            elevation = FloatingActionButtonDefaults.elevation(8.dp), // Adding shadow
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete, // Icon to indicate deletion
                    contentDescription = "Clear Seed Phrase",
                    tint = MaterialTheme.colorScheme.onError // Contrast with the background
                )
                Text(
                    text = "Clear Stored Seed Phrase",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold // Bold text for emphasis
                    ),
                    color = MaterialTheme.colorScheme.onError // Text color that contrasts with the button
                )
            }
        }
    }
}


