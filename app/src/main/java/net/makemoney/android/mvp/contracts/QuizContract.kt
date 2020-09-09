package net.makemoney.android.mvp.contracts

import net.makemoney.android.data.models.entertainment.QuizQuestionItem
import net.makemoney.android.skeleton.presentation.BasePresenter
import net.makemoney.android.skeleton.presentation.BaseView

interface QuizContract {

    interface View : BaseView {
        fun fetchedQuizzes(quizUniqueNumber: Int, items: List<net.makemoney.android.data.models.entertainment.QuizQuestionItem>)
    }

    interface Presenter : BasePresenter<View> {
        fun getQuizQuestions(id: Int)

        fun quizCompleted(quizUniqueNumber: Int, score : Int)
    }
}