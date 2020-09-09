package net.makemoney.android.mvp.presenters

import net.makemoney.android.api.DefaultCallback
import net.makemoney.android.api.RestClient
import net.makemoney.android.data.mappers.TaskMapper
import net.makemoney.android.data.responses.BalanceSimple
import net.makemoney.android.data.responses.GameResponseItem
import net.makemoney.android.data.responses.QuizzesResponse
import net.makemoney.android.mvp.contracts.EntertainmentContract
import timber.log.Timber

class EntertainmentPresenter(override val view: EntertainmentContract.View) : EntertainmentContract.Presenter {

    private val mapper = net.makemoney.android.data.mappers.TaskMapper()

    override fun getQuizzes() = net.makemoney.android.api.RestClient.api.getQuizzes().enqueue(object : net.makemoney.android.api.DefaultCallback<List<net.makemoney.android.data.responses.QuizzesResponse>>(view) {
        override fun onResponse(body: List<net.makemoney.android.data.responses.QuizzesResponse>) {
            view.retrieveQuizzes(mapper.transformQuizzes(body))
        }
    })

    override fun getGames() = net.makemoney.android.api.RestClient.api.getGames().enqueue(object : net.makemoney.android.api.DefaultCallback<List<net.makemoney.android.data.responses.GameResponseItem>>(view) {
        override fun onResponse(body: List<net.makemoney.android.data.responses.GameResponseItem>) {
            view.retrieveGames(mapper.transformGames(body))
        }
    })

    override fun updateGame(gameId: Int, score: Float) = net.makemoney.android.api.RestClient.api.updateGame(gameId, score).enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.BalanceSimple>(view) {
        override fun onResponse(body: net.makemoney.android.data.responses.BalanceSimple) {
            Timber.i("Game($gameId) was updated")
            view.gameUpdated(body.balance)
        }
    })
}