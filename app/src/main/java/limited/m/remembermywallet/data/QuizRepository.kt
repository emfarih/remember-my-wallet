package limited.m.remembermywallet.data

import android.content.Context
import android.util.Log
import javax.inject.Inject

class QuizRepository @Inject constructor(private val context: Context) {

    private val bip39WordList: List<String> by lazy {
        loadBip39Words()
    }

    private fun loadBip39Words(): List<String> {
        return try {
            context.assets.open("bip39_english.txt")
                .bufferedReader()
                .readLines()
        } catch (e: Exception) {
            Log.e("QuizRepository", "Error loading BIP-39 word list", e)
            emptyList() // Return empty list if an error occurs
        }
    }

    fun createQuestion(seedIndex: Int, correctWord: String): QuizQuestion {
        Log.d("QuizRepository", "Creating question for seed index: $seedIndex with word: $correctWord")

        if (bip39WordList.isEmpty()) {
            Log.e("QuizRepository", "BIP-39 word list is empty!")
            return QuizQuestion(seedIndex, correctWord, listOf(correctWord))
        }

        // Get incorrect options from BIP-39 list, excluding the correct word
        val possibleOptions = bip39WordList.filter { it != correctWord }.shuffled()

        // Pick 3 random incorrect answers
        val incorrectOptions = possibleOptions.take(5)

        // Combine correct answer with incorrect options, then shuffle
        val finalOptions = (incorrectOptions + correctWord).shuffled()

        return QuizQuestion(
            seedIndex = seedIndex,
            correctAnswer = correctWord,
            options = finalOptions
        )
    }
}
