package net.makemoney.android.utils.decoration

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import net.makemoney.android.R
import net.makemoney.android.extensions.getDimens

class GridItemDecoration(val spanCount: Int = 2) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPosition = parent.getChildAdapterPosition(view)
        if (itemPosition < spanCount) {
            outRect.top = getDimens(R.dimen.mid_view_padding)
        } else {
            outRect.top = getDimens(R.dimen.small_view_padding)
        }
        if (itemPosition % 2 == 0) outRect.right = getDimens(R.dimen.small_view_padding) else outRect.left = getDimens(R.dimen.small_view_padding)
    }
}