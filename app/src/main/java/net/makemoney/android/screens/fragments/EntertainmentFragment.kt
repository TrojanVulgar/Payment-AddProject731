package net.makemoney.android.screens.fragments

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardedVideoAd
import kotlinx.android.synthetic.main.dialog_simple_info.view.*
import kotlinx.android.synthetic.main.fragment_entertainment.*
import net.makemoney.android.R
import net.makemoney.android.adapters.TasksAdapter
import net.makemoney.android.adapters.TasksTopAdapter
import net.makemoney.android.data.models.entertainment.GameTaskItem
import net.makemoney.android.data.models.entertainment.QuizTaskItem
import net.makemoney.android.data.models.entertainment.TopEntertainmentType
import net.makemoney.android.data.models.tasks.TaskType
import net.makemoney.android.data.models.tasks.TasksTopCardItem
import net.makemoney.android.extensions.appContext
import net.makemoney.android.extensions.inflate
import net.makemoney.android.game.GameSwipeActivity
import net.makemoney.android.mvp.contracts.EntertainmentContract
import net.makemoney.android.mvp.presenters.EntertainmentPresenter
import net.makemoney.android.screens.MainActivity
import net.makemoney.android.screens.QuizActivity
import net.makemoney.android.screens.QuizActivity.Companion.EXTRA_QUIZ_ITEM
import net.makemoney.android.screens.QuizActivity.Companion.RC_QUIZ_ACTIVITY
import net.makemoney.android.skeleton.fragment.BaseFragment
import net.makemoney.android.utils.InstantDialog
import net.makemoney.android.utils.TopAdapterClicksListener
import net.makemoney.android.utils.decoration.GridItemDecoration
import net.makemoney.android.utils.decoration.HorizontalItemDecorationTopTasks
import net.makemoney.android.utils.helpers.HorizontalSnapHelper
import org.jetbrains.anko.toast
import timber.log.Timber


class EntertainmentFragment : BaseFragment<MainActivity, EntertainmentPresenter>(), TopAdapterClicksListener, EntertainmentContract.View {

    private var topAdapterPosition = net.makemoney.android.data.models.entertainment.TopEntertainmentType.QUIZZES

    private lateinit var adVideo: RewardedVideoAd

    private val entertainmentAdapter = net.makemoney.android.adapters.TasksAdapter(arrayListOf()) {
        when (it) {
            is net.makemoney.android.data.models.entertainment.GameTaskItem -> {
                if (it.isUnpaidMode)
                //TODO: open gameActivity for result and override onActivityResult in fragment to get time in game
                    Intent(activity, net.makemoney.android.game.GameSwipeActivity::class.java).apply {
                        startActivityForResult(this, RC_GAME_SWIPE)
                    }
                else
                    showUnpaidAlert(it)
            }
            is net.makemoney.android.data.models.entertainment.QuizTaskItem -> {
                if (it.isAvailable) {
                    loadAdVideo()
                    Intent(activity, QuizActivity::class.java).apply {
                        putExtra(EXTRA_QUIZ_ITEM, it)
                        startActivityForResult(this, RC_QUIZ_ACTIVITY)
                    }
                } else
                    activity.toast(getString(R.string.quiz_unavailable))
            }
        }
        if (it !is net.makemoney.android.data.models.entertainment.GameTaskItem) return@TasksAdapter
    }.apply {
        addLoadingFooter()
        type = net.makemoney.android.data.models.tasks.TaskType.QUIZZES
    }

    override val TAG: String
        get() = EntertainmentFragment::class.java.simpleName

    override fun getLayoutId(): Int = net.makemoney.android.R.layout.fragment_entertainment

    override fun createPresenter(): EntertainmentPresenter = EntertainmentPresenter(this)

    override fun initViews(rootView: View?) {
        initGamesAdapter()
        initTopRecycler()

        adVideo = MobileAds.getRewardedVideoAdInstance(appContext)

        presenter.getQuizzes()

        rvEntertainment.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                toolbarEntertainmentRecycler.isSelected = rvEntertainment.canScrollVertically(-1)
            }
        })
    }

    override fun onPrimaryBtnClick(description: String) {

    }

    override fun retrieveQuizzes(items: List<net.makemoney.android.data.models.entertainment.QuizTaskItem>) {
        entertainmentAdapter.type = net.makemoney.android.data.models.tasks.TaskType.QUIZZES
        entertainmentAdapter.addItems(items)
    }

    override fun retrieveGames(items: List<net.makemoney.android.data.models.entertainment.GameTaskItem>) {
        entertainmentAdapter.type = net.makemoney.android.data.models.tasks.TaskType.GAMES
        entertainmentAdapter.addItems(items)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.i("Oh, hello there. Game score?\n$requestCode, $resultCode, ${data != null}")
        if (requestCode == RC_QUIZ_ACTIVITY && resultCode == Activity.RESULT_OK) {
            if (adVideo.isLoaded) {
                adVideo.show()
            }
        }
        if (requestCode == RC_GAME_SWIPE && resultCode == Activity.RESULT_OK && data != null) {
            val score = data.getFloatExtra("game_score", 0f)
            Timber.i("Game score: $score")
            presenter.updateGame(1, score)
        }
    }

    override fun onResume() {
        super.onResume()
        when (entertainmentAdapter.type) {
            net.makemoney.android.data.models.tasks.TaskType.QUIZZES -> getQuizzes()
            net.makemoney.android.data.models.tasks.TaskType.GAMES -> getGames()
        }
    }

    override fun gameUpdated(balance: Float) {
        activity.balance = balance
        activity.updateBalance()
        if (entertainmentAdapter.type == net.makemoney.android.data.models.tasks.TaskType.GAMES)
            presenter.getGames()
    }

    private fun initTopRecycler() {
        val snapHelper = HorizontalSnapHelper()
        val layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        rvTopEntertainmentCards.layoutManager = layoutManager
        rvTopEntertainmentCards.addItemDecoration(HorizontalItemDecorationTopTasks())
        rvTopEntertainmentCards.setHasFixedSize(true)
        snapHelper.attachToRecyclerView(rvTopEntertainmentCards)
        rvTopEntertainmentCards.adapter = net.makemoney.android.adapters.TasksTopAdapter(listOf(
                net.makemoney.android.data.models.tasks.TasksTopCardItem(getString(R.string.entertainment_quiz_title), getString(net.makemoney.android.R.string.entertainment_quiz_additional_info), getString(net.makemoney.android.R.string.tasks_additional_info), R.drawable.bg_quizzes_card),
                net.makemoney.android.data.models.tasks.TasksTopCardItem(getString(R.string.entertainment_games_title), getString(net.makemoney.android.R.string.entertainment_games_additional_info), getString(net.makemoney.android.R.string.tasks_partners_additional_info), R.drawable.bg_games_card)), this).apply { type = net.makemoney.android.data.models.tasks.TaskType.QUIZZES }
        rvTopEntertainmentCards.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (topAdapterPosition != layoutManager.findFirstCompletelyVisibleItemPosition() && topAdapterPosition != -1)
                        try {
                            when (layoutManager.findFirstCompletelyVisibleItemPosition()) {
                                net.makemoney.android.data.models.entertainment.TopEntertainmentType.QUIZZES -> {
                                    topAdapterPosition = net.makemoney.android.data.models.entertainment.TopEntertainmentType.QUIZZES
                                    entertainmentAdapter.type = net.makemoney.android.data.models.tasks.TaskType.QUIZZES
                                    getQuizzes()
                                    //TODO: 14.02.2019 get quizzes from server
                                }
                                net.makemoney.android.data.models.entertainment.TopEntertainmentType.GAMES -> {
                                    topAdapterPosition = net.makemoney.android.data.models.entertainment.TopEntertainmentType.GAMES
                                    entertainmentAdapter.type = net.makemoney.android.data.models.tasks.TaskType.GAMES
                                    getGames()
                                    // TODO: 14.02.2019 get games from server
                                }
                            }
                        } catch (e: NullPointerException) {
                            Timber.e(e.message)
                        }
                }
            }
        })
    }

    private fun initGamesAdapter() {
        rvEntertainment.layoutManager = GridLayoutManager(activity, 2)
        rvEntertainment.itemAnimator = DefaultItemAnimator()
        rvEntertainment.setHasFixedSize(true)
        rvEntertainment.addItemDecoration(GridItemDecoration())
        rvEntertainment.adapter = entertainmentAdapter
    }

    private fun showUnpaidAlert(gameTaskItem: net.makemoney.android.data.models.entertainment.GameTaskItem) {
        val dialogView = activity.inflate(net.makemoney.android.R.layout.dialog_simple_info)
        val dialog = InstantDialog(activity, dialogView).show()
        dialog?.setCancelable(true)
        dialogView.apply {
            tv_info_simple_dialog_text.text = getString(net.makemoney.android.R.string.games_limit_over)
            btn_info_simple_dialog_ok.text = getString(android.R.string.ok)
            btn_info_simple_dialog_ok.setOnClickListener {
                dialog?.dismiss()
            }
        }
    }

    private fun loadAdVideo() {
        adVideo.rewardedVideoAdListener = null
        adVideo.loadAd(getString(R.string.google_ad_video_id),
                AdRequest.Builder().build())
    }

    private fun getQuizzes() {
        entertainmentAdapter.clearItems()
        entertainmentAdapter.addLoadingFooter()
        presenter.getQuizzes()
    }

    private fun getGames() {
        entertainmentAdapter.clearItems()
        entertainmentAdapter.addLoadingFooter()
        presenter.getGames()
    }

    companion object {
        const val EXTRA_GAME_ITEM = "game_item"
        const val RC_GAME_SWIPE = 8927
    }
}