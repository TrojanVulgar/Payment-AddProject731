package net.makemoney.android.mvp.contracts

import net.makemoney.android.data.models.tasks.OwnTaskItem
import net.makemoney.android.skeleton.presentation.BasePresenter
import net.makemoney.android.skeleton.presentation.BaseView
import okhttp3.RequestBody


/**
 * This contract used in [net.traffapp.money.screens.TaskActivity]
 */
interface TaskContract {

    interface View : BaseView {
        fun taskUpdated(item: net.makemoney.android.data.models.tasks.OwnTaskItem)

        fun screenshotWasSend()
    }

    interface Presenter : BasePresenter<View> {
        fun taskInstalled(taskId: Int)

        fun taskCompleted(taskId: Int)

        fun sendScreenshot(id: Int, screen: RequestBody)
    }
}