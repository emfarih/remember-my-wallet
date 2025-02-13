package limited.m.remembermywallet.data

data class QuizQuestion(
    val seedIndex: Int, // Position of the word in the seed phrase
    val correctAnswer: String,
    val options: List<String>
)
