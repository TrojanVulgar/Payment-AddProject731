package net.makemoney.android.screens.fragments

import android.app.Activity
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.TextView
import com.fyber.FairBid
import com.fyber.ads.AdFormat
import com.fyber.fairbid.ads.Banner
import com.fyber.fairbid.ads.banner.BannerOptions
import com.fyber.fairbid.ads.banner.SupportedCreativeSizes
import com.fyber.requesters.OfferWallRequester
import com.fyber.requesters.RequestCallback
import com.fyber.requesters.RequestError
import com.offertoro.sdk.sdk.OffersInit
import com.transitionseverywhere.TransitionManager
import kotlinx.android.synthetic.main.fragment_tasks.*
import net.makemoney.android.R
import net.makemoney.android.extensions.getColor
import net.makemoney.android.extensions.gone
import net.makemoney.android.extensions.visible
import net.makemoney.android.mvp.contracts.TasksContract
import net.makemoney.android.mvp.presenters.TasksPresenter
import net.makemoney.android.screens.MainActivity
import net.makemoney.android.screens.MainActivity.Companion.EX_MARATHON_ITEM
import net.makemoney.android.screens.TaskActivity
import net.makemoney.android.screens.TaskActivity.Companion.RC_TASK_ACTIVITY
import net.makemoney.android.skeleton.fragment.BaseFragment
import net.makemoney.android.utils.DURATION_FOR_TASKS_TOP_SLIDER
import net.makemoney.android.utils.TopAdapterClicksListener
import net.makemoney.android.utils.decoration.GridItemDecoration
import net.makemoney.android.utils.decoration.HorizontalItemDecoration
import net.makemoney.android.utils.decoration.HorizontalItemDecorationTopTasks
import net.makemoney.android.utils.helpers.HorizontalSnapHelper
import org.jetbrains.anko.toast
import timber.log.Timber
import kotlin.concurrent.thread

class TasksFragment : BaseFragment<MainActivity, TasksContract.Presenter>(), TopAdapterClicksListener, TasksContract.View {

    private var topAdapterPosition = net.makemoney.android.data.models.tasks.TopTasksType.TASKS

    private lateinit var marathonAdapter: net.makemoney.android.adapters.MarathonAdapter
    private var marathonItem: net.makemoney.android.data.models.marathon.MarathonItem? = null
    private var offerwallIntent: Intent? = null

    private var taskNewOrActive = net.makemoney.android.data.models.tasks.TaskType.NEW
    private val tasksAdapter = net.makemoney.android.adapters.TasksAdapter(arrayListOf()) {
        if (it is net.makemoney.android.data.models.tasks.OwnTaskItem) {
            val intent = Intent(activity, TaskActivity::class.java).apply {
                putExtra(TaskActivity.OPENED_TASK, it)
            }
            startActivityForResult(intent, RC_TASK_ACTIVITY)
        }
        if (it is net.makemoney.android.data.models.tasks.PartnerTaskItem) {
            when (it.id) {
                2 -> OffersInit.getInstance().showOfferWall(activity)
                else -> OfferWallRequester.create(requestCallback)
                        .request(context)
            }
        }
    }.apply {
        addLoadingFooter()
    }

    private var requestCallback: RequestCallback = object : RequestCallback {

        override fun onAdAvailable(intent: Intent) {
            // Store the intent that will be used later to show the Offer Wall
            offerwallIntent = intent
            startActivity(offerwallIntent)
            Log.d(TAG, "Offers are available")
        }

        override fun onAdNotAvailable(adFormat: AdFormat) {
            // Since we don't have an ad, it's best to reset the Offer Wall intent
            offerwallIntent = null
            Log.d(TAG, "No ad available")
        }

        override fun onRequestError(requestError: RequestError) {
            // Since we don't have an ad, it's best to reset the Offer Wall intent
            offerwallIntent = null
            Log.d(TAG, "Something went wrong with the request: " + requestError.description)
        }
    }

    override val TAG: String
        get() = TasksFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_tasks

    override fun createPresenter(): TasksPresenter = TasksPresenter(this)

    override fun initViews(rootView: View?) {
        initSwitchButtons()
        initTopRecycler()
        initTaskAdapter()
        marathonItem = arguments?.getParcelable(EX_MARATHON_ITEM)
        if (marathonItem != null) initMarathon(marathonItem!!)
        else presenter.getMarathon()
        presenter.getNewTasks()
        OffersInit.getInstance().create(activity)
    }

    override fun onPrimaryBtnClick(description: String) {
        activity.showInfo(description)
    }

    override fun marathonCompleted(balance: Float) {
        activity.toast(getString(R.string.marathon_done_message, balance - activity.balance))
        activity.balance = balance
        activity.updateBalance()
        marathonAdapter.updateAfterTaken()
    }

    override fun retrieveMarathon(marathonItem: net.makemoney.android.data.models.marathon.MarathonItem) {
        TransitionManager.beginDelayedTransition(transitionContainer)
        initMarathon(marathonItem)
    }

    override fun retrieveNewTasks(tasks: List<net.makemoney.android.data.models.tasks.OwnTaskItem>) {
        taskNewOrActive = net.makemoney.android.data.models.tasks.TaskType.NEW
        tasksAdapter.clearItems()
        tasksAdapter.addItems(tasks)
        if (rvTasks != null)
            rvTasks.requestLayout()
    }

    override fun retrieveActiveTasks(tasks: List<net.makemoney.android.data.models.tasks.OwnTaskItem>) {
        tasksAdapter.type = net.makemoney.android.data.models.tasks.TaskType.ACTIVE
        tasksAdapter.clearItems()
        tasksAdapter.addItems(tasks)
        if (rvTasks != null)
            rvTasks.requestLayout()
    }

    override fun retrievePartnerTasks(tasks: List<net.makemoney.android.data.models.tasks.PartnerTaskItem>) {
        tasksAdapter.type = net.makemoney.android.data.models.tasks.TaskType.PARTNER
        tasksAdapter.clearItems()
        tasksAdapter.addItems(tasks)
        if (rvTasks != null)
            rvTasks.requestLayout()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_TASK_ACTIVITY && resultCode == Activity.RESULT_OK)
            when (taskNewOrActive) {
                net.makemoney.android.data.models.tasks.TaskType.NEW -> getNewTasks()
                net.makemoney.android.data.models.tasks.TaskType.ACTIVE -> getActiveTasks()
            }
    }

    private fun initMarathon(marathonItem: net.makemoney.android.data.models.marathon.MarathonItem) {
        val snapHelper = HorizontalSnapHelper()
        val layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        rvMarathon.layoutManager = layoutManager
        rvMarathon.addItemDecoration(HorizontalItemDecoration())
        rvMarathon.setHasFixedSize(true)
        snapHelper.attachToRecyclerView(rvMarathon)
        marathonAdapter = net.makemoney.android.adapters.MarathonAdapter(marathonItem) {
            presenter.takeMarathonAward(marathonItem.id)
        }
        rvMarathon.adapter = marathonAdapter
    }

    private fun initTaskAdapter() {
        rvTasks.layoutManager = GridLayoutManager(activity, 2)
        rvTasks.itemAnimator = DefaultItemAnimator()
        rvTasks.setHasFixedSize(true)
        rvTasks.addItemDecoration(GridItemDecoration())
        rvTasks.adapter = tasksAdapter
    }

    private fun initTopRecycler() {
        val snapHelper = HorizontalSnapHelper()
        val layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        rvTopTasksCards.layoutManager = layoutManager
        rvTopTasksCards.addItemDecoration(HorizontalItemDecorationTopTasks())
        rvTopTasksCards.setHasFixedSize(true)
        snapHelper.attachToRecyclerView(rvTopTasksCards)
        rvTopTasksCards.adapter = net.makemoney.android.adapters.TasksTopAdapter(listOf(
                net.makemoney.android.data.models.tasks.TasksTopCardItem(getString(R.string.tasks_title), getString(R.string.tasks_description), getString(R.string.tasks_additional_info), R.drawable.bg_tasks_card),
                net.makemoney.android.data.models.tasks.TasksTopCardItem(getString(R.string.tasks_partners_title), getString(R.string.tasks_partners_description), getString(R.string.tasks_partners_additional_info), R.drawable.bg_partners_card)), this)
        rvTopTasksCards.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (topAdapterPosition != layoutManager.findFirstCompletelyVisibleItemPosition() && topAdapterPosition != -1)
                        try {
                            when (layoutManager.findFirstCompletelyVisibleItemPosition()) {
                                net.makemoney.android.data.models.tasks.TopTasksType.TASKS -> {
                                    TransitionManager.beginDelayedTransition(transitionContainer)
                                    containerMarathon.visible()
                                    containerNewOrActiveTasks.visible()
                                    topAdapterPosition = net.makemoney.android.data.models.tasks.TopTasksType.TASKS
                                    thread(start = true) {
                                        Thread.sleep(DURATION_FOR_TASKS_TOP_SLIDER)
                                        when (taskNewOrActive) {
                                            net.makemoney.android.data.models.tasks.TaskType.NEW -> getNewTasks()
                                            net.makemoney.android.data.models.tasks.TaskType.ACTIVE -> getActiveTasks()
                                        }
                                    }
                                }
                                net.makemoney.android.data.models.tasks.TopTasksType.PARTNERS -> {
                                    TransitionManager.beginDelayedTransition(transitionContainer)
                                    containerMarathon.gone()
                                    containerNewOrActiveTasks.gone()
                                    topAdapterPosition = net.makemoney.android.data.models.tasks.TopTasksType.PARTNERS
                                    thread(start = true) {
                                        Thread.sleep(DURATION_FOR_TASKS_TOP_SLIDER)
                                        getPartnerTasks()
                                    }
                                }
                            }
                        } catch (e: NullPointerException) {
                            Timber.e(e.message)
                        }
                }
            }
        })
    }

    private fun getNewTasks() {
        activity.runOnUiThread {
            tasksAdapter.clearItems()
            tasksAdapter.addLoadingFooter()
            taskNewOrActive = net.makemoney.android.data.models.tasks.TaskType.NEW
            tasksAdapter.type = taskNewOrActive
            presenter.getNewTasks()
        }
    }

    private fun getActiveTasks() {
        activity.runOnUiThread {
            tasksAdapter.clearItems()
            tasksAdapter.addLoadingFooter()
            taskNewOrActive = net.makemoney.android.data.models.tasks.TaskType.ACTIVE
            tasksAdapter.type = taskNewOrActive
            presenter.getActiveTasks()
        }
    }

    private fun getPartnerTasks() {
        activity.runOnUiThread {
            tasksAdapter.clearItems()
            tasksAdapter.addLoadingFooter()
            tasksAdapter.type = net.makemoney.android.data.models.tasks.TaskType.PARTNER
            presenter.getPartnerTasks()
        }
    }

    private fun initSwitchButtons() {
        newTasks.setOnClickListener {
            with (it as TextView) {
                backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorGold)
                setTextColor(getColor(R.color.colorSelectedButton))
            }
            with (activeTasks) {
                backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorUnselectedUnderline)
                setTextColor(getColor(R.color.colorUnselectedButton))
            }
            getNewTasks()
        }
        activeTasks.setOnClickListener {
            with (it as TextView) {
                backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorGold)
                setTextColor(getColor(R.color.colorSelectedButton))
            }
            with (newTasks) {
                backgroundTintList = ContextCompat.getColorStateList(activity, R.color.colorUnselectedUnderline)
                setTextColor(getColor(R.color.colorUnselectedButton))
            }
            getActiveTasks()
        }
    }
}