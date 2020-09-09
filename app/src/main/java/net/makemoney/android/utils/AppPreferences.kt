package net.makemoney.android.utils

import net.makemoney.android.extensions.appContext
import org.jetbrains.anko.defaultSharedPreferences

object AppPreferences {

    private val sPrefs = appContext.defaultSharedPreferences
    private const val TOKEN = "pref.token"
    private const val PRIMARY_PROMO = "pref.primary.promo"
    private const val SECONDARY_PROMO = "pref.secondary.promo"
    private const val TRANSACTION_PHONE_NUMBER = "transaction.phone"
    private const val TRANSACTION_WEBMONEY = "transaction.webmoney"
    private const val TRANSACTION_YANDEXMONEY = "transaction.yandexmoney"
    private const val TRANSACTION_EMAIL = "transaction.email"
    private const val TRANSACTION_WARFACE = "transaction.warface"
    private const val TRANSACTION_STEAM = "transaction.steam"
    private const val TRANSACTION_VK = "transaction.vk"
    private const val PRIVACY_POLICY_ACCEPTING = "privacy.policy.accepting"
    private const val GAME_SWIPE_SWIPETIMES = "game_swipe_times"
    private const val GAME_SWIPE_DATE = "game_swipe_date"

    fun clear() {
        sPrefs.edit().clear().apply()
    }

    var token: String
        get() = sPrefs.getString(TOKEN, "") ?: ""
        set(value) = sPrefs.edit().putString(TOKEN, value).apply()

    var primaryPromo
        get() = sPrefs.getString(PRIMARY_PROMO, "") ?: ""
        set(value) = sPrefs.edit().putString(PRIMARY_PROMO, value).apply()

    var secondaryPromo
        get() = sPrefs.getString(SECONDARY_PROMO, "") ?: ""
        set(value) = sPrefs.edit().putString(SECONDARY_PROMO, value).apply()

    var transactionPhoneNumber: String
        get() = sPrefs.getString(TRANSACTION_PHONE_NUMBER, "") ?: ""
        set(value) = sPrefs.edit().putString(TRANSACTION_PHONE_NUMBER, value).apply()

    var transactionWebMoney: String
        get() = sPrefs.getString(TRANSACTION_WEBMONEY, "") ?: ""
        set(value) = sPrefs.edit().putString(TRANSACTION_WEBMONEY, value).apply()

    var transactionYandexMoney: String
        get() = sPrefs.getString(TRANSACTION_YANDEXMONEY,"") ?: ""
        set(value) = sPrefs.edit().putString(TRANSACTION_YANDEXMONEY, value).apply()

    var transactionEmail: String
        get() = sPrefs.getString(TRANSACTION_EMAIL, "") ?: ""
        set(value) = sPrefs.edit().putString(TRANSACTION_EMAIL, value).apply()

    var transactionWarface: String
        get() = sPrefs.getString(TRANSACTION_WARFACE, "") ?: ""
        set(value) = sPrefs.edit().putString(TRANSACTION_WARFACE, value).apply()

    var transactionSteam: String
        get() = sPrefs.getString(TRANSACTION_STEAM, "") ?: ""
        set(value) = sPrefs.edit().putString(TRANSACTION_STEAM, value).apply()

    var transactionVk: String
        get() = sPrefs.getString(TRANSACTION_VK, "") ?: ""
        set(value) = sPrefs.edit().putString(TRANSACTION_VK, value).apply()

    var privacyPolicyIsAccepted: Boolean
        get() = sPrefs.getBoolean(PRIVACY_POLICY_ACCEPTING, false)
        set(value) = sPrefs.edit().putBoolean(PRIVACY_POLICY_ACCEPTING, value).apply()

    var gameSwipeTimes: Int
        get() = sPrefs.getInt(GAME_SWIPE_SWIPETIMES, 0)
        set(value) = sPrefs.edit().putInt(GAME_SWIPE_SWIPETIMES, value).apply()

    var gameSwipeDateStart: String
        get() = sPrefs.getString(GAME_SWIPE_DATE, "") ?: ""
        set(value) = sPrefs.edit().putString(GAME_SWIPE_DATE, value).apply()
}
