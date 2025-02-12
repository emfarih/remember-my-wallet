package limited.m.remembermywallet.ui.quizgame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import limited.m.remembermywallet.viewmodel.QuizGameViewModel

@Composable
fun QuizGameScreen(viewModel: QuizGameViewModel = hiltViewModel()) {
    val quizState by viewModel.quizState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        quizState.currentQuestion?.let { question ->
            Text(text = "Select the correct word:", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            question.options.forEach { option ->
                Button(
                    onClick = { viewModel.checkAnswer(option) },
                    modifier = Modifier.fillMaxWidth().padding(4.dp)
                ) {
                    Text(text = option)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Score: ${quizState.score}")
        }
        FloatingActionButton(onClick = { viewModel.resetQuiz() }) {
            Text("Reset")
        }
    }
}