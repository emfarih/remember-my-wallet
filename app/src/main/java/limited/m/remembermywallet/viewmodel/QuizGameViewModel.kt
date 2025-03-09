package limited.m.remembermywallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import limited.m.remembermywallet.data.QuizRepository
import limited.m.remembermywallet.data.QuizState
import limited.m.remembermywallet.data.SeedPhraseRepository
import limited.m.remembermywallet.util.logDebug
import limited.m.remembermywallet.util.logError
import javax.inject.Inject

@HiltViewModel
class QuizGameViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val seedPhraseRepository: SeedPhraseRepository
) : ViewModel() {

    private val _quizState = MutableStateFlow(QuizState(score = 0))
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    private val _selectedWord = MutableStateFlow<String?>(null)
    val selectedWord: StateFlow<String?> = _selectedWord.asStateFlow()

    private val _positionInput = MutableStateFlow("")
    val positionInput: StateFlow<String> = _positionInput.asStateFlow()

    init {
        logDebug(TAG, "Initializing QuizGameViewModel")
        generateNewQuestion()
    }

    /** Generate a new quiz question */
    private fun generateNewQuestion() {
        viewModelScope.launch {
            val seedIndex = (0 until 24).random()
            val newQuestion = quizRepository.createQuestion(seedIndex)

            if (newQuestion != null) {
                _quizState.value = _quizState.value.copy(
                    questions = listOf(newQuestion),
                    currentQuestionIndex = 0
                )
                logDebug(TAG, "Generated new quiz question: ${newQuestion.correctAnswer}")
            } else {
                logError(TAG, "Failed to generate a valid question.")
            }
        }
    }

    fun selectWord(word: String?) {
        _selectedWord.value = word
    }

    fun updatePositionInput(input: String) {
        _positionInput.value = input.filter { it.isDigit() }
    }

    fun submitAnswer() {
        val word = _selectedWord.value ?: return
        val position = _positionInput.value.toIntOrNull()
        if (position != null) {
            checkAnswer(word, position)
            _selectedWord.value = null
            _positionInput.value = ""
        }
    }

    private fun checkAnswer(selectedWord: String, userPosition: Int) {
        viewModelScope.launch {
            val shuffledIndices = seedPhraseRepository.getStoredIndices()
            val correctIndex = shuffledIndices.getOrNull(userPosition - 1) ?: return@launch
            val correctWord = seedPhraseRepository.getWordByIndex(correctIndex)

            val isCorrect = selectedWord == correctWord
            if (isCorrect) {
                _quizState.value = _quizState.value.copy(score = _quizState.value.score + 1)
            }

            logDebug(TAG, "Answer: $selectedWord, Position: $userPosition, Correct: $isCorrect, Score: ${_quizState.value.score}")

            generateNewQuestion()
        }
    }

    companion object {
        private const val TAG = "QuizGameViewModel"
    }
}
