package me.wishwith.android.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    private const val PREF_KEY = "appLocale"

    fun getLocale(prefs: SharedPreferences): String {
        return prefs.getString(PREF_KEY, "en") ?: "en"
    }

    fun setLocale(prefs: SharedPreferences, locale: String) {
        prefs.edit().putString(PREF_KEY, locale).apply()
    }

    fun applyLocale(context: Context, locale: String): Context {
        val loc = Locale(locale)
        Locale.setDefault(loc)
        val config = Configuration(context.resources.configuration)
        config.setLocale(loc)
        return context.createConfigurationContext(config)
    }

    fun wrapContext(context: Context, prefs: SharedPreferences): Context {
        val locale = getLocale(prefs)
        return applyLocale(context, locale)
    }
}
