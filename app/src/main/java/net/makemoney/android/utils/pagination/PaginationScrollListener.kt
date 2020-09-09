package net.makemoney.android.utils.pagination

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView


/**
 * Class created to handle pagination mechanism in recycler view.
 * This class also extends [RecyclerView.OnScrollListener] which makes it possible to be added into
 * recyclerView as OnScrollListener.
 */
abstract class PaginationScrollListener : RecyclerView.OnScrollListener() {

    /**
     * New data still fetching
     */
    var isDataLoading: Boolean = false
    /**
     * Server has no items to provide
     */
    var isDataLoaded: Boolean = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val visibleItemsCount = layoutManager.childCount
        val firstCompletelyVisibleItemPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        val totalItemsCount = layoutManager.itemCount
        if (!isDataLoading && !isDataLoaded) {
            if ((visibleItemsCount + firstCompletelyVisibleItemPosition) >= totalItemsCount
                    && firstCompletelyVisibleItemPosition >= 0) {
                loadMoreItems()
            }
        }
    }

    abstract fun loadMoreItems()
}