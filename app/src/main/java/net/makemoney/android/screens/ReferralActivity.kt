package net.makemoney.android.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_policy.*
import kotlinx.android.synthetic.main.activity_referral.*
import net.makemoney.android.R
import net.makemoney.android.adapters.CombinedDetailsAdapter
import net.makemoney.android.data.models.details.DetailsItem
import net.makemoney.android.extensions.gone
import net.makemoney.android.extensions.visible
import net.makemoney.android.mvp.contracts.ReferralContract
import net.makemoney.android.mvp.presenters.ReferralPresenter
import net.makemoney.android.skeleton.activity.BaseActivity
import net.makemoney.android.utils.AppPreferences
import net.makemoney.android.utils.decoration.GridItemDecoration
import org.jetbrains.anko.toast
import timber.log.Timber


class ReferralActivity : BaseActivity<ReferralPresenter>(), ReferralContract.View {

    private val adapter = net.makemoney.android.adapters.CombinedDetailsAdapter(arrayListOf()).apply {
        addLoadingFooter()
    }

    override fun getLayoutId(): Int = R.layout.activity_referral

    override fun createPresenter(): ReferralPresenter = ReferralPresenter(this)

    override fun initViews() {
        initRecycler()
        setHomeButton()
        initInfo()
        tvReferralCopy.setOnClickListener {
            copyToClipBoard(AppPreferences.secondaryPromo)
        }
        presenter.getReferralItems()
    }

    override fun retrieveReferralItems(items: List<net.makemoney.android.data.models.details.DetailsItem>) {
        try {
            adapter.addItems(items)
            rvReferralDetails.requestLayout()
        } catch (e: Exception) {
            Timber.e(e.message)
        }
    }

    private fun initRecycler() {
        rvReferralDetails.layoutManager = GridLayoutManager(this, 2)
        rvReferralDetails.itemAnimator = DefaultItemAnimator()
        rvReferralDetails.setHasFixedSize(true)
        rvReferralDetails.addItemDecoration(GridItemDecoration())
        rvReferralDetails.adapter = adapter
    }

    private fun initInfo() {
        ivReferralInfo.setOnClickListener {
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
        setSupportActionBar(toolbar_referral)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_referral.setNavigationIcon(R.drawable.ic_toolbar_back)
        toolbar_referral.setNavigationOnClickListener {
            finish()
        }
    }

    private fun copyToClipBoard(text: String) {
        //Copying to clip board
        val clipBoardManager = getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clipData = ClipData.newPlainText("", text)
        clipBoardManager?.primaryClip = clipData
        toast(R.string.referral_promo_copied)
    }
}