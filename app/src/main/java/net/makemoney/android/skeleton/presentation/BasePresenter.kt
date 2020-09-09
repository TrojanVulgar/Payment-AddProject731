package net.makemoney.android.skeleton.presentation

interface BasePresenter<V : BaseView> {
    val view: V
}
