package net.makemoney.android.screens

import android.view.animation.AnimationUtils
import com.adcolony.sdk.AdColony
import com.adcolony.sdk.AdColonyAppOptions
import com.fyber.Fyber
import com.google.android.gms.ads.MobileAds
import com.google.firebase.iid.FirebaseInstanceId
import com.offertoro.sdk.OTOfferWallSettings
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_other.*
import net.makemoney.android.data.models.marathon.MarathonItem
import net.makemoney.android.extensions.*
import net.makemoney.android.mvp.contracts.MainContract
import net.makemoney.android.mvp.presenters.MainPresenter
import net.makemoney.android.screens.fragments.*
import net.makemoney.android.skeleton.activity.BaseActivity
import net.makemoney.android.skeleton.fragment.BaseFragment
import net.makemoney.android.utils.AppPreferences
import net.makemoney.android.R
import org.jetbrains.anko.startActivity



class MainActivity : BaseActivity<MainPresenter>(), MainContract.View {

    var balance = 0f
    private var bottomNavigationPosition = 0
    private var marathonItem: MarathonItem? = null

    private var isItNeedToShowMenu: Boolean = true

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun createPresenter(): MainPresenter = MainPresenter(this)

    override fun initViews() {
        marathonItem = intent?.getParcelableExtra(EX_MARATHON_ITEM)
        replaceFragment(R.id.container_main, newInstance<TasksFragment>(EX_MARATHON_ITEM to marathonItem!!))
        initMenuOther()
        getFCMToken()
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                net.makemoney.android.R.id.navigation_tasks -> showBlock(0, newInstance<TasksFragment>(EX_MARATHON_ITEM to marathonItem!!))
                net.makemoney.android.R.id.navigation_videos -> showBlock(1, newInstance<VideoFragment>())
                net.makemoney.android.R.id.navigation_games -> showBlock(2, newInstance<EntertainmentFragment>())
                net.makemoney.android.R.id.navigation_more -> if (isItNeedToShowMenu) showMenuOther()
                else -> showBlock(0, newInstance<EmptyFragment>())
            }
            return@setOnNavigationItemSelectedListener true
        }
        toolbar_iv_profile.setOnClickListener {
            isItNeedToShowMenu = false
            showBlock(3, newInstance<ProfileFragment>())
            returnBottomNavigationSelectedPosition()
        }
        ivInfoClose.setOnClickListener {
            AnimationUtils.loadAnimation(this, net.makemoney.android.R.anim.alpha_hide).also { animation ->
                bgInfo.startAnimation(animation)
                bgInfo.gone()
            }
        }
        Fyber.with(getString(net.makemoney.android.R.string.fyber_id), this)
                .withUserId(AppPreferences.token)
                .start()
        MobileAds.initialize(appContext, getString(net.makemoney.android.R.string.google_mobile_ads_id))
        AdColony.configure(this, AdColonyAppOptions().setUserID(AppPreferences.token).setKeepScreenOn(true),
                getString(net.makemoney.android.R.string.adColony_app_id), getString(net.makemoney.android.R.string.adColony_zone_id))
        OTOfferWallSettings.getInstance().configInit(getString(net.makemoney.android.R.string.offerToro_app_id),
                getString(net.makemoney.android.R.string.offerToro_secret_key), AppPreferences.token);
    }

    override fun onResume() {
        super.onResume()
        presenter.getBalance()
    }

    override fun retrieveBalance(balance: Float) {
        this.balance = balance
        updateBalance()
        val fragment = supportFragmentManager?.findFragmentById(R.id.container_main)
        when (fragment) {
            is ProfileFragment -> fragment.updateBalance()
        }
    }

    private fun getFCMToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            val token = it.token
            presenter.sendFCMToken(token)
        }
    }

    private fun showBlock(newPosition: Int, primaryFragment: BaseFragment<*, *>) {
        when {
            newPosition == bottomNavigationPosition -> return
            newPosition > bottomNavigationPosition -> {
                showPrevBlock(primaryFragment)
            }
            newPosition < bottomNavigationPosition -> {
                showNextBlock(primaryFragment)
            }
        }
        bottomNavigationPosition = newPosition
    }

    /**
     * Change primaryFragment with selected animation. Call this method when new selected item is on the left
     * of current selected item
     * @param primaryFragment next content window
     */
    private fun showNextBlock(primaryFragment: BaseFragment<*, *>) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(net.makemoney.android.R.anim.next, net.makemoney.android.R.anim.next_end)
                .replace(R.id.container_main, primaryFragment, primaryFragment.TAG).commitAllowingStateLoss()
    }

    /**
     * Change primaryFragment with selected animation. Call this method when new selected item is on the right
     * of current selected item
     * @param primaryFragment next content window
     */
    private fun showPrevBlock(primaryFragment: BaseFragment<*, *>) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(net.makemoney.android.R.anim.prev, net.makemoney.android.R.anim.prev_end)
                .replace(R.id.container_main, primaryFragment, primaryFragment.TAG).commitAllowingStateLoss()
    }

    private fun initMenuOther() {
        menu_profile.setOnClickListener {
            hideMenuOther()
            showBlock(3, newInstance<ProfileFragment>())
        }
        menu_balance.setOnClickListener {
            hideMenuOther()
            returnBottomNavigationSelectedPosition()
            startActivity<BalanceActivity>(ProfileFragment.EXTRA_BALANCE to balance)
        }
        menu_referral.setOnClickListener {
            hideMenuOther()
            returnBottomNavigationSelectedPosition()
            startActivity<ReferralActivity>()
        }
        menu_policy.setOnClickListener {
            hideMenuOther()
            returnBottomNavigationSelectedPosition()
            startActivity<PrivacyPolicyActivity>()
        }
        mainMenuBackground.setOnClickListener {
            hideMenuOther()
            returnBottomNavigationSelectedPosition()
        }
    }

    private fun showMenuOther() {
        AnimationUtils.loadAnimation(this, net.makemoney.android.R.anim.alpha_show).also { animation ->
            mainMenuBackground.visible()
            mainMenuBackground.startAnimation(animation)
        }
        AnimationUtils.loadAnimation(this, net.makemoney.android.R.anim.up).also { animation ->
            menuOther.visible()
            menuOther.startAnimation(animation)
        }
    }

    private fun hideMenuOther() {
        AnimationUtils.loadAnimation(this, net.makemoney.android.R.anim.alpha_hide).also { animation ->
            mainMenuBackground.startAnimation(animation)
            mainMenuBackground.gone()
        }
        AnimationUtils.loadAnimation(this, net.makemoney.android.R.anim.down).also { animation ->
            menuOther.startAnimation(animation)
            menuOther.gone()
        }
    }

    private fun returnBottomNavigationSelectedPosition() {
        if (bottomNavigationPosition == 3 && isItNeedToShowMenu) return //if we are already in tabs_more menu other will be loop. In case when [isItNeedToShowMenu] == false (click toolbar profile image) let bottomNavigation bar to select navigation tab more
        bottomNavigation.selectedItemId = when (bottomNavigationPosition) {
            0 -> net.makemoney.android.R.id.navigation_tasks
            1 -> net.makemoney.android.R.id.navigation_videos
            2 -> net.makemoney.android.R.id.navigation_games
            3 -> net.makemoney.android.R.id.navigation_more
            else -> net.makemoney.android.R.id.navigation_tasks
        }
        if (!isItNeedToShowMenu) isItNeedToShowMenu = !isItNeedToShowMenu
    }

    fun updateBalance() {
        toolbar_balance.text = balance.toBalance()
    }

    fun showInfo(text: String) {
        tvInfo.text = text
        AnimationUtils.loadAnimation(this, net.makemoney.android.R.anim.alpha_show).also { animation ->
            bgInfo.visible()
            bgInfo.startAnimation(animation)
        }
    }

    companion object {
        const val EX_MARATHON_ITEM = "ex.marathon.item"
    }
}
