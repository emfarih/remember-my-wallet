import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SeedInputViewModel @Inject constructor() : ViewModel() {

    private val _seedWords = MutableStateFlow(List(24) { "" }) // Enforces 24 words
    val seedWords: StateFlow<List<String>> = _seedWords

    private val _isSeedStored = MutableStateFlow(false)
    val isSeedStored: StateFlow<Boolean> = _isSeedStored

    fun updateSeedWord(index: Int, word: String) {
        if (index in 0..23) {
            _seedWords.value = _seedWords.value.toMutableList().apply { this[index] = word.trim() }
        }
    }

    fun validateSeedPhrase(): Boolean {
        return _seedWords.value.all { it.isNotBlank() } // Ensures all 24 words are filled
    }

    fun storeSeedPhrase() {
        if (validateSeedPhrase()) {
            viewModelScope.launch {
                // TODO: Encrypt and store securely (Keystore / EncryptedSharedPreferences)
                _isSeedStored.value = true
            }
        }
    }

//    fun resetSeedPhrase() {
//        _seedWords.value = List(24) { "" }
//        _isSeedStored.value = false
//    }
}
