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
import limited.m.remembermywallet.viewmodel.SeedPhraseViewModel

@Composable
fun QuizGameScreen(
    quizGameViewModel: QuizGameViewModel = hiltViewModel(),
    seedPhraseViewModel: SeedPhraseViewModel = hiltViewModel(),
    onSeedCleared: () -> Unit
) {
    @Suppress("LocalVariableName") val TAG = "QuizGameScreen"
    val quizState by quizGameViewModel.quizState.collectAsState()
    val seedPhrase by seedPhraseViewModel.seedWords.collectAsState()

    LaunchedEffect(seedPhrase) {
        seedPhrase.let { phrase ->
            if (phrase.size >= 6) {
                quizGameViewModel.generateQuiz(phrase)
            }
        }
    }

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

                Text(text = "What is the seed word at position ${question.seedIndex + 1}?",
                    style = MaterialTheme.typography.headlineSmall)
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

        FloatingActionButton(
            onClick = {
                Log.d(TAG, "Clear Stored Seed Phrase Triggered")
                seedPhraseViewModel.clearSeed()
                onSeedCleared()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            containerColor = MaterialTheme.colorScheme.error,
            elevation = FloatingActionButtonDefaults.elevation(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Clear Seed Phrase",
                    tint = MaterialTheme.colorScheme.onError
                )
                Text(
                    text = "Clear Stored Seed Phrase",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}
