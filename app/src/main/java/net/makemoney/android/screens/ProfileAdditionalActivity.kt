package net.makemoney.android.screens

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.widget.TextView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import kotlinx.android.synthetic.main.activity_policy.*
import kotlinx.android.synthetic.main.activity_profile_additional.*
import kotlinx.android.synthetic.main.dialog_username_picker.view.*
import net.makemoney.android.R
import net.makemoney.android.data.models.profile.ProfileItem
import net.makemoney.android.extensions.RESULT_EMPTY_PHONE_NUMBER
import net.makemoney.android.extensions.consume
import net.makemoney.android.extensions.inflate
import net.makemoney.android.mvp.contracts.ProfileDetailsContract
import net.makemoney.android.mvp.presenters.ProfileDetailsPresenter
import net.makemoney.android.skeleton.activity.BaseActivity
import net.makemoney.android.utils.InstantDialog
import org.jetbrains.anko.toast
import timber.log.Timber


class ProfileAdditionalActivity : BaseActivity<ProfileDetailsPresenter>(), ProfileDetailsContract.View {

    private val apiClient: GoogleApiClient by lazy {
        GoogleApiClient.Builder(this)
                .enableAutoManage(this) { result ->
                    toast(getErrorMessage(result.errorCode))
                    Timber.e("FAILED: ${result.errorMessage}")
                }
                .addApi(Auth.CREDENTIALS_API)
                .build()
    }
    private lateinit var profileItem: net.makemoney.android.data.models.profile.ProfileItem

    override fun getLayoutId(): Int = R.layout.activity_profile_additional

    override fun createPresenter(): ProfileDetailsPresenter = ProfileDetailsPresenter(this)

    override fun initViews() {
        profileItem = intent.getParcelableExtra(EXTRA_PROFILE_DATA)
        if (::profileItem.isInitialized) {
            with(profileItem) {
                try {
                    tvProfileAdditionalEmail.text = email
                    tvProfileAdditionalName.text = username
                    tvProfileAdditionalPhone.text = phone
                    tvProfileAdditionalGender.text = gender
                } catch (e: IllegalStateException) {
                    Timber.i(e.message)
                }
            }
        }
        setHomeButton()
        tvProfileAdditionalName.setOnClickListener { getUsername() }
        tvProfileAdditionalPhone.setOnClickListener { getPhoneNumber() }
        tvProfileAdditionalGender.setOnClickListener { view -> switchGender((view as TextView).text.toString()) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> consume { onBackPressed() }
        else -> super.onOptionsItemSelected(item)
    }

    override fun profileDetailsWasSaved() {
        toast(getString(R.string.profile_details_saved_successfully))
        setResult(Activity.RESULT_OK)
        finish()
    }

    // Obtain the phone number from the result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESOLVE_HINT) {
            if (resultCode == Activity.RESULT_OK) {
                val credential = data?.getParcelableExtra<Parcelable>(Credential.EXTRA_KEY) as Credential
                tvProfileAdditionalPhone.text = credential.id //selected phone number
            }
            else if (resultCode == RESULT_EMPTY_PHONE_NUMBER) toast(getString(R.string.profile_cant_get_phone_number))
        }
    }

    override fun onBackPressed() {
        val user = getUser()
        if (user != null) {
            showSaveDetailsDialog(user)
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun setHomeButton() {
        setSupportActionBar(toolbar_profile_additional)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_profile_additional.setNavigationIcon(R.drawable.ic_toolbar_back)
    }

    private fun getUsername() {
        val dialogView = inflate(R.layout.dialog_username_picker)
        val dialog = InstantDialog(this, dialogView).show()
        dialog?.setCancelable(true)
        with(dialogView) {
            etUsernameDialog.append(tvProfileAdditionalName.text)
            btnUsernameDialogPositive.setOnClickListener {
                tvProfileAdditionalName.text = etUsernameDialog.text.toString()
                dialog?.dismiss()
            }
        }
    }

    /**
     * Use GoogleApi here to avoid adding [android.Manifest.permission.READ_PHONE_STATE] permission
     */
    private fun getPhoneNumber() {
        val hintRequest = HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build()
        val intent = Auth.CredentialsApi.getHintPickerIntent(
                apiClient, hintRequest)
        this.startIntentSenderForResult(intent.intentSender,
                RESOLVE_HINT, null, 0, 0, 0, null)
    }

    private fun switchGender(gender: String = MALE) {
        val newGender = if (gender.contentEquals(MALE)) FEMALE else MALE
        tvProfileAdditionalGender.text = newGender
    }

    private fun getErrorMessage(code: Int): Int = when (code) {
        ConnectionResult.NETWORK_ERROR -> R.string.all_connection_error
        ConnectionResult.CANCELED -> R.string.all_smthing_cancel
        ConnectionResult.INTERRUPTED -> R.string.all_smthing_cancel
        else -> R.string.all_developer_errors
    }

    private fun getUser(): net.makemoney.android.data.models.profile.ProfileItem? {
        val phone = if(tvProfileAdditionalPhone.text.toString().isBlank()) null else tvProfileAdditionalPhone.text.toString()
        return if (::profileItem.isInitialized) {
            val newUser = net.makemoney.android.data.models.profile.ProfileItem(tvProfileAdditionalName.text.toString(), tvProfileAdditionalEmail.text.toString(),
                    phone ?: "", tvProfileAdditionalGender.text.toString())
            if (newUser.compareTo(profileItem) == 0) null else newUser
        } else {
            null
        }
    }

    private fun showSaveDetailsDialog(user: net.makemoney.android.data.models.profile.ProfileItem) {
        AlertDialog.Builder(this)
                .setMessage(R.string.profile_save_dialog_title)
                .setPositiveButton(R.string.profile_save_dialog_btn_ok) { dialog, _ ->
                    dialog.dismiss()
                    saveUser(user)
                }
                .setNegativeButton(R.string.profile_save_dialog_btn_cancel) { dialog, _ ->
                    dialog.dismiss()
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }.create().show()
    }

    private fun saveUser(user: net.makemoney.android.data.models.profile.ProfileItem) = presenter.saveProfileDetails(user)

    companion object {
        const val RESOLVE_HINT = 1561
        const val RC_PROFILE_DETAILS = 4216
        const val EXTRA_PROFILE_DATA = "extra_profile_data_set"
        const val MALE = "male"
        const val FEMALE = "female"
    }
}