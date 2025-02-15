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

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> get() = _quizState

    private val _quizDialogState = MutableStateFlow<QuizDialogState>(QuizDialogState.None)
    val quizDialogState: StateFlow<QuizDialogState> get() = _quizDialogState

    private val _selectedWord = MutableStateFlow<String?>(null)
    val selectedWord: StateFlow<String?> get() = _selectedWord

    private val _positionInput = MutableStateFlow("")
    val positionInput: StateFlow<String> get() = _positionInput

    private val tag = "QuizGameViewModel"

    init {
        Log.d(tag, "Initializing QuizGameViewModel")
        loadSeedAndGenerateQuiz()
    }

    private fun loadSeedAndGenerateQuiz() {
        viewModelScope.launch {
            seedPhraseRepository.getSeedPhrase()?.let { seedPhrase ->
                if (seedPhrase.size >= 6) {
                    generateQuiz(seedPhrase)
                } else {
                    Log.e(tag, "Error: Seed phrase must have at least 6 words")
                }
            }
        }
    }

    private fun generateQuiz(seedPhrase: List<String>) {
        _quizState.value = QuizState(
            questions = List(6) {
                val seedIndex = seedPhrase.indices.random()
                repository.createQuestion(seedIndex, seedPhrase[seedIndex])
            },
            currentQuestionIndex = 0,
            score = 0
        )
        Log.d(tag, "Generated 6 quiz questions from seed phrase")
    }

    fun selectWord(word: String?) {  // ✅ Now accepts nullable String
        _selectedWord.value = word
    }


    fun updatePositionInput(input: String) {
        _positionInput.value = input
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
        val quizStateValue = _quizState.value
        val currentQuestion = quizStateValue.questions.getOrNull(quizStateValue.currentQuestionIndex) ?: return

        val isCorrect = selectedWord == currentQuestion.correctAnswer && userPosition - 1 == currentQuestion.seedIndex
        val newScore = if (isCorrect) quizStateValue.score + 1 else quizStateValue.score
        val nextIndex = quizStateValue.currentQuestionIndex + 1

        _quizState.value = quizStateValue.copy(
            score = newScore,
            currentQuestionIndex = nextIndex
        )

        Log.d(tag, "Answer: $selectedWord, Position: $userPosition, Correct: $isCorrect, Score: $newScore")

        if (nextIndex >= quizStateValue.questions.size) {
            _quizDialogState.value = QuizDialogState.QuizCompleted
        }
    }

    fun dismissQuizDialog() {
        _quizDialogState.value = QuizDialogState.None
    }

    fun restartQuiz() {
        loadSeedAndGenerateQuiz()
        dismissQuizDialog()
    }
}

sealed class QuizDialogState {
    data object None : QuizDialogState()
    data object QuizCompleted : QuizDialogState()
}
