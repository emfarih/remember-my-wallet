package limited.m.remembermywallet.util

import android.util.Log
import limited.m.remembermywallet.BuildConfig

/** Log only in debug mode */
fun logDebug(tag: String, message: String) {
    if (BuildConfig.DEBUG) Log.d(tag, message)
}

/** Log errors always */
fun logError(tag: String, message: String) {
    Log.e(tag, message)
}
