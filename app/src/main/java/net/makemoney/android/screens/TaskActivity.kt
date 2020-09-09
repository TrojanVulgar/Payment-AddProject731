package net.makemoney.android.screens

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.dialog_simple_info.view.*
import kotlinx.android.synthetic.main.dialog_task_search_accept.view.*
import net.makemoney.android.R
import net.makemoney.android.data.models.tasks.OwnTaskItem
import net.makemoney.android.data.models.tasks.OwnTaskType
import net.makemoney.android.data.models.tasks.TaskRateType
import net.makemoney.android.extensions.*
import net.makemoney.android.mvp.contracts.TaskContract
import net.makemoney.android.mvp.presenters.TaskPresenter
import net.makemoney.android.skeleton.activity.BaseActivity
import net.makemoney.android.utils.InstantDialog
import okhttp3.MediaType
import okhttp3.RequestBody
import org.jetbrains.anko.toast
import org.joda.time.Period
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class TaskActivity : BaseActivity<TaskContract.Presenter>(), TaskContract.View {

    private lateinit var openedTask: net.makemoney.android.data.models.tasks.OwnTaskItem
    private lateinit var runnable: Runnable
    private var handler: Handler? = null
    private var dialog: AlertDialog? = null
    private var isTaskTimerDone = false
    private var isTaskInstalledFromOurApp: Boolean? = null
    private var changeToolbarColorWithAnimation = ValueAnimator()

    override fun getLayoutId(): Int = R.layout.activity_task

    override fun createPresenter(): TaskPresenter = TaskPresenter(this)

    override fun initViews() {
        openedTask = intent.getParcelableExtra(OPENED_TASK)
        toolbar_task.title = openedTask.title
        tvCardTitle.text = openedTask.title
        initTaskView()
        initInfo()
        setUpAppBarBehavior()
    }

    override fun onResume() {
        super.onResume()
        isTaskInstalledFromOurApp?.let {
            if (!it && isAppInstalled()) {
                isTaskInstalledFromOurApp = true
                presenter.taskInstalled(openedTask.id)
            }
        }
        initTaskView()
        handler?.removeCallbacks(runnable)
        if (isTaskTimerDone) {
            isTaskTimerDone = false
            val intent = Intent()
            intent.putExtra(OPENED_TASK_ID, openedTask.id)
            setResult(Activity.RESULT_OK, intent)
            presenter.taskCompleted(openedTask.id)
            toast(getString(R.string.task_was_completed_show_duration, openedTask.durationInApp))
            showProgressView()
        } else if (handler != null) {
            toast(getString(R.string.own_task_was_failed))
        }
        handler = null
    }

    override fun taskUpdated(item: net.makemoney.android.data.models.tasks.OwnTaskItem) {
        openedTask = item
        initTaskView()
        hideProgressView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GET_SCREEN && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val uri: Uri? = data.data
            uri?.let { fileUri ->
                uploadUri(fileUri)
            }
        }
    }

    override fun screenshotWasSend() {
        toast(R.string.own_task_screenshot_sending_complete)
        setCardDisabledView()
        hideProgressView()
    }

    private fun initTaskView() {
        if (!openedTask.isAvailable) {
            setResult(Activity.RESULT_OK)
        } else {
            setResult(Activity.RESULT_CANCELED)
        }
        setHomeButton()
        setDataToCard()
        initStepViews()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    private fun showAcceptDialog(isRating: Boolean) {
        val dialogView = inflate(R.layout.dialog_task_search_accept)
        dialog = InstantDialog(this, dialogView).show()
        dialog?.setCancelable(true)

        if (isRating) {
            dialogView.tv_task_search_accept_dialog_info.text = getString(R.string.own_task_rating_accept)
        } else {
            dialogView.tv_task_search_accept_dialog_info.text = getString(R.string.own_task_search_accept, getTaskKeywords(openedTask.keywords))
        }
        dialogView.btn_task_search_accept_dialog_ok.setOnClickListener {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
            dialog?.dismiss()
        }
    }

    private fun setHomeButton() {
        setSupportActionBar(toolbar_task)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_task.setNavigationIcon(R.drawable.ic_toolbar_back)
        toolbar_task.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setDataToCard() {
        tvCardAward.text = openedTask.dailyAward.asPrice()
        ivCardLogo.load(openedTask.imageUrl)
        tvAwardAtTheMoment.text = getString(R.string.task_award_at_the_moment, openedTask.dailyAward * openedTask.daysPassed, openedTask.award)
        val collapsingParams = collapsing_toolbar_task.layoutParams as AppBarLayout.LayoutParams
        Timber.i("SCROLL: ${collapsingParams.scrollFlags}")
        collapsingParams.scrollFlags = 3
        collapsing_toolbar_task.layoutParams = collapsingParams
        when {
            !isAppInstalled() -> {
                isTaskInstalledFromOurApp = false
                setCardViewDirectSearch()
            }
            openedTask.isAvailable -> setCardViewLaunch()
            (!openedTask.isAvailable && openedTask.isRatingAvailable == 1) -> setCardViewRating()
            else -> setCardDisabledView()
        }
    }

    private fun setCardViewDirectSearch() {
        when (openedTask.type) {
            net.makemoney.android.data.models.tasks.OwnTaskType.DIRECT -> {
                tvCardDescription.text = getString(R.string.own_task_direct_description)
                tvCardTaskAction.text = getString(R.string.own_task_direct)
                tvCardTaskAction.setOnClickListener {
                    val dialogView = inflate(R.layout.dialog_simple_info)
                    dialog = InstantDialog(this, dialogView).show()
                    dialog?.setCancelable(true)

                    dialogView.tv_simple_dialog_title.text = getString(R.string.own_task_preinstall_dialog_title)
                    dialogView.tv_info_simple_dialog_text.text = getString(R.string.own_task_preinstall_dialog_description)
                    dialogView.btn_info_simple_dialog_ok.setOnClickListener { _ ->
                        if (!openedTask.trackingLink.isEmpty()) {
                            try {
                                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(openedTask.trackingLink)))
                            } catch (e: android.content.ActivityNotFoundException) {
                                Timber.e(e.message)
                                openTaskInPlayMarket(openedTask.packageName)
                            }
                        } else {
                            openTaskInPlayMarket(openedTask.packageName)
                        }
                        dialog?.dismiss()
                    }
                }
            }
            net.makemoney.android.data.models.tasks.OwnTaskType.SEARCH -> {
                tvCardDescription.text = getString(R.string.own_task_search_description, getTaskKeywords(openedTask.keywords))
                tvCardTaskAction.text = getString(R.string.own_task_found)
                tvCardTaskAction.setOnClickListener {
                    showAcceptDialog(false)
                }
            }
        }
        tvCardLaunchTime.gone()
        containerAdditionalTasks.gone()
        val collapsingParams = collapsing_toolbar_task.layoutParams as AppBarLayout.LayoutParams
        collapsingParams.scrollFlags = 0
        collapsing_toolbar_task.layoutParams = collapsingParams
    }

    private fun setCardViewRating() {
        Timber.i("RateType: ${openedTask.rateType}")
        tvCardDescription.text = if (openedTask.rateType == net.makemoney.android.data.models.tasks.TaskRateType.RATE_WITH_FEED) getString(R.string.own_task_conditions_dialog_title_rating_and_feedback) else getString(R.string.own_task_conditions_dialog_title_only_rating)
        tvCardTaskAction.text = getString(R.string.own_task_send_to_moderation)
        tvCardTaskAction.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Select picture"), RC_GET_SCREEN)
        }
        tvCardLaunchTime.gone()
        containerAdditionalTasks.visible()
    }

    private fun setCardViewLaunch(isDisabled: Boolean = false) {
        tvCardDescription.text = getString(R.string.own_task_time_in_app, openedTask.durationInApp)
        containerAdditionalTasks.visible()
        if (isDisabled && openedTask.launchDate.isNotEmpty()) {
            tvCardTaskAction.text = getString(R.string.task_disabled_title)
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val launchDate = dateFormat.parse(openedTask.launchDate)
            val period = Period(Date().time, launchDate.time)
            val days = period.days
            var hours = period.hours
            val minutes = period.minutes
            if (days > 0) hours += 24*days
            tvCardLaunchTime.text = getString(R.string.task_launch_will_be_active_at, hours, minutes)
            tvCardLaunchTime.visible()
            tvCardTaskAction.isClickable = false
        } else {
            tvCardLaunchTime.gone()
            tvCardTaskAction.text = getString(R.string.own_task_launch)
            tvCardTaskAction.setOnClickListener {
                if (openedTask.description.isEmpty()) {
                    openApp(openedTask.packageName)
                } else {
                    val dialogView = inflate(R.layout.dialog_simple_info)
                    dialog = InstantDialog(this, dialogView).show()
                    dialog?.setCancelable(true)

                    dialogView.tv_simple_dialog_title.text = getString(R.string.own_task_prelaunch_description_title)
                    dialogView.tv_info_simple_dialog_text.text = openedTask.description
                    dialogView.btn_info_simple_dialog_ok.setOnClickListener { _ ->
                        openApp(openedTask.packageName)
                        dialog?.dismiss()
                    }
                }
            }
        }
    }

    private fun setCardDisabledView() {
        setCardViewLaunch(isDisabled = true)
    }

    private fun uploadUri(fileUri: Uri) {
        val realPath = getPath(fileUri)
        if (realPath.isNullOrEmpty()) {
            toast(getString(R.string.task_cant_get_screen))
            return
        }
        val imageFile = File(realPath)
        showProgressView()

        val oneMByte = 1048576
        try {
            if (imageFile.length() >= oneMByte) {
                val bitmap = BitmapFactory.decodeFile(imageFile.path)
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 2, FileOutputStream(imageFile))
                } catch (e: FileNotFoundException) {
                    hideProgressView()
                    Timber.e("Image compressing: ${e.message}")
                }
            }
        } catch (e: SecurityException) {
            hideProgressView()
            Timber.e("Image compressing: ${e.message}")
        }
        toast(getString(R.string.own_task_screenshot_sending_to_server))
        val screenPart = RequestBody.create(MediaType.parse("image/*"), imageFile)
        presenter.sendScreenshot(openedTask.id, screenPart)
    }

    private fun getPath(contentUri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val array = arrayOf(MediaStore.Images.Media.DATA)
            cursor = this.contentResolver.query(contentUri, array, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)
        } catch (e: IllegalStateException) {
            null
        } finally {
            cursor?.close()
        }
    }

    private fun initInfo() {
        tvInfoDescription.text = getString(R.string.info_task_description)
        ivTaskInfo.setOnClickListener {
            AnimationUtils.loadAnimation(this, R.anim.alpha_show).also { animation ->
                bgInfo.visible()
                bgInfo.startAnimation(animation)
            }
        }
        ivInfoClose.setOnClickListener {
            AnimationUtils.loadAnimation(this, R.anim.alpha_hide).also { animation ->
                bgInfo.startAnimation(animation)
                bgInfo.gone()
            }
        }
    }

    private fun initStepViews() {
        val stringList = ArrayList<String>()

        Timber.i("RateType: ${openedTask.rateType}")

        stringList.add("1. ${if (openedTask.type == net.makemoney.android.data.models.tasks.OwnTaskType.SEARCH) getString(R.string.own_task_search_description, getTaskKeywords(openedTask.keywords)) else getString(R.string.own_task_direct_description)}")
        stringList.add("2. ${openedTask.text}")
        if (openedTask.rateType != net.makemoney.android.data.models.tasks.TaskRateType.WITHOUT_RATE) {
            stringList.add("3. ${if (openedTask.rateType == net.makemoney.android.data.models.tasks.TaskRateType.RATE_WITH_FEED) getString(R.string.own_task_rating_with_feedback, getTaskKeywords(openedTask.rateKeywords)) else getString(R.string.own_task_only_rating_description)}")
        }

        var completedPosition = 0
        if (isAppInstalled()) {
            completedPosition = when {
                openedTask.isRatingAvailable in 1..2 -> 2
                openedTask.isRatingAvailable == 3 -> 3
                else -> 1
            }
        }
        vertical_step_view
                .setStepsViewIndicatorComplectingPosition(completedPosition).reverseDraw(false)
                .setStepViewTexts(stringList)
                .setLinePaddingProportion(1.25f)
                .setTextSize(if ((appRes.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) != Configuration.SCREENLAYOUT_SIZE_XLARGE) 14 else 18)
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(appContext, R.color.colorPrimaryText))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(appContext, R.color.colorSecondaryText))
                .setStepViewComplectedTextColor(ContextCompat.getColor(appContext, R.color.colorPrimaryText))
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(appContext, R.color.colorSecondaryText))
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(appContext, R.drawable.ic_progress_done))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(appContext, R.drawable.ic_progress_disabled))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(appContext, R.drawable.ic_progress_attention))

        when (openedTask.rateType) {
            net.makemoney.android.data.models.tasks.TaskRateType.ONLY_RATE -> {
                vertical_step_view.layoutParams.height = VERTICAL_STEP_VIEW_HEIGHT_ONLY_RATE
                vertical_step_view.requestLayout()
            }
            net.makemoney.android.data.models.tasks.TaskRateType.RATE_WITH_FEED -> {
                vertical_step_view.layoutParams.height = VERTICAL_STEP_VIEW_HEIGHT_WITH_FEED
                vertical_step_view.requestLayout()
            }
        }

        val stepsBeanList = ArrayList<String>()
        for (i in 1..openedTask.days) {
            stepsBeanList.add(getString(R.string.task_launch_number, i))
        }

        vertical_step_view_launch
                .setStepsViewIndicatorComplectingPosition(openedTask.daysPassed).reverseDraw(false)
                .setStepViewTexts(stepsBeanList)
                .setLinePaddingProportion(1f)
                .setTextSize(if ((appRes.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) != Configuration.SCREENLAYOUT_SIZE_XLARGE) 14 else 18)
                .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(appContext, R.color.colorPrimaryText))
                .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(appContext, R.color.colorSecondaryText))
                .setStepViewComplectedTextColor(ContextCompat.getColor(appContext, R.color.colorPrimaryText))
                .setStepViewUnComplectedTextColor(ContextCompat.getColor(appContext, R.color.colorSecondaryText))
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(appContext, R.drawable.ic_progress_done))
                .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(appContext, R.drawable.ic_progress_disabled))
                .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(appContext, R.drawable.ic_progress_attention))
    }

    private fun getTaskKeywords(keywords: List<String>): String {
        var resultKeywords = "\""
        if (keywords.isNotEmpty()) {
            for (i in 0 until keywords.size) {
                resultKeywords += keywords[i]
                resultKeywords += if (i != keywords.size - 1) ", " else "\""
            }
        } else resultKeywords += openedTask.packageName + "\""
        return resultKeywords
    }

    private fun setUpAppBarBehavior() {
        app_bar_task.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            updateToolbarAfterContainerBecomeFullscreen(verticalOffset + appBarLayout.totalScrollRange == 0)
        })
    }

    private fun isAppInstalled(): Boolean {
        val packageManager = packageManager
        return try {
            packageManager.getPackageInfo(openedTask.packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e("${e.message}")
            false
        }
    }

    private fun openApp(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent.let { nonNullIntent ->
            startActivity(nonNullIntent)
            startTimer()
        }
    }

    private fun startTimer() {
        runnable = Runnable {
            toast(getString(R.string.own_task_was_completed))
            isTaskTimerDone = true
        }
        handler = Handler()
        handler?.postDelayed(runnable, openedTask.durationInApp.convertToMilliseconds())
    }

    //Paris library cannot work with Toolbar that's why was decided to change all attributes one by one
    private fun updateToolbarAfterContainerBecomeFullscreen(isFullscreen: Boolean) = if (isFullscreen) {
        //AppBar collapsed
        containerAdditionalTasks.setBackgroundResource(R.color.colorPrimaryDark)
        changeToolbarColorWithAnimation()
    } else {
        //AppBar expanded
        if (changeToolbarColorWithAnimation.isStarted) {
            changeToolbarColorWithAnimation.removeAllUpdateListeners()
        }
        containerAdditionalTasks.setBackgroundResource(R.drawable.shape_content_top_rounded)
        toolbar_task.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
    }

    private fun changeToolbarColorWithAnimation() {
        val toolbarColorFrom = ContextCompat.getColor(this, R.color.colorPrimary)
        val toolbarColorTo = ContextCompat.getColor(this, R.color.colorPrimaryDark)

        changeToolbarColorWithAnimation = ValueAnimator.ofObject(ArgbEvaluator(), toolbarColorFrom, toolbarColorTo)
        changeToolbarColorWithAnimation.duration = 250
        changeToolbarColorWithAnimation.addUpdateListener {
            toolbar_task.setBackgroundColor(it.animatedValue as Int)
        }
        changeToolbarColorWithAnimation.start()
    }

    private fun openTaskInPlayMarket(appPackageName: String) {
        Timber.e(playMarketWayToApp + appPackageName)
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(playMarketWayToApp + appPackageName)))
        } catch (e: android.content.ActivityNotFoundException) {
            Timber.e(e.message)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(playMarketBrowserWayToApp + appPackageName)))
        }
    }

    companion object {
        private const val RC_GET_SCREEN = 48475
        private const val VERTICAL_STEP_VIEW_HEIGHT_WITH_FEED = 750
        private const val VERTICAL_STEP_VIEW_HEIGHT_ONLY_RATE = 630
        const val OPENED_TASK = "opened_task"
        const val OPENED_TASK_ID = "OPENED_TASK_ID"
        const val RC_TASK_ACTIVITY = 7458
    }
}