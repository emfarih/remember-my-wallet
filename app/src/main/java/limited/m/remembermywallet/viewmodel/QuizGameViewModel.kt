package limited.m.remembermywallet.viewmodel

import android.util.Log
import limited.m.remembermywallet.data.QuizRepository
import limited.m.remembermywallet.data.QuizState
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class QuizGameViewModel @Inject constructor(private val repository: QuizRepository) : ViewModel() {

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState
    private val tag = "QuizGameViewModel"

    init {
        Log.d(tag, "Initializing QuizGameViewModel")
    }

    fun generateQuiz(seedPhrase: List<String>) {
        if (seedPhrase.size < 6) {
            Log.e(tag, "Error: Seed phrase must have at least 6 words")
            return
        }

        val questions = (0 until 6).map { index ->
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
    }
}
