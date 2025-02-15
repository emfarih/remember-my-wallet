package limited.m.remembermywallet.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import limited.m.remembermywallet.data.QuizRepository
import limited.m.remembermywallet.data.QuizState
import limited.m.remembermywallet.data.SeedPhraseRepository
import javax.inject.Inject

@HiltViewModel
class QuizGameViewModel @Inject constructor(
    private val repository: QuizRepository,
    private val seedPhraseRepository: SeedPhraseRepository
) : ViewModel() {

    private val _quizState = MutableStateFlow(QuizState(score = 0))
    val quizState: StateFlow<QuizState> get() = _quizState

    private val _selectedWord = MutableStateFlow<String?>(null)
    val selectedWord: StateFlow<String?> get() = _selectedWord

    private val _positionInput = MutableStateFlow("")
    val positionInput: StateFlow<String> get() = _positionInput

    private val tag = "QuizGameViewModel"

    init {
        Log.d(tag, "Initializing QuizGameViewModel")
        generateNewQuestion()
    }

    private fun generateNewQuestion() {
        viewModelScope.launch {
            val seedPhrase = seedPhraseRepository.getSeedPhrase()
            if (seedPhrase.isNullOrEmpty() || seedPhrase.size < 24) {
                Log.e(tag, "Error: Seed phrase must have at least 24 words")
                return@launch
            }

            val seedIndex = (0 until 24).random()
            val newQuestion = repository.createQuestion(seedIndex, seedPhrase[seedIndex])

            _quizState.value = _quizState.value.copy(
                questions = listOf(newQuestion),
                currentQuestionIndex = 0
            )

            Log.d(tag, "Generated new quiz question: ${newQuestion.correctAnswer}")
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
        val currentQuestion = _quizState.value.questions.firstOrNull() ?: return

        val isCorrect = selectedWord == currentQuestion.correctAnswer && userPosition - 1 == currentQuestion.seedIndex
        val newScore = if (isCorrect) _quizState.value.score + 1 else _quizState.value.score

        _quizState.value = _quizState.value.copy(score = newScore)
        Log.d(tag, "Answer: $selectedWord, Position: $userPosition, Correct: $isCorrect, Score: $newScore")

        generateNewQuestion()
    }
}
