package net.makemoney.android.mvp.contracts

import net.makemoney.android.data.models.marathon.MarathonItem
import net.makemoney.android.data.models.tasks.OwnTaskItem
import net.makemoney.android.data.models.tasks.PartnerTaskItem
import net.makemoney.android.skeleton.presentation.BasePresenter
import net.makemoney.android.skeleton.presentation.BaseView


interface TasksContract {

    interface View: BaseView {
        fun retrieveMarathon(marathonItem: net.makemoney.android.data.models.marathon.MarathonItem)

        fun marathonCompleted(balance: Float)

        fun retrieveNewTasks(tasks: List<net.makemoney.android.data.models.tasks.OwnTaskItem>)

        fun retrieveActiveTasks(tasks: List<net.makemoney.android.data.models.tasks.OwnTaskItem>)

        fun retrievePartnerTasks(tasks: List<net.makemoney.android.data.models.tasks.PartnerTaskItem>)
    }

    interface Presenter: BasePresenter<View> {
        fun getMarathon()

        fun takeMarathonAward(id: Int)

        fun getNewTasks()

        fun getActiveTasks()

        fun getPartnerTasks()
    }
}