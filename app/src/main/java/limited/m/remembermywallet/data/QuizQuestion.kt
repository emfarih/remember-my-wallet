package limited.m.remembermywallet.data

/** Data class representing a quiz question */
data class QuizQuestion(
    val seedIndex: Int,
    val correctAnswer: String,
    val options: List<String>
)