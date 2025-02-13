package limited.m.remembermywallet.viewmodel

import android.util.Log
import limited.m.remembermywallet.data.QuizRepository
import limited.m.remembermywallet.data.QuizState
import limited.m.remembermywallet.data.SeedPhraseRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizGameViewModel @Inject constructor(
    private val repository: QuizRepository,
    private val seedPhraseRepository: SeedPhraseRepository
) : ViewModel() {

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState
    private val _quizDialogState = MutableStateFlow<QuizDialogState>(QuizDialogState.None)
    val quizDialogState: StateFlow<QuizDialogState> = _quizDialogState
    private val tag = "QuizGameViewModel"

    init {
        Log.d(tag, "Initializing QuizGameViewModel")
        loadSeedAndGenerateQuiz()
    }

    private fun loadSeedAndGenerateQuiz() {
        viewModelScope.launch {
            val seedPhrase = seedPhraseRepository.getSeedPhrase()
            if (seedPhrase != null && seedPhrase.size >= 6) {
                generateQuiz(seedPhrase)
            } else {
                Log.e(tag, "Error: Seed phrase must have at least 6 words")
            }
        }
    }

    private fun generateQuiz(seedPhrase: List<String>) {
        val questions = (0 until 6).map { _ ->
            val seedIndex = (seedPhrase.indices).random()
            repository.createQuestion(seedIndex, seedPhrase[seedIndex])
        }

        _quizState.value = QuizState(questions = questions, currentQuestionIndex = 0, score = 0)
        Log.d(tag, "Generated 6 quiz questions from seed phrase")
    }

    fun checkAnswer(selectedAnswer: String) {
        val currentQuestion = _quizState.value.questions.getOrNull(_quizState.value.currentQuestionIndex)
        if (currentQuestion == null) {
            Log.e(tag, "Error: No current question available!")
            return
        }

        val isCorrect = selectedAnswer == currentQuestion.correctAnswer
        val newScore = if (isCorrect) _quizState.value.score + 1 else _quizState.value.score
        val nextIndex = _quizState.value.currentQuestionIndex + 1

        _quizState.value = _quizState.value.copy(
            score = newScore,
            currentQuestionIndex = nextIndex.coerceAtMost(_quizState.value.questions.size - 1)
        )

        Log.d(tag, "Answer selected: $selectedAnswer, Correct: $isCorrect, New Score: $newScore")

        if (nextIndex >= _quizState.value.questions.size) {
            showQuizDialog(QuizDialogState.QuizCompleted)
        }
    }

    private fun showQuizDialog(dialog: QuizDialogState) {
        _quizDialogState.value = dialog
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
