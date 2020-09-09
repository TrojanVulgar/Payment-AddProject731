package net.makemoney.android.mvp.presenters

import net.makemoney.android.api.DefaultCallback
import net.makemoney.android.api.RestClient
import net.makemoney.android.data.responses.EmptyResponse
import net.makemoney.android.data.responses.QuizQuestionsResponseItem
import net.makemoney.android.mvp.contracts.QuizContract


class QuizPresenter(override val view: QuizContract.View) : QuizContract.Presenter {

    override fun getQuizQuestions(id: Int) = net.makemoney.android.api.RestClient.api.getQuizzesQuestions(id).enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.QuizQuestionsResponseItem>(view){
            override fun onResponse(body: net.makemoney.android.data.responses.QuizQuestionsResponseItem) {
                view.fetchedQuizzes(body.quizUniqueNumber, body.items)
            }
        })

    override fun quizCompleted(quizUniqueNumber: Int, score: Int) = net.makemoney.android.api.RestClient.api.quizCompleted(quizUniqueNumber, score).enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.EmptyResponse>(view){
            override fun onResponse(body: net.makemoney.android.data.responses.EmptyResponse) {}
        })
}