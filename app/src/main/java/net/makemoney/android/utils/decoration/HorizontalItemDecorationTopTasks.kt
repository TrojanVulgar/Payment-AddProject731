package net.makemoney.android.utils.decoration

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import net.makemoney.android.R
import net.makemoney.android.extensions.getDimens


class HorizontalItemDecorationTopTasks : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemPadding = getDimens(R.dimen.small_view_padding)
        parent.let {
            if (it.getChildAdapterPosition(view) != 0) {
                outRect.left = itemPadding
            } else {
                outRect.right = itemPadding
            }
        }
    }
}