package limited.m.remembermywallet.ui.quizgame

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import limited.m.remembermywallet.viewmodel.QuizGameViewModel

@Composable
fun QuizGameScreen(viewModel: QuizGameViewModel = hiltViewModel()) {
    @Suppress("LocalVariableName") val TAG = "QuizGameScreen"
    val quizState by viewModel.quizState.collectAsState()

    Log.d(TAG, "QuizGameScreen Composable Loaded")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
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
                        viewModel.checkAnswer(option)
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

        FloatingActionButton(onClick = {
            Log.d(TAG, "Quiz Reset Triggered")
            viewModel.resetQuiz()
        }) {
            Text("Reset")
        }
    }
}
