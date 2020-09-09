package net.makemoney.android.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.AdapterView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.android.synthetic.main.dialog_country_selector.view.*
import kotlinx.android.synthetic.main.dialog_promo.view.*
import net.makemoney.android.R
import net.makemoney.android.adapters.CountrySelectorAdapter
import net.makemoney.android.data.models.country.CountryItem
import net.makemoney.android.data.models.marathon.MarathonItem
import net.makemoney.android.extensions.*
import net.makemoney.android.mvp.contracts.StartContract
import net.makemoney.android.mvp.presenters.StartPresenter
import net.makemoney.android.screens.MainActivity.Companion.EX_MARATHON_ITEM
import net.makemoney.android.skeleton.activity.BaseActivity
import net.makemoney.android.utils.AppPreferences
import net.makemoney.android.utils.InstantDialog
import net.makemoney.android.utils.permissions.OnPermissionResultListener
import net.makemoney.android.utils.permissions.PermissionUtil
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import timber.log.Timber

class StartActivity : BaseActivity<StartPresenter>(), StartContract.View {

    companion object {
        const val RC_SIGN_IN = 111
        const val RC_POLICY = 41241
    }

    private lateinit var client: GoogleSignInClient
    private var promoDialog: AlertDialog? = null
    private val googleSignInOptions: GoogleSignInOptions =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestId()
                    .requestEmail()
                    .build()

    override fun getLayoutId(): Int = R.layout.activity_start

    override fun createPresenter(): StartPresenter = StartPresenter(this)

    override fun initViews() {
        btnEnter.setOnClickListener {
            startSession()
        }
        startSession()
        if (!AppPreferences.privacyPolicyIsAccepted) {
            startActivityForResult<PrivacyPolicyActivity>(RC_POLICY)
        } else {
            requestPermission()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            presenter.signIn(GoogleSignIn.getSignedInAccountFromIntent(data))
        }
        if (requestCode == RC_POLICY && resultCode == Activity.RESULT_CANCELED) {
            finish()
        }
        if (requestCode == RC_POLICY && resultCode == Activity.RESULT_OK) {
            requestPermission()
        }
    }

    override fun onDataFetchError(error: String) {
        toast(error)
    }

    override fun onLoggedIn(isNew: Boolean) = if (isNew) {
        showPromoDialog()
    } else {
        startFetchingData()
    }

    override fun onPromoConfirmed() {
        promoDialog?.dismiss()
        startFetchingData()
    }

    override fun onCountriesFetched(countries: List<net.makemoney.android.data.models.country.CountryItem>) {
        var selectedCountry = countries[0]
        val dialogView = inflate(R.layout.dialog_country_selector)
        val countryDialog = InstantDialog(this, dialogView).show()
        val adapter = net.makemoney.android.adapters.CountrySelectorAdapter(countries)
        dialogView.spinner_auth_countries.adapter = adapter
        dialogView.spinner_auth_countries.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCountry = countries[dialogView.spinner_auth_countries?.selectedItemPosition
                        ?: 0]
            }
        }
        dialogView.btn_auth_select_country.setOnClickListener {
            presenter.registerUser(selectedCountry.id)
            countryDialog?.dismiss()
        }
    }

    override fun retrieveMarathon(marathonItem: net.makemoney.android.data.models.marathon.MarathonItem) {
        startAffinity<MainActivity>(EX_MARATHON_ITEM to marathonItem)
    }

    override fun updateUI() {
        btnEnter.setOnClickListener {
            startSession()
        }
        pbEnter.gone()
        btnEnter.visible()
    }

    private fun startSession() {
        if (AppPreferences.token.isNotEmpty()) { //if user is authorized
            startFetchingData()
        } else {
            client = GoogleSignIn.getClient(this, googleSignInOptions)
            btnEnter.setOnClickListener {
                startActivityForResult(client.signInIntent, RC_SIGN_IN)
            }
        }
    }

    private fun showPromoDialog() {
        val dialogView = inflate(R.layout.dialog_promo)
        promoDialog = InstantDialog(this, dialogView).show()
        dialogView.et_promo.onTextChange { _, _, _, count ->
            dialogView.btn_promo_ok.text = if (count > 0) {
                getString(R.string.all_smthing_accept)
            } else {
                getString(R.string.signin_promo_skip)
            }
        }
        dialogView.btn_promo_ok.setOnClickListener { _ ->
            dialogView.et_promo.string.also {
                if (it.isEmpty()) {
                    onPromoConfirmed()
                } else {
                    presenter.registerPromo(it)
                }
            }
        }
    }

    private fun requestPermission() {
        val permissionUtil = PermissionUtil(this)
        permissionUtil.setListener(object : OnPermissionResultListener {
            override fun granted() {
                Timber.i("granted")
            }
        })
        permissionUtil.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    /**
     * Change UI for data fetching and start fetching
     */
    private fun startFetchingData() {
        btnEnter.gone()
        pbEnter.visible()
        presenter.startFetchingData()
    }
}