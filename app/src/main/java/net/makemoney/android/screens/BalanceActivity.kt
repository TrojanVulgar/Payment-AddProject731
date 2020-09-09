package net.makemoney.android.screens

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.animation.AnimationUtils
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_balance.*
import net.makemoney.android.App.Companion.context
import net.makemoney.android.R
import net.makemoney.android.extensions.gone
import net.makemoney.android.extensions.toBalanceSpanEdition
import net.makemoney.android.extensions.visible
import net.makemoney.android.mvp.contracts.BalanceContract
import net.makemoney.android.mvp.presenters.BalancePresenter
import net.makemoney.android.skeleton.activity.BaseActivity
import net.makemoney.android.utils.decoration.GridItemDecoration
import org.jetbrains.anko.startActivity
import timber.log.Timber


class BalanceActivity : BaseActivity<BalancePresenter>(), BalanceContract.View {

    private var balance: Float = 0f

    private val adapter = net.makemoney.android.adapters.CombinedDetailsAdapter(arrayListOf()).apply {
        addLoadingFooter()
    }

    override fun getLayoutId(): Int = R.layout.activity_balance

    override fun createPresenter(): BalancePresenter = BalancePresenter(this)

    override fun initViews() {
        balance = intent.getFloatExtra(EXTRA_BALANCE, 0f)
        initRecycler()
        initInfo()
        setHomeButton()
        updateView()
        tvBalanceWithdrawal.setOnClickListener {
            startActivity<WithdrawalActivity>()
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.getBalanceDetails()
    }

    override fun retrieveBalanceDetails(detailsItems: List<net.makemoney.android.data.models.details.DetailsItem>, balance: Float) {
        try {
            this.balance = balance
            updateView()
            adapter.addItems(detailsItems)
            rvBalanceDetails.requestLayout()
        } catch (e: IllegalStateException) {
            Timber.e(e.message)
        } catch (e: NullPointerException) {
            Timber.e(e.message)
        }
    }

    private fun initRecycler() {
        rvBalanceDetails.layoutManager = GridLayoutManager(this, 2)
        rvBalanceDetails.itemAnimator = DefaultItemAnimator()
        rvBalanceDetails.setHasFixedSize(true)
        rvBalanceDetails.addItemDecoration(GridItemDecoration())
        rvBalanceDetails.adapter = adapter
    }

    private fun initInfo() {
        tvInfoDescription.text = getString(R.string.info_balance_description)
        ivBalanceInfo.setOnClickListener {
            AnimationUtils.loadAnimation(this, R.anim.alpha_show).also { animation ->
                bgInfo.visible()
                bgInfo.startAnimation(animation)
            }
        }
        ivInfoClose.setOnClickListener {
            AnimationUtils.loadAnimation(this, R.anim.alpha_hide).also { animation ->
                bgInfo.startAnimation(animation)
                bgInfo.gone()
            }
        }
    }

    private fun setHomeButton() {
        setSupportActionBar(toolbar_balance)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_balance.setNavigationIcon(R.drawable.ic_toolbar_back)
        toolbar_balance.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateView() {
        val description = SpannableStringBuilder(getString(R.string.balance_available_to_award, balance.toBalanceSpanEdition()))
        description.setSpan(ImageSpan(context, R.drawable.ic_coin), description.length - 1, description.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        tvBalanceDescription.setText(description, TextView.BufferType.SPANNABLE)
    }

    companion object {
        const val EXTRA_BALANCE = "extra.activity.balance"
    }
}