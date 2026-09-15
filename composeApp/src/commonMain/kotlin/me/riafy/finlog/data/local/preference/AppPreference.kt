package me.riafy.finlog.data.local.preference

import com.russhwolf.settings.Settings

/** Wraps multiplatform settings so the rest of the app reads preferences as plain properties **/
class AppPreference(private val settings: Settings) {

    companion object {
        const val THEME_MODE = "THEME_MODE"
        const val CURRENCY_CODE = "CURRENCY_CODE"
        const val FIRST_TIME_LAUNCH = "FIRST_TIME_LAUNCH"

        const val THEME_SYSTEM = "SYSTEM"
        const val THEME_LIGHT = "LIGHT"
        const val THEME_DARK = "DARK"

        const val DEFAULT_CURRENCY_CODE = "INR"
    }

    fun clearPreferences() {
        settings.clear()
    }

    var themeMode: String
        get() = settings.getString(THEME_MODE, THEME_SYSTEM)
        set(value) = settings.putString(THEME_MODE, value)

    var currencyCode: String
        get() = settings.getString(CURRENCY_CODE, DEFAULT_CURRENCY_CODE)
        set(value) = settings.putString(CURRENCY_CODE, value)

    var firstTimeLaunch: Boolean
        get() = settings.getBoolean(FIRST_TIME_LAUNCH, true)
        set(value) = settings.putBoolean(FIRST_TIME_LAUNCH, value)
}
