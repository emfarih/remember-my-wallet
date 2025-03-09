package limited.m.remembermywallet.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import limited.m.remembermywallet.util.logDebug
import limited.m.remembermywallet.util.logError
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import androidx.core.content.edit

@Singleton
class SeedPhraseRepository @Inject constructor(@ApplicationContext context: Context) {
    private val tag = "SeedPhraseRepository"
    private val fileName = "secure_prefs"
    private val seedIndicesKey = "seed_indices"
    private val shuffleSeedKey = "shuffle_seed"

    private val sharedPreferences: SharedPreferences
    private val bip39WordList: List<String> by lazy { loadBip39Words(context) }

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        sharedPreferences = EncryptedSharedPreferences.create(
            context,
            fileName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    /** Load BIP-39 wordlist from assets */
    private fun loadBip39Words(context: Context): List<String> {
        return try {
            context.assets.open("bip39_english.txt").bufferedReader().readLines()
        } catch (e: Exception) {
            logError(tag, "Error loading BIP-39 word list: ${e.message}")
            emptyList()
        }
    }

    /** Generate and store a new shuffled seed phrase */
    fun storeNewSeedPhrase(seedWords: List<String>) {
        if (seedWords.size != 24) {
            logError(tag, "Invalid seed phrase: Must contain exactly 24 words")
            return
        }

        val newShuffleSeed = System.currentTimeMillis()
        val shuffledList = bip39WordList.shuffled(Random(newShuffleSeed))
        val indices = seedWords.mapNotNull { shuffledList.indexOf(it).takeIf { idx -> idx >= 0 } }

        if (indices.size != 24) {
            logError(tag, "Invalid seed words: Some words are not in the BIP-39 list")
            return
        }

        sharedPreferences.edit().apply {
            putLong(shuffleSeedKey, newShuffleSeed)
            putString(seedIndicesKey, indices.joinToString(","))
            apply()
        }

        logDebug(tag, "Seed phrase stored securely with new shuffle seed")
    }

    /** Retrieve the shuffled word list based on stored shuffle seed */
    fun getShuffledWordList(): List<String> {
        val shuffleSeed = sharedPreferences.getLong(shuffleSeedKey, System.currentTimeMillis()) // Default to current time
        logDebug("SeedPhraseRepository", "Retrieved shuffle seed: $shuffleSeed")

        val shuffledList = bip39WordList.shuffled(Random(shuffleSeed))
        logDebug("SeedPhraseRepository", "Generated shuffled word list with seed $shuffleSeed")

        return shuffledList
    }

    /** Retrieve a word by shuffled index */
    fun getWordByIndex(index: Int): String? {
        val shuffledList = getShuffledWordList()
        val word = shuffledList.getOrNull(index)

        logDebug("SeedPhraseRepository", "Retrieving word at index $index: ${word ?: "Not Found"}")

        return word
    }


    /** Retrieve stored seed indices */
    fun getStoredIndices(): List<Int> {
        return sharedPreferences.getString(seedIndicesKey, null)
            ?.split(",")?.mapNotNull { it.toIntOrNull() } ?: emptyList()
    }

    /** Check if a seed phrase exists */
    fun hasSeed(): Boolean = sharedPreferences.contains(seedIndicesKey)

    /** Reset the stored seed (delete indices and shuffle seed) */
    fun resetSeedPhrase() {
        sharedPreferences.edit { remove(seedIndicesKey).remove(shuffleSeedKey) }
        logDebug(tag, "Seed phrase and shuffle seed reset successfully")
    }
}
