package limited.m.remembermywallet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import limited.m.remembermywallet.data.SeedPhraseRepository
import limited.m.remembermywallet.util.logDebug
import javax.inject.Inject

@HiltViewModel
class SeedPhraseViewModel @Inject constructor(
    private val seedPhraseRepository: SeedPhraseRepository
) : ViewModel() {

    private val _seedWords = MutableStateFlow(List(24) { "" }) // Always 24 words
    val seedWords: StateFlow<List<String>> = _seedWords.asStateFlow()

    private val _isSeedStored = MutableStateFlow(false)
    val isSeedStored: StateFlow<Boolean> = _isSeedStored.asStateFlow()

    private val shuffledWordList = seedPhraseRepository.getShuffledWordList()

    init {
        checkSeed()
    }

    fun prepopulateSeedWords() {
        _seedWords.value = listOf(
            "abandon", "ability", "able", "about", "above", "absent", "absorb", "abstract",
            "absurd", "abuse", "access", "accident", "account", "accuse", "achieve", "acid",
            "acoustic", "acquire", "across", "act", "action", "actor", "actress", "actual"
        )
    }

    /** Check if a seed exists and load it */
    private fun checkSeed() {
        viewModelScope.launch {
            val stored = seedPhraseRepository.hasSeed()
            _isSeedStored.value = stored

            val storedIndices = seedPhraseRepository.getStoredIndices()
            _seedWords.value = storedIndices.map { shuffledWordList.getOrElse(it) { "" } }

            logDebug("SeedPhraseViewModel", "Seed stored: $stored, words: ${_seedWords.value}")
        }
    }

    /** Validate if all words are non-blank and exist in the shuffled word list */
    fun validateSeedPhrase(): Boolean {
        val seedWords = _seedWords.value

        if (seedWords.isEmpty()) {
            logDebug("SeedPhraseViewModel", "Validation failed: Seed words list is empty.")
            return false
        }

        val isValid = seedWords.all { it.isNotBlank() && it in shuffledWordList }
        logDebug("SeedPhraseViewModel", "Seed phrase validation result: $isValid")

        return isValid
    }

    fun isValidSeedWord(word: String): Boolean {
        val isValid = word.isBlank() || word in shuffledWordList
        logDebug("SeedPhraseRepository", "Validating word: '$word' -> isValid: $isValid")
        return isValid
    }

    /** Store the seed phrase */
    fun storeSeedPhrase() {
        if (validateSeedPhrase()) {
            viewModelScope.launch {
                seedPhraseRepository.storeNewSeedPhrase(_seedWords.value)
                _isSeedStored.value = true
                logDebug("SeedPhraseViewModel", "Stored seed phrase securely")
            }
        }
    }

    /** Update a single word in the seed phrase */
    fun updateSeedWord(index: Int, word: String) {
        if (index in _seedWords.value.indices) {
            _seedWords.update { it.toMutableList().apply { this[index] = word.trim() } }
        }
    }

    /** Clear the stored seed */
    fun clearSeed() {
        viewModelScope.launch {
            seedPhraseRepository.resetSeedPhrase()
            _isSeedStored.value = false
            _seedWords.value = List(24) { "" }
            logDebug("SeedPhraseViewModel", "Seed phrase reset successfully")
        }
    }
}
