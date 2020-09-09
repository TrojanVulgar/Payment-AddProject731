package net.makemoney.android.screens

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.View
import kotlinx.android.synthetic.main.activity_policy.*
import kotlinx.android.synthetic.main.dialog_simple_info.view.*
import net.makemoney.android.R
import net.makemoney.android.extensions.gone
import net.makemoney.android.extensions.inflate
import net.makemoney.android.skeleton.EmptyPresenter
import net.makemoney.android.skeleton.activity.BaseActivity
import net.makemoney.android.utils.AppPreferences
import net.makemoney.android.utils.InstantDialog
import timber.log.Timber


class PrivacyPolicyActivity : BaseActivity<EmptyPresenter>() {

    override fun getLayoutId(): Int = net.makemoney.android.R.layout.activity_policy

    override fun createPresenter(): EmptyPresenter = EmptyPresenter(this)

    override fun initViews() {
        initText()
        if (AppPreferences.privacyPolicyIsAccepted) {
            privacy_policy_button_accept.gone()
            setHomeButton()
        }
        initButtons()
    }

    override fun onBackPressed() {
        if (!AppPreferences.privacyPolicyIsAccepted)
            showDialog()
        else
            finish()
    }

    private fun setHomeButton() {
        setSupportActionBar(toolbar_policy)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_policy.setNavigationIcon(R.drawable.ic_toolbar_back)
        toolbar_policy.setNavigationOnClickListener {
            if (!AppPreferences.privacyPolicyIsAccepted)
                showDialog()
            else
                finish()
        }
    }

    private fun initButtons() {
        privacy_policy_button_accept.setOnClickListener {
            AppPreferences.privacyPolicyIsAccepted = true
            setResult(Activity.RESULT_OK)
            finish()
        }
        privacy_policy_button_reject.setOnClickListener {
            showDialog()
        }
    }

    private fun initText() {
        val privacyText = SpannableStringBuilder(getString(net.makemoney.android.R.string.privacy_policy))
        //"Privacy policy"
        privacyText.setSpan(StyleSpan(Typeface.BOLD), 0, 14, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        privacyText.setSpan(RelativeSizeSpan(1.7f), 0, 14, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        //"Information Collections and Use"
        privacyText.setSpan(StyleSpan(Typeface.BOLD), 893, 923, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        privacyText.setSpan(RelativeSizeSpan(1.2f), 893, 923, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        //"Cookies"
        privacyText.setSpan(StyleSpan(Typeface.BOLD), 1892, 1899, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        privacyText.setSpan(RelativeSizeSpan(1.2f), 1892, 1899, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        //"Service Providers"
        privacyText.setSpan(StyleSpan(Typeface.BOLD), 2507, 2526, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        privacyText.setSpan(RelativeSizeSpan(1.2f), 2507, 2526, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        //"Security"
        privacyText.setSpan(StyleSpan(Typeface.BOLD), 3028, 3036, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        privacyText.setSpan(RelativeSizeSpan(1.2f), 3028, 3036, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        //"Links to Other Sites"
        privacyText.setSpan(StyleSpan(Typeface.BOLD), 3347, 3367, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        privacyText.setSpan(RelativeSizeSpan(1.2f), 3347, 3367, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        //"Children`s Privacy"
        privacyText.setSpan(StyleSpan(Typeface.BOLD), 3762, 3780, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        privacyText.setSpan(RelativeSizeSpan(1.2f), 3762, 3780, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

        //"Changes to This Privacy Policy"
        privacyText.setSpan(StyleSpan(Typeface.BOLD), 4235, 4265, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        privacyText.setSpan(RelativeSizeSpan(1.2f), 4235, 4265, Spannable.SPAN_INCLUSIVE_INCLUSIVE)


        tvPrivacy.text = privacyText
        tvPrivacy.movementMethod = LinkMovementMethod.getInstance()
        tvPrivacy.highlightColor = Color.TRANSPARENT
    }

    private fun initClickableSpan(link: String): ClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
            } catch (e: android.content.ActivityNotFoundException) {
                Timber.e(e.message)
            }
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

    private fun showDialog() {
        val description = getString(R.string.privacy_policy_reject_description_info)
        val dialogInflate = inflate(R.layout.dialog_simple_info)
        val dialog = InstantDialog(this, dialogInflate).show()
        dialog?.setCancelable(true)
        dialogInflate.apply {
            tv_info_simple_dialog_text.text = description
            btn_info_simple_dialog_ok.setOnClickListener {
                if (AppPreferences.privacyPolicyIsAccepted) {
                    reboot()
                } else {
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
                dialog?.dismiss()
            }
        }
    }
}