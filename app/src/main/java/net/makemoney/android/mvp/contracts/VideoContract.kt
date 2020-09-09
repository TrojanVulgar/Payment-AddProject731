package net.makemoney.android.mvp.contracts

import net.makemoney.android.data.models.tasks.VideoTaskItem
import net.makemoney.android.skeleton.presentation.BasePresenter
import net.makemoney.android.skeleton.presentation.BaseView


interface VideoContract {

    interface View : BaseView {
        fun videoRewarded()

        fun retrieveVideos(items: List<net.makemoney.android.data.models.tasks.VideoTaskItem>, todayLimit: Int)
    }

    interface Presenter : BasePresenter<View> {
        fun getVideos()

        fun getReward(id: Int)
    }
}