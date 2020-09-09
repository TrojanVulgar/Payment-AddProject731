package net.makemoney.android.utils

import net.makemoney.android.R
import net.makemoney.android.data.models.withdrawal.WithdrawalCardType
import net.makemoney.android.extensions.getString
import java.util.*


/**
 * Used to get background image for a cards, card titles
 */
object CardBackgroundProvider {

    fun getProfileCardBackground(level: Int): Int = when (level) {
        1 -> R.drawable.bg_profile_level_1
        2 -> R.drawable.bg_profile_level_2
        3 -> R.drawable.bg_profile_level_3
        4 -> R.drawable.bg_profile_level_4
        5 -> R.drawable.bg_profile_level_5
        else -> R.drawable.bg_profile_level_1
    }

    fun getProfileLevelName(level: Int): String = when (level) {
        1 -> getString(R.string.profile_level_name_1, level)
        2 -> getString(R.string.profile_level_name_2, level)
        3 -> getString(R.string.profile_level_name_3, level)
        4 -> getString(R.string.profile_level_name_4, level)
        5 -> getString(R.string.profile_level_name_5, level)
        else -> getString(R.string.profile_level_name_1, 0)
    }

    fun getPartnerBackgroundCard(): Int = getPartnerBackgroundCard(randomizer())

    fun getVideoBackgroundCard(): Int = getVideoBackgroundCard(randomizer())

    fun getGameBackgroundCard(id: Int): Int = when(id) {
        1 -> R.drawable.bg_entertainment_card_1
        else -> R.drawable.bg_entertainment_card_1
    }

    fun getQuizBackgroundCard(id: Int): Int = when(id) {
        2 -> R.drawable.bg_entertainment_card_2
        1 -> R.drawable.bg_entertainment_card_3
        else -> R.drawable.bg_entertainment_card_2
    }

    fun getEntertainmentBackgroundCard(): Int = getEntertainmentBackgroundCard(randomizer())

    fun getWithdrawalBg(id: Int): Int = when (id) {
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.WEBMONEY -> R.color.colorWebmoney
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.YANDEX -> R.color.colorYandex
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.QIWI -> R.color.colorQiwi
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.MTS -> R.color.colorMTC
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.TELE2 -> R.color.colorTele2
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.MEGAFON -> R.color.colorMegafon
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.BEELINE -> R.color.colorBeeline
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.PAYPAL -> R.color.colorPaypal
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.WOT -> R.color.colorWOT
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.WARFACE -> R.color.colorWarface
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.STEAM -> R.color.colorSteam
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.VK -> R.color.colorVK
        else -> R.color.colorGold
    }

    fun getWithdrawalLogo(id: Int): Int = when (id) {
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.WEBMONEY -> R.drawable.ic_webmoney
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.YANDEX -> R.drawable.ic_yandexmoney
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.QIWI -> R.drawable.ic_qiwi
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.MTS -> R.drawable.ic_mts_logo
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.TELE2 -> R.drawable.ic_tele2_logo
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.MEGAFON -> R.drawable.ic_megafon
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.BEELINE -> R.drawable.ic_beeline
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.PAYPAL -> R.drawable.ic_paypal
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.WOT -> R.drawable.ic_wot
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.WARFACE -> R.drawable.ic_warface
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.STEAM -> R.drawable.ic_steam
        net.makemoney.android.data.models.withdrawal.WithdrawalCardType.VK -> R.drawable.ic_vk
        else -> R.drawable.ic_star
    }

    private fun getPartnerBackgroundCard(number: Int): Int = when(number) {
        in 0..2 -> R.drawable.bg_partner_card_1
        in 3..5 -> R.drawable.bg_partner_card_2
        in 6..9 -> R.drawable.bg_partner_card_3
        in 10..12 -> R.drawable.bg_partner_card_4
        else -> throw IllegalArgumentException("wrong number for partner backgrounds cards")
    }

    private fun getVideoBackgroundCard(number: Int): Int = when(number) {
        in 0..1 -> R.drawable.bg_video_card_1
        in 2..3 -> R.drawable.bg_video_card_2
        in 4..5 -> R.drawable.bg_video_card_3
        in 6..7 -> R.drawable.bg_video_card_4
        in 8..9 -> R.drawable.bg_video_card_5
        in 10..12 -> R.drawable.bg_video_card_6
        else -> throw IllegalArgumentException("wrong number for video backgrounds cards")
    }

    private fun getEntertainmentBackgroundCard(id: Int): Int = when(id) {
        0 -> R.drawable.bg_entertainment_card_1
        else -> R.drawable.bg_entertainment_card_2
    }

    /**
     * Created to prevent background images of the same type from being repeated
     */
    private fun randomizer(prevNumber: Int = 0): Int {
        val random = Random()
        val result = random.nextInt(12)
        if (result == prevNumber) randomizer(prevNumber)
        return result
    }

}