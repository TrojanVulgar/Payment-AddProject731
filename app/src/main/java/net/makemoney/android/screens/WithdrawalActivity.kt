package net.makemoney.android.screens

import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.*
import android.text.style.ImageSpan
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_withdrawal.*
import kotlinx.android.synthetic.main.dialog_withdrawal_buying.view.*
import kotlinx.android.synthetic.main.dialog_withdrawal_using.view.*
import net.makemoney.android.App
import net.makemoney.android.R
import net.makemoney.android.adapters.WithdrawalInventoryCardsAdapter
import net.makemoney.android.adapters.WithdrawalNewCardsAdapter
import net.makemoney.android.data.models.withdrawal.WithdrawalCardType.*
import net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem
import net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem
import net.makemoney.android.data.models.withdrawal.WithdrawalUseCardItem
import net.makemoney.android.extensions.*
import net.makemoney.android.mvp.contracts.WithdrawalContract
import net.makemoney.android.mvp.presenters.WithdrawalPresenter
import net.makemoney.android.skeleton.activity.BaseActivity
import net.makemoney.android.utils.AppPreferences
import net.makemoney.android.utils.InstantDialog
import net.makemoney.android.utils.decoration.GridItemDecoration
import net.makemoney.android.utils.decoration.HorizontalItemDecoration
import net.makemoney.android.utils.helpers.HorizontalSnapHelper
import org.jetbrains.anko.toast


class WithdrawalActivity : BaseActivity<WithdrawalPresenter>(), WithdrawalContract.View {

    private var rate = 0f
    private var nominalList = listOf<Float>()
    private var nominal = 0f
    private var dialog: AlertDialog? = null

    private val newCardsAdapter = net.makemoney.android.adapters.WithdrawalNewCardsAdapter(arrayListOf()) {
        showBuyingDialog(it as net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem)
    }.apply {
        addLoadingFooter()
    }
    private val inventoryCardsAdapter = net.makemoney.android.adapters.WithdrawalInventoryCardsAdapter(arrayListOf()) {
        showUsingCardDialog(it as net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem)
    }.apply {
        addLoadingFooter()
    }

    override fun getLayoutId(): Int = R.layout.activity_withdrawal

    override fun createPresenter(): WithdrawalPresenter = WithdrawalPresenter(this)

    override fun initViews() {
        setHomeButton()
        initInfo()
        initRecyclers()
        presenter.getNewCards()
        presenter.getInventoryCards()
        tvCardInInventory.text = getString(R.string.withdrawal_card_in_inventory, inventoryCardsAdapter.getItemsCount())
    }

    override fun retrieveNewCards(newCards: List<net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem>, nominals: List<Float>, rate: Float) {
        newCardsAdapter.addItems(newCards)
        nominalList = nominals
        this.rate = rate
    }

    override fun retrieveInventoryCards(inventoryCards: List<net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem>) {
        inventoryCardsAdapter.addItems(inventoryCards)
        tvCardInInventory.text = getString(R.string.withdrawal_card_in_inventory, inventoryCardsAdapter.getItemsCount())
    }

    override fun cardBought() {
        toast("Покупка прошла успешно")
        hideProgressView()
        inventoryCardsAdapter.clearItems()
        inventoryCardsAdapter.addLoadingFooter()
        presenter.getInventoryCards()
    }

    override fun cardUsed() {
        toast("Заявка на вывод денег успешно отправлена")
        hideProgressView()
        inventoryCardsAdapter.clearItems()
        inventoryCardsAdapter.addLoadingFooter()
        presenter.getInventoryCards()
    }

    private fun initRecyclers() {
        val snapHelper = HorizontalSnapHelper()
        rvNewCards.layoutManager = LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false)
        rvNewCards.addItemDecoration(HorizontalItemDecoration())
        rvNewCards.setHasFixedSize(true)
        snapHelper.attachToRecyclerView(rvNewCards)
        rvNewCards.adapter = newCardsAdapter

        rvInventoryCards.layoutManager = GridLayoutManager(this, 2)
        rvInventoryCards.itemAnimator = DefaultItemAnimator()
        rvInventoryCards.setHasFixedSize(true)
        rvInventoryCards.addItemDecoration(GridItemDecoration())
        rvInventoryCards.adapter = inventoryCardsAdapter
    }

    private fun initInfo() {
        tvInfoDescription.text = getString(R.string.info_withdrawal_description)
        ivWithdrawalInfo.setOnClickListener {
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
        setSupportActionBar(toolbar_withdrawal)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_withdrawal.setNavigationIcon(R.drawable.ic_toolbar_back)
        toolbar_withdrawal.setNavigationOnClickListener {
            finish()
        }
    }

    private fun showBuyingDialog(card: net.makemoney.android.data.models.withdrawal.WithdrawalNewCardItem) {
        nominal = nominalList[0]
        val dialogView = inflate(R.layout.dialog_withdrawal_buying)
        dialog = InstantDialog(this, dialogView).show()
        dialog?.setCancelable(true)
        with(dialogView) {
            updateBtnTitle(this)
            ivLogoBuying.setImageResource(card.logo)
            backgroundColorBuying.backgroundTintList = ContextCompat.getColorStateList(this@WithdrawalActivity, card.bgColor)
            tvTitleBuying.text = card.title
            tvNominalFirst.text = getString(R.string.withdrawal_dialog_nominal, nominalList[0].toBalance())
            tvNominalSecond.text = getString(R.string.withdrawal_dialog_nominal, nominalList[1].toBalance())
            tvNominalThird.text = getString(R.string.withdrawal_dialog_nominal, nominalList[2].toBalance())
            tvNominalFourth.text = getString(R.string.withdrawal_dialog_nominal, nominalList[3].toBalance())
            ivCloseBuying.setOnClickListener {
                dialog?.dismiss()
            }
            btnPositiveBuying.setOnClickListener {
                presenter.buyCard(card, nominal)
                showProgressView()
                dialog?.dismiss()
            }
        }
        onCheckboxClicked(dialogView)
    }

    private fun onCheckboxClicked(view: View) {
        with(view) {
            nominalFirst.setOnClickListener {
                cbNominalFirst.isChecked = true
                cbNominalSecond.isChecked = false
                cbNominalThird.isChecked = false
                cbNominalFourth.isChecked = false
                nominal = nominalList[0]
                updateBtnTitle(view)
            }
            nominalSecond.setOnClickListener {
                cbNominalFirst.isChecked = false
                cbNominalSecond.isChecked = true
                cbNominalThird.isChecked = false
                cbNominalFourth.isChecked = false
                nominal = nominalList[1]
                updateBtnTitle(view)
            }
            nominalThird.setOnClickListener {
                cbNominalFirst.isChecked = false
                cbNominalSecond.isChecked = false
                cbNominalThird.isChecked = true
                cbNominalFourth.isChecked = false
                nominal = nominalList[2]
                updateBtnTitle(view)
            }
            nominalFourth.setOnClickListener {
                cbNominalFirst.isChecked = false
                cbNominalSecond.isChecked = false
                cbNominalThird.isChecked = false
                cbNominalFourth.isChecked = true
                nominal = nominalList[3]
                updateBtnTitle(view)
            }
        }
    }

    private fun updateBtnTitle(view: View) {
        val btnTitle = SpannableStringBuilder(getString(R.string.withdrawal_dialog_btn_title, (nominal * rate).toBalanceSpanEdition(false)))
        btnTitle.setSpan(ImageSpan(net.makemoney.android.App.context, R.drawable.ic_coin_dark), btnTitle.length - 1, btnTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        view.btnPositiveBuying.setText(btnTitle, TextView.BufferType.SPANNABLE)
    }

    private fun showUsingCardDialog(card: net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem) {
        val dialogView = inflate(R.layout.dialog_withdrawal_using)
        dialog = InstantDialog(this, dialogView).show()
        dialog?.setCancelable(true)
        with (dialogView) {
            tvAmountUsing.text = card.amount.toBalance()
            backgroundColorUsing.backgroundTintList = ContextCompat.getColorStateList(this@WithdrawalActivity, card.bgColor)
            ivLogoUsing.setImageResource(card.logo)
            tvTitleUsing.text = card.title
            ivCloseUsing.setOnClickListener {
                dialog?.dismiss()
            }
        }
        when (card.id) {
            BEELINE -> setBoughtCardDialogInfoForNumbers(dialogView, card)
            MEGAFON -> setBoughtCardDialogInfoForNumbers(dialogView, card)
            MTS -> setBoughtCardDialogInfoForNumbers(dialogView, card)
            PAYPAL -> setBoughtCardDialogInfoForEmail(dialogView, card)
            QIWI -> setBoughtCardDialogInfoForNumbers(dialogView, card)
            STEAM -> setBoughtCardDialogInfoForSteam(dialogView, card)
            TELE2 -> setBoughtCardDialogInfoForNumbers(dialogView, card)
            VK -> setBoughtCardDialogInfoForVK(dialogView, card)
            WARFACE -> setBoughtCardDialogInfoForWarface(dialogView, card)
            WEBMONEY -> setBoughtCardDialogInfoForWebMoney(dialogView, card)
            WOT -> setBoughtCardDialogInfoForEmail(dialogView, card)
            YANDEX -> setBoughtCardDialogInfoForYandexMoney(dialogView, card)
            else -> {
                dialog?.dismiss()
                toast("Ошибка, обратитесь к центру поддержки")
            }
        }
    }

    private fun setBoughtCardDialogInfoForNumbers(view: View, card: net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem) {
        with(view) {
            val maxLength = 20 //with plus on the start
            val minLength = 5
            etDataUsing.append(AppPreferences.transactionPhoneNumber)
            etDataUsing.inputType = InputType.TYPE_CLASS_PHONE
            etDataUsing.hint = getString(R.string.bought_card_number_hint)
            etDataUsing.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            etDataUsing.onTextChange { changedText, _, _, count ->
                if (count > 0) {
                    if (!changedText.toString().startsWith("+")) {
                        val text = etDataUsing.text.toString()
                        val textWithPlus = "+$text"
                        etDataUsing.setText(textWithPlus)
                        Selection.setSelection(etDataUsing.text, etDataUsing.text.length)
                    }
                }
            }
            btnPositiveUsing.setOnClickListener {
                if (etDataUsing.text.isNotEmpty() && etDataUsing.text.toString().length > minLength) {
                    val dataResult = etDataUsing.text.toString()
                    AppPreferences.transactionPhoneNumber = dataResult
                    presenter.useCard(net.makemoney.android.data.models.withdrawal.WithdrawalUseCardItem(card.ud, dataResult))
                    showProgressView()
                    dialog?.dismiss()
                } else {
                    toast(getString(R.string.bought_card_empty_dialog_et))
                }
            }
        }
    }

    private fun setBoughtCardDialogInfoForWebMoney(view: View, card: net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem) {
        with(view) {
            val maxLength = 13 //12 numbers with R on the start
            etDataUsing.append(AppPreferences.transactionWebMoney)
            etDataUsing.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            etDataUsing.hint = getString(R.string.bought_card_webmoney_hint)
            etDataUsing.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            etDataUsing.onTextChange { _, start, _, count ->
                if (count > 0) {
                    if (start == 1) {
                        val text = etDataUsing.text.toString()
                        etDataUsing.setText(text.toUpperCase())
                        Selection.setSelection(etDataUsing.text, etDataUsing.text.length)
                    }
                    etDataUsing.inputType = android.text.InputType.TYPE_CLASS_PHONE
                } else if (start == 0) {
                    etDataUsing.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                }
            }
            btnPositiveUsing.setOnClickListener {
                if (etDataUsing.text.isNotEmpty() && etDataUsing.text.toString().length == maxLength) {
                    val dataResult = etDataUsing.text.toString()
                    AppPreferences.transactionWebMoney = dataResult
                    presenter.useCard(net.makemoney.android.data.models.withdrawal.WithdrawalUseCardItem(card.ud, dataResult))
                    showProgressView()
                    dialog?.dismiss()
                } else {
                    toast(getString(R.string.bought_card_empty_dialog_et))
                }
            }
        }
    }

    private fun setBoughtCardDialogInfoForYandexMoney(view: View, card: net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem) {
        with(view) {
            val maxLength = 15 //15 numbers
            etDataUsing.append(AppPreferences.transactionYandexMoney)
            etDataUsing.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            etDataUsing.hint = getString(R.string.bought_card_yandexmoney_hint)
            etDataUsing.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            btnPositiveUsing.setOnClickListener {
                if (etDataUsing.text.isNotEmpty() && etDataUsing.text.toString().length == maxLength) {
                    val dataResult = etDataUsing.text.toString()
                    AppPreferences.transactionYandexMoney = dataResult
                    presenter.useCard(net.makemoney.android.data.models.withdrawal.WithdrawalUseCardItem(card.ud, dataResult))
                    showProgressView()
                    dialog?.dismiss()
                } else {
                    toast(getString(R.string.bought_card_empty_dialog_et))
                }
            }
        }
    }

    private fun setBoughtCardDialogInfoForEmail(view: View, card: net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem) {
        with(view) {
            val maxLength = 120
            etDataUsing.append(AppPreferences.transactionEmail)
            etDataUsing.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            etDataUsing.hint = getString(R.string.bought_card_email_hint)
            etDataUsing.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            setSimplePrimaryButtonListener(view, card)
        }
    }

    private fun setBoughtCardDialogInfoForWarface(view: View, card: net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem) {
        with(view) {
            val maxLength = 120
            etDataUsing.append(AppPreferences.transactionEmail)
            etDataUsing.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            etDataUsing.hint = getString(R.string.bought_card_warface_hint)
            etDataUsing.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            setSimplePrimaryButtonListener(view, card)
        }
    }

    private fun setBoughtCardDialogInfoForSteam(view: View, card: net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem) {
        with(view) {
            val maxLength = 120
            etDataUsing.append(AppPreferences.transactionSteam)
            etDataUsing.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            etDataUsing.hint = getString(R.string.bought_card_steam_hint)
            etDataUsing.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            setSimplePrimaryButtonListener(view, card)
        }
    }

    private fun setBoughtCardDialogInfoForVK(view: View, card: net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem) {
        with(view) {
            val maxLength = 120
            etDataUsing.append(AppPreferences.transactionVk)
            etDataUsing.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            etDataUsing.hint = getString(R.string.bought_card_vk_hint)
            etDataUsing.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
            setSimplePrimaryButtonListener(view, card)
        }
    }

    private fun setSimplePrimaryButtonListener(view: View, card: net.makemoney.android.data.models.withdrawal.WithdrawalInventoryCardItem) {
        with(view) {
            btnPositiveUsing.setOnClickListener {
                if (etDataUsing.text.isNotEmpty()) {
                    val dataResult = etDataUsing.text.toString()
                    when (card.id) {
                        VK -> AppPreferences.transactionVk = dataResult
                        WARFACE -> AppPreferences.transactionWarface = dataResult
                        STEAM -> AppPreferences.transactionSteam = dataResult
                        PAYPAL -> AppPreferences.transactionEmail = dataResult
                        WOT -> AppPreferences.transactionEmail = dataResult
                    }
                    presenter.useCard(net.makemoney.android.data.models.withdrawal.WithdrawalUseCardItem(card.ud, dataResult))
                    showProgressView()
                    dialog?.dismiss()
                } else {
                    toast(getString(R.string.bought_card_empty_dialog_et))
                }
            }
        }
    }
}