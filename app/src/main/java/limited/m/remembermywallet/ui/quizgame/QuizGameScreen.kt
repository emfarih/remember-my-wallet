package limited.m.remembermywallet.ui.quizgame

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import limited.m.remembermywallet.viewmodel.QuizDialogState
import limited.m.remembermywallet.viewmodel.QuizGameViewModel
import limited.m.remembermywallet.viewmodel.SeedPhraseViewModel

@Composable
fun QuizGameScreen(
    quizGameViewModel: QuizGameViewModel = hiltViewModel(),
    seedPhraseViewModel: SeedPhraseViewModel = hiltViewModel(),
    onSeedCleared: () -> Unit,
    onExitTap: () -> Unit
) {
    @Suppress("LocalVariableName") val TAG = "QuizGameScreen"

    val quizState by quizGameViewModel.quizState.collectAsState()
    val quizDialogState by quizGameViewModel.quizDialogState.collectAsState()
    val selectedWord by quizGameViewModel.selectedWord.collectAsState()
    val positionInput by quizGameViewModel.positionInput.collectAsState()

    var showClearSeedDialog by remember { mutableStateOf(false) }

    Log.d(TAG, "QuizGameScreen Loaded")

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

                Text(
                    text = "Select the correct seed word:",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                WordSelection(question.options) { quizGameViewModel.selectWord(it) }

                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Score: ${quizState.score}")
                Log.d(TAG, "Current Score: ${quizState.score}")
            }
        }

        selectedWord?.let { word ->
            PositionInputDialog(
                selectedWord = word,
                positionInput = positionInput,
                onPositionChange = { quizGameViewModel.updatePositionInput(it) },
                onConfirm = {
                    quizGameViewModel.submitAnswer()
                },
                onDismiss = { quizGameViewModel.selectWord(null) }
            )
        }

        FloatingActionButton(
            onClick = { showClearSeedDialog = true },
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

        if (showClearSeedDialog) {
            ConfirmClearSeedDialog(
                onConfirm = {
                    Log.d(TAG, "Clear Stored Seed Phrase Confirmed")
                    seedPhraseViewModel.clearSeed()
                    onSeedCleared()
                    showClearSeedDialog = false
                },
                onDismiss = { showClearSeedDialog = false }
            )
        }

        if (quizDialogState is QuizDialogState.QuizCompleted) {
            QuizCompletedDialog(
                score = quizState.score,
                onRetry = { quizGameViewModel.restartQuiz() },
                onExit = onExitTap
            )
        }
    }
}

@Composable
fun WordSelection(options: List<String>, onWordSelected: (String) -> Unit) {
    Column {
        options.forEach { option ->
            Button(
                onClick = { onWordSelected(option) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(text = option)
            }
        }
    }
}

@Composable
fun PositionInputDialog(
    selectedWord: String,
    positionInput: String,
    onPositionChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Position") },
        text = {
            Column {
                Text("You selected \"$selectedWord\". Now enter its correct position:")
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = positionInput,
                    onValueChange = { newValue ->
                        // Only allow digits and enforce range 1-24
                        val filteredValue = newValue.filter { it.isDigit() }
                        val number = filteredValue.toIntOrNull()

                        if (number == null || number in 1..24) {
                            onPositionChange(filteredValue)
                        }
                    },
                    label = { Text("Seed Position (1-24)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val userPosition = positionInput.toIntOrNull()
                    if (userPosition in 1..24) {
                        onConfirm()
                    }
                },
                enabled = positionInput.toIntOrNull() in 1..24 // ✅ Only enable if valid
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ConfirmClearSeedDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion") },
        text = { Text("Are you sure you want to clear the stored seed phrase? This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Yes, Clear")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun QuizCompletedDialog(score: Int, onRetry: () -> Unit, onExit: () -> Unit) {
    AlertDialog(
        onDismissRequest = onExit,
        title = { Text("Quiz Completed") },
        text = { Text("Your final score is $score. Would you like to retry the quiz?") },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text("Retry")
            }
        },
        dismissButton = {
            TextButton(onClick = onExit) {
                Text("Exit")
            }
        }
    )
}
