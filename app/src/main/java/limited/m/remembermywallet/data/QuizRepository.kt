package limited.m.remembermywallet.data

import android.util.Log
import javax.inject.Inject

class QuizRepository @Inject constructor() {
    private val seedWords = listOf("apple", "banana", "cherry", "date", "elderberry")

    fun getRandomQuestion(): QuizQuestion {
        Log.d("QuizRepository", "Generating random question...")

        val correctWord = seedWords.random()
        Log.d("QuizRepository", "Selected correct word: $correctWord")

        val shuffledWords = seedWords.shuffled()
        Log.d("QuizRepository", "Shuffled words: $shuffledWords")

        val options = shuffledWords.take(3) + correctWord
        Log.d("QuizRepository", "Options before shuffle: $options")

        val finalOptions = options.shuffled()
        Log.d("QuizRepository", "Final options: $finalOptions")

        return QuizQuestion(correctAnswer = correctWord, options = finalOptions)
    }
}
