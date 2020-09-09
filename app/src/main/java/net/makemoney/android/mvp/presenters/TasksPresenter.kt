package net.makemoney.android.mvp.presenters

import net.makemoney.android.api.DefaultCallback
import net.makemoney.android.api.RestClient
import net.makemoney.android.data.mappers.MarathonMapper
import net.makemoney.android.data.mappers.TaskMapper
import net.makemoney.android.data.responses.MarathonResponseItem
import net.makemoney.android.data.responses.PartnerResponseItem
import net.makemoney.android.data.responses.TaskResponseItem
import net.makemoney.android.mvp.contracts.TasksContract


class TasksPresenter(override val view: TasksContract.View) : TasksContract.Presenter {

    private val marathonMapper = net.makemoney.android.data.mappers.MarathonMapper()
    private val taskMapper = net.makemoney.android.data.mappers.TaskMapper()

    override fun getMarathon() = net.makemoney.android.api.RestClient.api.getMarathon().enqueue(object : net.makemoney.android.api.DefaultCallback<net.makemoney.android.data.responses.MarathonResponseItem>(view) {
        override fun onResponse(body: net.makemoney.android.data.responses.MarathonResponseItem) {
            view.retrieveMarathon(marathonMapper.transformTo(body))
        }
    })

    override fun takeMarathonAward(id: Int) = net.makemoney.android.api.RestClient.api.takeMarathonAward(id).enqueue(object : net.makemoney.android.api.DefaultCallback<Float>(view) {
        override fun onResponse(body: Float) {
            view.marathonCompleted(body)
        }
    })

    override fun getNewTasks() = net.makemoney.android.api.RestClient.api.getNewTasks().enqueue(object : net.makemoney.android.api.DefaultCallback<List<net.makemoney.android.data.responses.TaskResponseItem>>(view) {
        override fun onResponse(body: List<net.makemoney.android.data.responses.TaskResponseItem>) {
            view.retrieveNewTasks(taskMapper.transform(body))
        }
    })

    override fun getActiveTasks() = net.makemoney.android.api.RestClient.api.getActiveTasks().enqueue(object : net.makemoney.android.api.DefaultCallback<List<net.makemoney.android.data.responses.TaskResponseItem>>(view) {
        override fun onResponse(body: List<net.makemoney.android.data.responses.TaskResponseItem>) {
            view.retrieveActiveTasks(taskMapper.transform(body))
        }
    })

    override fun getPartnerTasks() = net.makemoney.android.api.RestClient.api.getPartnersTasks().enqueue(object : net.makemoney.android.api.DefaultCallback<List<net.makemoney.android.data.responses.PartnerResponseItem>>(view) {
        override fun onResponse(body: List<net.makemoney.android.data.responses.PartnerResponseItem>) {
            view.retrievePartnerTasks(taskMapper.transformPartners(body))
        }
    })
}