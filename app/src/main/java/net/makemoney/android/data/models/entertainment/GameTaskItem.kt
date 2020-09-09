package net.makemoney.android.data.models.entertainment

import android.os.Parcel
import net.makemoney.android.R
import net.makemoney.android.data.models.tasks.TaskItem
import net.makemoney.android.extensions.parcelableCreator
import net.makemoney.android.extensions.readBoolean
import net.makemoney.android.extensions.writeBoolean
import net.makemoney.android.utils.Parcelize


data class GameTaskItem(
     override val id: Int,
     val title: String,
     val imageUrl: String,
     val isUnpaidMode: Boolean,
     val background: Int = R.drawable.bg_entertainment_card_1
) : net.makemoney.android.data.models.tasks.TaskItem(), Parcelize {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readBoolean(),
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(imageUrl)
        parcel.writeBoolean(isUnpaidMode)
        parcel.writeInt(background)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::GameTaskItem)
    }
}