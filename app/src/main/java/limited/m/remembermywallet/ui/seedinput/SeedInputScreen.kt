package limited.m.remembermywallet.ui.seedinput

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import limited.m.remembermywallet.viewmodel.SeedPhraseViewModel
import limited.m.remembermywallet.BuildConfig // Import for checking debug mode

@Composable
fun SeedInputScreen(
    viewModel: SeedPhraseViewModel = hiltViewModel(),
    onSeedStored: () -> Unit
) {
    val seedWords by viewModel.seedWords.collectAsState()
    val isSeedStored by viewModel.isSeedStored.collectAsState()
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(isSeedStored) {
        if (isSeedStored) onSeedStored()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Enter Your Seed Phrase",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        SeedInputGrid(seedWords, viewModel)

        Spacer(modifier = Modifier.height(10.dp))

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        StoreSeedButton(
            onClick = {
                if (viewModel.validateSeedPhrase()) {
                    viewModel.storeSeedPhrase()
                    errorMessage = ""
                } else {
                    errorMessage = "Invalid seed phrase! Please enter exactly 24 valid words."
                }
            },
            isEnabled = viewModel.validateSeedPhrase()
        )

        if (BuildConfig.DEBUG) { // Only show in debug mode
            Spacer(modifier = Modifier.height(10.dp))
            PrepopulateSeedButton { viewModel.prepopulateSeedWords() }
        }
    }
}

@Composable
fun SeedInputGrid(seedWords: List<String>, viewModel: SeedPhraseViewModel) {
    val focusManager = LocalFocusManager.current

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(24) { index ->
            val word = seedWords.getOrNull(index) ?: ""
            val isValid = viewModel.isValidSeedWord(word)

            OutlinedTextField(
                value = word,
                onValueChange = { viewModel.updateSeedWord(index, it) },
                label = { Text("${index + 1}") },
                isError = !isValid,
                supportingText = {
                    if (!isValid) {
                        Text("Invalid seed word", color = MaterialTheme.colorScheme.error)
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = if (index < 23) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Next) },
                    onDone = { focusManager.clearFocus() }
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun StoreSeedButton(onClick: () -> Unit, isEnabled: Boolean) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = isEnabled
    ) {
        Text("Store Seed Phrase")
    }
}

@Composable
fun PrepopulateSeedButton(onPrepopulate: () -> Unit) {
    Button(
        onClick = onPrepopulate,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Prepopulate Seed Phrase (Test)")
    }
}

@Preview(showBackground = true)
@Composable
fun SeedInputScreenPreview() {
    SeedInputScreen(onSeedStored = {})
}
