package net.makemoney.android.utils.pagination


/**
 * This interface should be implemented in adapters which suppose to support pagination
 * @param E type of items displayed in recycler view which uses current adapter
 */
interface PaginationAdapter<E> {

    /**
     * Add loading view holder[net.traffapp.money.tasks.holders.LoadingHolder] in the end of the
     * existing list
     */
    fun addLoadingFooter()

    /**
     * Remove previously added holder
     */
    fun removeLoadingFooter()

    /**
     * Update current list with new items
     */
    fun addItems(newItems: List<E>)

    /**
     * Use this to remove all existing items
     */
    fun clearItems()
}