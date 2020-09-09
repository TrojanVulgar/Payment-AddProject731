package net.makemoney.android.mvp.presenters

import android.content.Intent
import com.adcolony.sdk.*
import com.fyber.ads.AdFormat
import com.fyber.requesters.RequestCallback
import com.fyber.requesters.RequestError
import com.fyber.requesters.RewardedVideoRequester
import net.makemoney.android.R
import net.makemoney.android.api.DefaultCallback
import net.makemoney.android.api.RestClient
import net.makemoney.android.data.mappers.TaskMapper
import net.makemoney.android.data.responses.EmptyResponse
import net.makemoney.android.data.responses.VideoResponse
import net.makemoney.android.extensions.appContext
import net.makemoney.android.extensions.getString
import net.makemoney.android.mvp.contracts.VideoContract
import net.makemoney.android.screens.fragments.VideoFragment
import net.makemoney.android.skeleton.activity.BaseActivity
import org.jetbrains.anko.toast
import timber.log.Timber


class VideoPresenter(override val view: VideoContract.View,
                     private val activity: BaseActivity<*>,
                     private val fragment: VideoFragment) : VideoContract.Presenter {

    private val mapper = net.makemoney.android.data.mappers.TaskMapper()

    private lateinit var adColonyTest: AdColonyInterstitial
    private lateinit var listener: AdColonyInterstitialListener
    private val adColonyRewardedListener: AdColonyRewardListener by lazy {
        AdColonyRewardListener {
            getReward(4)
        }
    }

    override fun getVideos() = net.makemoney.android.api.RestClient.api.getVideos().enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.VideoResponse>(view) {
            override fun onResponse(body: net.makemoney.android.data.responses.VideoResponse) {
                view.retrieveVideos(mapper.transformVideos(body.videos), body.video_limit.limit - body.video_limit.today_viewed)
            }
        })

    override fun getReward(id: Int) = net.makemoney.android.api.RestClient.api.getVideoReward(id).enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.EmptyResponse>(view) {
            override fun onResponse(body: net.makemoney.android.data.responses.EmptyResponse) {
                view.videoRewarded()
            }
        })

    private fun showAdMobRewardedVideo() {
        if (fragment.rewardedVideoAd.isLoaded) fragment.rewardedVideoAd.show()
        else activity.toast(getString(R.string.adds_content_empty))
        fragment.loadRewardedVideo()
    }

    private fun showFyberRewardedVideo() {
        if (fragment.fyberRewardedIntent != null) fragment.startActivityForResult(fragment.fyberRewardedIntent, VideoFragment.RC_FYBER_REWARDED_VIDEO)
        else {
            activity.toast(activity.getString(R.string.adds_content_empty))
            uploadFyberRewardedVideo()
        }
    }

    fun showRewardedVideo(id: Int) {
        when (id) {
            1 -> showAdMobRewardedVideo()
            3 -> showFyberRewardedVideo()
            2 -> showAdColonyVideo()
            else -> activity.toast("in Development")
        }
    }

    fun uploadFyberRewardedVideo() {
        val requestCallback = object : RequestCallback {
            override fun onAdAvailable(intent: Intent?) {
                fragment.fyberRewardedIntent = intent
            }

            override fun onAdNotAvailable(format: AdFormat?) {
                activity.toast(activity.getString(R.string.adds_content_empty))
                fragment.fyberRewardedIntent = null
            }

            override fun onRequestError(p0: RequestError?) {
                activity.toast(activity.getString(R.string.adds_error))
                Timber.i("onRequestError: ${p0?.description}")
                fragment.fyberRewardedIntent = null
            }
        }
        RewardedVideoRequester.create(requestCallback)
                .request(appContext)
    }

    fun initAdColony() {
        listener = object : AdColonyInterstitialListener() {
            override fun onRequestNotFilled(zone: AdColonyZone?) {
                super.onRequestNotFilled(zone)
                activity.toast(getString(R.string.adds_content_empty))
            }

            override fun onExpiring(ad: AdColonyInterstitial?) {
                super.onExpiring(ad)
                AdColony.requestInterstitial(getString(R.string.adColony_zone_id), this)
            }

            override fun onRequestFilled(adColony: AdColonyInterstitial?) {
                if (adColony != null) {
                    adColonyTest = adColony
                    adColonyTest.show()
                }
            }
        }
        AdColony.setRewardListener(adColonyRewardedListener)
    }

    private fun showAdColonyVideo() {
        AdColony.requestInterstitial(getString(R.string.adColony_zone_id), listener)
    }

}