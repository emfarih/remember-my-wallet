package limited.m.remembermywallet.viewmodel

import android.util.Log
import limited.m.remembermywallet.data.SecureStorage
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeedPhraseViewModel @Inject constructor(private val secureStorage: SecureStorage) : ViewModel() {

    private val tag = "SeedInputViewModel"

    private val _seedWords = MutableStateFlow(List(24) { "" }) // Always enforce 24 words
    val seedWords: StateFlow<List<String>> = _seedWords

    private val _isSeedStored = MutableStateFlow(false)
    val isSeedStored: StateFlow<Boolean> = _isSeedStored

    init {
        checkSeed()
    }

    /** Check if a seed exists and load it */
    private fun checkSeed() {
        viewModelScope.launch {
            val stored = secureStorage.hasSeed()
            _isSeedStored.value = stored
            _seedWords.value = secureStorage.getSeedPhrase() ?: List(24) { "" } // Ensure always 24 words
            Log.d(tag, "Seed stored: $stored, words: ${_seedWords.value}")
        }
    }

    /** Validate seed phrase (all words must be non-blank) */
    fun validateSeedPhrase(): Boolean {
        return _seedWords.value.all { it.isNotBlank() }
    }

    /** Store the seed phrase */
    fun storeSeedPhrase() {
        if (validateSeedPhrase()) {
            viewModelScope.launch {
                secureStorage.storeSeedPhrase(_seedWords.value) // SecureStorage handles joining
                _isSeedStored.value = true
                Log.d(tag, "Stored seed phrase: ${_seedWords.value}")
            }
        }
    }

    /** Update a single word in the seed phrase */
    fun updateSeedWord(index: Int, word: String) {
        if (index in 0..23) {
            _seedWords.value = _seedWords.value.toMutableList().apply {
                this[index] = word.trim()
            }
        }
    }

    /** Clear the stored seed */
    fun clearSeed() {
        viewModelScope.launch {
            secureStorage.clearSeedPhrase()
            _isSeedStored.value = false
            _seedWords.value = List(24) { "" } // Reset seed words
            Log.d(tag, "Seed phrase cleared")
        }
    }
}
