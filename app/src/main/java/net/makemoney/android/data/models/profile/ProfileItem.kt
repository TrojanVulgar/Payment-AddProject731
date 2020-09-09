package net.makemoney.android.data.models.profile

import android.os.Parcel
import net.makemoney.android.extensions.parcelableCreator
import net.makemoney.android.utils.Parcelize


data class ProfileItem(
        val username: String,
        val email: String,
        val phone: String,
        val gender: String
) : Parcelize, Comparable<net.makemoney.android.data.models.profile.ProfileItem> {

    override fun compareTo(other: net.makemoney.android.data.models.profile.ProfileItem): Int = if (username == other.username && email == other.email
            && phone == other.phone && gender == other.gender) {
        0
    } else {
        -1
    }

    private constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(phone)
        parcel.writeString(gender)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::ProfileItem)
    }
}