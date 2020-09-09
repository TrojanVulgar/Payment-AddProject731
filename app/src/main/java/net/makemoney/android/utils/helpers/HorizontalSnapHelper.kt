package net.makemoney.android.utils.helpers

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.View


class HorizontalSnapHelper : LinearSnapHelper() {

    private lateinit var horizontalHelper: OrientationHelper

    override fun attachToRecyclerView(recyclerView: RecyclerView?) {
        horizontalHelper = OrientationHelper.createHorizontalHelper(recyclerView?.layoutManager)
        super.attachToRecyclerView(recyclerView)
    }

    override fun calculateDistanceToFinalSnap(layoutManager: RecyclerView.LayoutManager, targetView: View)
            : IntArray? = if (layoutManager.canScrollHorizontally()) {
        arrayOf(distanceToStart(targetView, horizontalHelper), VERTICAL_SNAP_DISTANCE)
    } else {
        arrayOf(VERTICAL_SNAP_DISTANCE, VERTICAL_SNAP_DISTANCE)
    }.toIntArray()

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager)
            : View? = getStartView(layoutManager as LinearLayoutManager, horizontalHelper)

    private fun getStartView(layoutManager: LinearLayoutManager,
                             helper: OrientationHelper): View? {
        val firstChild = layoutManager.findFirstVisibleItemPosition()
        //if recycler view scrolled to the end
        val isLastItem = layoutManager
                .findLastCompletelyVisibleItemPosition() == layoutManager.itemCount - 1
        //No need to do a snap
        if (firstChild == RecyclerView.NO_POSITION || isLastItem) return null
        val child = layoutManager.findViewByPosition(firstChild)
        return if (helper.getDecoratedEnd(child) >= helper.getDecoratedMeasurement(child) / 2 && helper.getDecoratedEnd(child) > 0) {
            child
        } else {
            layoutManager.findViewByPosition(firstChild + 1)
        }
    }

    private fun distanceToStart(targetView: View, helper: OrientationHelper)
            : Int = helper.getDecoratedStart(targetView) - helper.startAfterPadding

    companion object {
        const val VERTICAL_SNAP_DISTANCE = 0 //if this passed then SnapHelper will ignore snapping
    }

}