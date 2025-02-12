import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun SeedInputScreen(
    viewModel: SeedInputViewModel = viewModel(),
    onSeedStored: () -> Unit // Callback for navigation
) {
    var seedPhrase by remember { mutableStateOf(TextFieldValue("")) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Enter Your Seed Phrase", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(12.dp))

        BasicTextField(
            value = seedPhrase,
            onValueChange = { seedPhrase = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(8.dp),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.CenterStart
                ) {
                    if (seedPhrase.text.isEmpty()) {
                        Text("Enter 12 or 24 words...", color = Color.Gray)
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (viewModel.validateSeedPhrase(seedPhrase.text)) {
                    viewModel.storeSeedPhrase(seedPhrase.text)
                    onSeedStored()
                } else {
                    errorMessage = "Invalid seed phrase! Please enter 12 or 24 words."
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Store Seed Phrase")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SeedInputScreenPreview() {
    SeedInputScreen(onSeedStored = {})
}
