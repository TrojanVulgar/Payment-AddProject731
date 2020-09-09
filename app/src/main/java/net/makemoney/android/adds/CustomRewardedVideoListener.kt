package net.makemoney.android.adds

import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import timber.log.Timber



interface CustomRewardedVideoListener : RewardedVideoAdListener {
    override fun onRewardedVideoAdClosed() {
        Timber.i("AdMob: closed")
    }

    override fun onRewardedVideoAdLeftApplication() {
        Timber.i("AdMob: left")
    }

    override fun onRewardedVideoAdLoaded() {
        Timber.i("AdMob: loaded")
    }

    override fun onRewardedVideoAdOpened() {
        Timber.i("AdMob: opened")
    }

    override fun onRewardedVideoCompleted() {
        Timber.i("AdMob: completed")
    }

    override fun onRewarded(rewardedItem: RewardItem?) {
        Timber.i("AdMob: rewarded: ${rewardedItem?.amount}")
    }

    override fun onRewardedVideoStarted() {
        Timber.i("AdMob: started")
    }

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
        Timber.i("AdMob: FaildToLoad")
    }
}