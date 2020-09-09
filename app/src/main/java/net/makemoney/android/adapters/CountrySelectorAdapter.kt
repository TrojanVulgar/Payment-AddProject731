package net.makemoney.android.adapters

import android.content.res.Configuration
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import net.makemoney.android.data.models.country.CountryItem
import net.makemoney.android.extensions.appRes
import net.makemoney.android.extensions.getColor
import net.makemoney.android.extensions.getDimens
import net.makemoney.android.extensions.getFont
import java.util.*




/**
 * This adapter created for displaying countries in spinner view during registration
 * in [net.makemoney.android.screens.StartActivity]
 */
class CountrySelectorAdapter(private val items: List<net.makemoney.android.data.models.country.CountryItem>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var countryName: TextView? = convertView as? TextView
        if (convertView == null) {
            countryName = TextView(parent?.context)
            countryName.setPadding(getDimens(net.makemoney.android.R.dimen.tiny_layout_margin), getDimens(net.makemoney.android.R.dimen.small_view_padding), 0, getDimens(net.makemoney.android.R.dimen.small_view_padding))
            countryName.setTextSize(TypedValue.COMPLEX_UNIT_SP, if ((appRes.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) != Configuration.SCREENLAYOUT_SIZE_XLARGE) 16F else 22F)
            countryName.typeface = getFont(net.makemoney.android.R.font.font_medium)
            countryName.setTextColor(getColor(net.makemoney.android.R.color.colorWhite))
        }
        countryName?.text = if (getCurrentLocale().toLanguageTag().contains("en")) {
            items[position].nameEn
        } else {
            items[position].nameRu
        }
        return countryName
    }

    override fun getItem(position: Int): Any? = null

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = items.size

    private fun getCurrentLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            appRes.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            appRes.configuration.locale
        }
    }
}