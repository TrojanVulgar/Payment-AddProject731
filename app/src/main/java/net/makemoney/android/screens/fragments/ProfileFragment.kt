package net.makemoney.android.screens.fragments

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.fragment_profile.*
import net.makemoney.android.R
import net.makemoney.android.adapters.ProgressAdapter
import net.makemoney.android.data.models.details.ProgressItem
import net.makemoney.android.data.models.profile.ProfileItem
import net.makemoney.android.data.responses.ProfileResponse
import net.makemoney.android.extensions.toBalance
import net.makemoney.android.mvp.contracts.ProfileContract
import net.makemoney.android.mvp.presenters.ProfilePresenter
import net.makemoney.android.screens.*
import net.makemoney.android.screens.ProfileAdditionalActivity.Companion.EXTRA_PROFILE_DATA
import net.makemoney.android.screens.ProfileAdditionalActivity.Companion.RC_PROFILE_DETAILS
import net.makemoney.android.skeleton.fragment.BaseFragment
import net.makemoney.android.utils.CardBackgroundProvider
import net.makemoney.android.utils.decoration.GridItemDecoration
import org.jetbrains.anko.startActivity
import timber.log.Timber


class ProfileFragment : BaseFragment<MainActivity, ProfilePresenter>(), ProfileContract.View {

    private val adapter = net.makemoney.android.adapters.ProgressAdapter(arrayListOf()).apply {
        addLoadingFooter()
    }

    private lateinit var profileItem: net.makemoney.android.data.models.profile.ProfileItem

    override val TAG: String
        get() = ProfileFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_profile

    override fun createPresenter(): ProfilePresenter = ProfilePresenter(this)

    override fun initViews(rootView: View?) {
        initRecycler()
        initButtons()
        initWithEmptyDataSet()
        presenter.getProfileData()
    }

    override fun retrieveProfileData(profileItem: net.makemoney.android.data.models.profile.ProfileItem, profileProgress: net.makemoney.android.data.responses.ProfileResponse.ProfileProgressResponseItem, additionalProgressItems: List<net.makemoney.android.data.models.details.ProgressItem>) {
        this.profileItem = profileItem
        adapter.addItems(additionalProgressItems)
        updateProfileCard(profileProgress)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_PROFILE_DETAILS && resultCode == Activity.RESULT_OK)
            presenter.getProfileData()
    }

    private fun initRecycler() {
        rvLevelProgress.layoutManager = GridLayoutManager(activity, 2)
        rvLevelProgress.itemAnimator = DefaultItemAnimator()
        rvLevelProgress.setHasFixedSize(true)
        rvLevelProgress.addItemDecoration(GridItemDecoration())
        rvLevelProgress.adapter = adapter
    }

    private fun initButtons() {
        tvAdditionalInfo.setOnClickListener {
            if (::profileItem.isInitialized) {
                val intent = Intent(activity, ProfileAdditionalActivity::class.java).apply {
                    putExtra(EXTRA_PROFILE_DATA, profileItem)
                }
                startActivityForResult(intent, RC_PROFILE_DETAILS)
            }
            else presenter.getProfileData()
        }
        contentProfileBalance.setOnClickListener {
            activity.startActivity<BalanceActivity>(EXTRA_BALANCE to activity.balance)
        }
        contentProfileReferral.setOnClickListener {
            activity.startActivity<ReferralActivity>()
        }
        contentProfilePrivacy.setOnClickListener {
            activity.startActivity<PrivacyPolicyActivity>()
        }
    }

    private fun initWithEmptyDataSet() {
        tvUsername.text = getString(R.string.profile_additional_username_hint)
        tvOverallLevel.text = getString(R.string.profile_overall_progress, "0/0")
        tvLevel.text = CardBackgroundProvider.getProfileLevelName(0)
    }

    private fun updateProfileCard(progressItem: net.makemoney.android.data.responses.ProfileResponse.ProfileProgressResponseItem) {
        try {
            if (::profileItem.isInitialized) tvUsername.text = profileItem.username
            with(progressItem) {
                rvLevelProgress.requestLayout()
                ivProfileCard.setBackgroundResource(CardBackgroundProvider.getProfileCardBackground(level))
                pbOverallLevel.progress = percent
                tvLevel.text = CardBackgroundProvider.getProfileLevelName(level)
                tvOverallLevel.text = getString(R.string.profile_overall_progress, "$currentProgress/$maxProgress")
                tvProfileBalance.text = activity.balance.toBalance()
            }
        } catch (e: IllegalStateException) {
            Timber.e(e.message)
        } catch (e: NullPointerException) {
            Timber.e(e.message)
        }
    }

    fun updateBalance() {
        try {
            tvProfileBalance.text = activity.balance.toBalance()
        } catch (e: IllegalStateException) {
            Timber.e(e.message)
        } catch (e: NullPointerException) {
            Timber.e(e.message)
        }
    }

    companion object {
        const val EXTRA_BALANCE = "extra_profile_balance"
    }
}