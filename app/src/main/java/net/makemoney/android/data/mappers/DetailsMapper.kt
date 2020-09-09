package net.makemoney.android.data.mappers

import net.makemoney.android.R
import net.makemoney.android.data.models.details.DetailsItem
import net.makemoney.android.data.models.details.DetailsSimpleItem
import net.makemoney.android.data.models.details.ProgressItem
import net.makemoney.android.data.models.referral.DetailsType
import net.makemoney.android.data.responses.BalanceDetailsResponse
import net.makemoney.android.data.responses.ReferralResponse
import net.makemoney.android.extensions.getString
import net.makemoney.android.extensions.toBalance


class DetailsMapper {

    fun transformToReferral(details: net.makemoney.android.data.responses.ReferralResponse): List<net.makemoney.android.data.models.details.DetailsItem> {
        val tempList = arrayListOf<net.makemoney.android.data.models.details.DetailsItem>()
        details.apply {
            tempList.add(net.makemoney.android.data.models.details.ProgressItem(getString(R.string.referral_details_referrals), referrals.level, referrals.currentProgress, referrals.maxProgress, referrals.percent, R.drawable.ic_referrals, R.color.colorReferralIcon, R.drawable.shape_referral_level))
            tempList.add(net.makemoney.android.data.models.details.DetailsSimpleItem(getString(R.string.referral_details_referral_balance), balance.toBalance(), net.makemoney.android.data.models.referral.DetailsType.CURRENCY, R.drawable.ic_purse, R.color.colorReferralIcon, R.drawable.shape_referral_icon))
            tempList.add(net.makemoney.android.data.models.details.DetailsSimpleItem(getString(R.string.referral_details_reward), award.toString().plus("%"), net.makemoney.android.data.models.referral.DetailsType.OTHER, R.drawable.ic_award, R.color.colorReferralIcon, R.drawable.shape_referral_icon))
            tempList.add(net.makemoney.android.data.models.details.DetailsSimpleItem(getString(R.string.referral_details_paid), paid.toBalance(), net.makemoney.android.data.models.referral.DetailsType.CURRENCY, R.drawable.ic_paid, R.color.colorReferralIcon, R.drawable.shape_referral_icon))
        }
        return tempList
    }

    fun transformToBalance(details: net.makemoney.android.data.responses.BalanceDetailsResponse): List<net.makemoney.android.data.models.details.DetailsItem> {
        val tempList = arrayListOf<net.makemoney.android.data.models.details.DetailsItem>()
        details.apply {
            tempList.add(net.makemoney.android.data.models.details.DetailsSimpleItem(getString(R.string.balance_title), balance.toBalance(), net.makemoney.android.data.models.referral.DetailsType.CURRENCY, R.drawable.ic_purse, R.color.colorBalanceIcon, R.drawable.shape_balance_icon))
            tempList.add(net.makemoney.android.data.models.details.DetailsSimpleItem(getString(R.string.balance_details_paid), paid.toBalance(), net.makemoney.android.data.models.referral.DetailsType.CURRENCY, R.drawable.ic_paid, R.color.colorBalanceIcon, R.drawable.shape_balance_icon))
        }
        return tempList
    }
}