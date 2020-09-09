package net.makemoney.android.screens.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.util.TypedValue
import android.view.View
import com.fyber.ads.videos.RewardedVideoActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import kotlinx.android.synthetic.main.fragment_videos.*
import kotlinx.android.synthetic.main.item_tasks_top_card.*
import net.makemoney.android.R
import net.makemoney.android.adapters.TasksAdapter
import net.makemoney.android.adds.CustomRewardedVideoListener
import net.makemoney.android.data.models.tasks.TaskType
import net.makemoney.android.data.models.tasks.VideoTaskItem
import net.makemoney.android.extensions.appContext
import net.makemoney.android.mvp.contracts.VideoContract
import net.makemoney.android.mvp.presenters.VideoPresenter
import net.makemoney.android.screens.MainActivity
import net.makemoney.android.skeleton.fragment.BaseFragment
import net.makemoney.android.utils.VIDEO_ADMOB
import net.makemoney.android.utils.VIDEO_FYBER
import net.makemoney.android.utils.decoration.GridItemDecoration
import org.jetbrains.anko.toast
import timber.log.Timber


class VideoFragment : BaseFragment<MainActivity, VideoPresenter>(), net.makemoney.android.adds.CustomRewardedVideoListener, VideoContract.View {

    lateinit var rewardedVideoAd: RewardedVideoAd
    var fyberRewardedIntent: Intent? = null

    private val videoAdapter = net.makemoney.android.adapters.TasksAdapter(arrayListOf()) {
        if (it is net.makemoney.android.data.models.tasks.VideoTaskItem && it.isAvailable)
            presenter.showRewardedVideo(it.id)
        else
            activity.toast(getString(R.string.adds_video_unavailable))
    }.apply {
        addLoadingFooter()
        type = net.makemoney.android.data.models.tasks.TaskType.VIDEO
    }

    override val TAG: String
        get() = VideoFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_videos

    override fun createPresenter(): VideoPresenter = VideoPresenter(this, activity, this)

    override fun initViews(rootView: View?) {
        initVideoAdapter()
        initPartners()
        presenter.getVideos()
        presenter.initAdColony()
        ivBackgroundImage.background = ContextCompat.getDrawable(appContext, R.drawable.bg_videos)
        guideline_description_top.setGuidelinePercent(0.65f)
        setDescription(0)
        tvTasksTitle.text = getString(R.string.video_card_title)
        tvTasksTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
        guideline_title.setGuidelinePercent(0.31f)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == RC_FYBER_REWARDED_VIDEO) {
            val engagementResult = data?.getStringExtra(RewardedVideoActivity.ENGAGEMENT_STATUS)
            if (engagementResult != null) {
                when (engagementResult) {
                    RewardedVideoActivity.REQUEST_STATUS_PARAMETER_FINISHED_VALUE -> {
                        presenter.getReward(VIDEO_FYBER)
                    }
                    RewardedVideoActivity.REQUEST_STATUS_PARAMETER_ABORTED_VALUE -> activity.toast(getString(R.string.adds_look_full_video))
                    RewardedVideoActivity.REQUEST_STATUS_PARAMETER_ERROR -> activity.toast(getString(R.string.adds_something_went_wrong))
                }
            }
            presenter.uploadFyberRewardedVideo()
        }
    }

    override fun videoRewarded() {
        activity.toast(getString(R.string.adds_reward_successful_received))
        videoAdapter.clearItems()
        videoAdapter.addLoadingFooter()
        presenter.getVideos()
        activity.updateBalance()
    }

    override fun retrieveVideos(items: List<net.makemoney.android.data.models.tasks.VideoTaskItem>, todayLimit: Int) {
        videoAdapter.addItems(items)
        setDescription(todayLimit)
    }

    override fun onRewardedVideoAdClosed() {
        loadRewardedVideo()
    }

    override fun onRewarded(rewardedItem: RewardItem?) {
        presenter.getReward(VIDEO_ADMOB)
    }

    override fun onRewardedVideoAdFailedToLoad(p0: Int) {
        activity.toast(getString(R.string.adds_content_empty))
    }

    private fun initPartners() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(appContext)
        rewardedVideoAd.rewardedVideoAdListener = this
        loadRewardedVideo()
        presenter.uploadFyberRewardedVideo()
    }

    fun loadRewardedVideo() {
        rewardedVideoAd.loadAd(getString(R.string.google_rewarded_video_id),
                AdRequest.Builder().build())
    }

    private fun initVideoAdapter() {
        rvVideos.layoutManager = GridLayoutManager(activity, 2)
        rvVideos.itemAnimator = DefaultItemAnimator()
        rvVideos.setHasFixedSize(true)
        rvVideos.addItemDecoration(GridItemDecoration())
        rvVideos.adapter = videoAdapter
    }

    private fun setDescription(count: Int) {
        try {
            val description = SpannableStringBuilder(getString(R.string.video_card_description, count))
            description.setSpan(RelativeSizeSpan(1.4f), 47, description.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            tvTasksDescription.text = description
        } catch (e: NullPointerException) {
            Timber.e(e.message)
        }
    }

    companion object {
        const val RC_FYBER_REWARDED_VIDEO = 23154
    }
}