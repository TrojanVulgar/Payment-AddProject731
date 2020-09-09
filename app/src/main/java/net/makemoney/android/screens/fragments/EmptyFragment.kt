package net.makemoney.android.screens.fragments

import android.view.View
import net.makemoney.android.R
import net.makemoney.android.screens.MainActivity
import net.makemoney.android.skeleton.EmptyPresenter
import net.makemoney.android.skeleton.fragment.BaseFragment


class EmptyFragment : BaseFragment<MainActivity, EmptyPresenter>() {

    override val TAG: String
        get() = EmptyFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_empty

    override fun createPresenter(): EmptyPresenter = EmptyPresenter(this)

    override fun initViews(rootView: View?) {

    }
}