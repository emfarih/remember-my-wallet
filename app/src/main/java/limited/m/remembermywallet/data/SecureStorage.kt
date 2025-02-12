package limited.m.remembermywallet.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureStorage @Inject constructor(@ApplicationContext context: Context) {
    private val tag = "SecureStorage"
    private val fileName = "secure_prefs"
    private val seedPhrase = "seed_phrase"

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        fileName,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /** Store seed as a space-separated string */
    fun storeSeedPhrase(seedWords: List<String>) {
        val seedPhrase = seedWords.joinToString(" ")
        sharedPreferences.edit().putString(this.seedPhrase, seedPhrase).apply()
        Log.d(tag, "Stored seed phrase: $seedPhrase")
    }

    /** Check if a seed exists */
    fun hasSeed(): Boolean {
        val stored = sharedPreferences.contains(seedPhrase)
        Log.d(tag, "Checking seed: $stored")
        return stored
    }

    /** Retrieve the stored seed phrase as a list */
    fun getSeedPhrase(): List<String>? {
        val seedPhrase = sharedPreferences.getString(seedPhrase, null)
        Log.d(tag, "Retrieved seed: $seedPhrase")
        return seedPhrase?.split(" ") // Convert back to list
    }

    /** Clear the stored seed */
//    fun clearSeedPhrase() {
//        sharedPreferences.edit().remove(seedPhrase).apply()
//        Log.d(tag, "Seed phrase cleared")
//    }
}
