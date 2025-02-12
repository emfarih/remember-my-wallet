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
        loadNewQuestion()
    }

    private fun loadNewQuestion() {
        val newQuestion = repository.getRandomQuestion()
        _quizState.value = _quizState.value.copy(currentQuestion = newQuestion)
        Log.d(tag, "New question loaded: $newQuestion")
    }

    fun checkAnswer(selectedAnswer: String) {
        val isCorrect = selectedAnswer == _quizState.value.currentQuestion?.correctAnswer
        val newScore = if (isCorrect) _quizState.value.score + 1 else _quizState.value.score
        _quizState.value = _quizState.value.copy(score = newScore)

        Log.d(tag, "Answer selected: $selectedAnswer, Correct: $isCorrect, New Score: $newScore")
        loadNewQuestion()
    }

    fun resetQuiz() {
        _quizState.value = QuizState()
        Log.d(tag, "Quiz reset")
        loadNewQuestion()
    }
}
