package limited.m.remembermywallet.data

import limited.m.remembermywallet.util.logDebug
import limited.m.remembermywallet.util.logError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizRepository @Inject constructor(
    private val seedPhraseRepository: SeedPhraseRepository
) {
    private val tag = "QuizRepository"

    /** Generate a quiz question with retry logic */
    fun createQuestion(seedIndex: Int): QuizQuestion? {
        val shuffledList = seedPhraseRepository.getShuffledWordList()
        val storedIndices = seedPhraseRepository.getStoredIndices()

        if (storedIndices.isEmpty() || shuffledList.isEmpty()) {
            logError(tag, "No valid seed phrase stored! Unable to generate quiz question.")
            return null
        }

        val validSeedIndices = storedIndices.indices.toList()
        var attemptIndex = seedIndex

        repeat(3) { // Try up to 3 times
            if (attemptIndex in validSeedIndices) {
                val correctShuffledIndex = storedIndices[attemptIndex]
                if (correctShuffledIndex in shuffledList.indices) {
                    return generateQuestion(quizIndex = attemptIndex, shuffledList, correctShuffledIndex)
                }
            }
            attemptIndex = validSeedIndices.random() // Try another index
        }

        logError(tag, "Failed to generate a valid quiz question after retries.")
        return null
    }

    /** Generate the quiz question and options */
    private fun generateQuestion(quizIndex: Int, shuffledList: List<String>, correctShuffledIndex: Int): QuizQuestion {
        val correctWord = shuffledList[correctShuffledIndex]

        val incorrectOptions = shuffledList
            .filter { it != correctWord }
            .shuffled()
            .take(5) // Take 5 incorrect words, or as many as available

        val finalOptions = (incorrectOptions + correctWord).shuffled()

        logDebug(tag, "Quiz question created: $correctWord (Index: $quizIndex) Options: $finalOptions")

        return QuizQuestion(
            seedIndex = quizIndex,
            correctAnswer = correctWord,
            options = finalOptions
        )
    }
}
