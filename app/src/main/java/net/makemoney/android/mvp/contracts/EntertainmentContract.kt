package net.makemoney.android.mvp.contracts

import net.makemoney.android.data.models.entertainment.GameTaskItem
import net.makemoney.android.data.models.entertainment.QuizTaskItem
import net.makemoney.android.skeleton.presentation.BasePresenter
import net.makemoney.android.skeleton.presentation.BaseView


interface EntertainmentContract {

    interface View : BaseView {
        fun retrieveQuizzes(items: List<net.makemoney.android.data.models.entertainment.QuizTaskItem>)

        fun retrieveGames(items: List<net.makemoney.android.data.models.entertainment.GameTaskItem>)

        fun gameUpdated(balance: Float)
    }

    interface Presenter : BasePresenter<View> {
        fun getQuizzes()

        fun getGames()

        fun updateGame(gameId: Int, score: Float)
    }
}