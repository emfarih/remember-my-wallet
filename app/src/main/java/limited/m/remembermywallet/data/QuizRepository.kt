package limited.m.remembermywallet.data

import javax.inject.Inject

class QuizRepository @Inject constructor() {
    private val seedWords = listOf("apple", "banana", "cherry", "date", "elderberry")

    fun getRandomQuestion(): QuizQuestion {
        val correctWord = seedWords.random()
        val options = seedWords.shuffled().take(3) + correctWord
        return QuizQuestion(correctAnswer = correctWord, options = options.shuffled())
    }
}