package net.makemoney.android.data.models.marathon

import android.os.Parcel
import net.makemoney.android.extensions.parcelableCreator
import net.makemoney.android.utils.Parcelize


data class MarathonItem(
        val id: Int,
        val current: Int,
        val max: Int,
        var isAvailable: Int, // 0 - unavailable, 1 - available, 2 - done
        val award: List<Float> = listOf()
) : Parcelize {

    private constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            arrayListOf<Float>().apply {
                parcel.readList(this, Float::class.java.classLoader)
            }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeInt(id)
        writeInt(current)
        writeInt(max)
        writeInt(isAvailable)
        writeList(award)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::MarathonItem)
    }
}