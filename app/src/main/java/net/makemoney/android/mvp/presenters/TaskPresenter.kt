package net.makemoney.android.mvp.presenters

import net.makemoney.android.api.DefaultCallback
import net.makemoney.android.api.RestClient
import net.makemoney.android.data.mappers.TaskMapper
import net.makemoney.android.data.responses.EmptyResponse
import net.makemoney.android.data.responses.TaskResponseItem
import net.makemoney.android.mvp.contracts.TaskContract
import okhttp3.RequestBody
import timber.log.Timber


class TaskPresenter(override val view: TaskContract.View) : TaskContract.Presenter {

    override fun taskInstalled(taskId: Int) = net.makemoney.android.api.RestClient.api.taskInstalledFromOurApp(taskId).enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.EmptyResponse>(view) {
        override fun onResponse(body: net.makemoney.android.data.responses.EmptyResponse) {
            Timber.i("task $taskId was installed")
        }
    })


    override fun taskCompleted(taskId: Int) {
        net.makemoney.android.api.RestClient.api.taskCompleted(taskId).enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.TaskResponseItem>(view) {
            override fun onResponse(body: net.makemoney.android.data.responses.TaskResponseItem) {
                view.taskUpdated(net.makemoney.android.data.mappers.TaskMapper().transformTask(body))
            }
        })
    }

    override fun sendScreenshot(id: Int, screen: RequestBody) {
        net.makemoney.android.api.RestClient.api.sendScreenshot(id, screen).enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.EmptyResponse>(view) {
            override fun onResponse(body: net.makemoney.android.data.responses.EmptyResponse) {
                view.screenshotWasSend()
            }
        })
    }
}