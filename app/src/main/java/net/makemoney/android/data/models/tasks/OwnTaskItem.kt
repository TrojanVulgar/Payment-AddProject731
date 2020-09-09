package net.makemoney.android.data.models.tasks

import android.os.Parcel
import net.makemoney.android.R
import net.makemoney.android.extensions.*
import net.makemoney.android.utils.Parcelize


data class OwnTaskItem(override val id: Int = -1,
                       val name: String = "",
                       val packageName: String = "",
                       val imageUrl: String = "",
                       val description: String = "",
                       val trackingLink: String = "",
                       val durationInApp: Int = 0,
                       val award: Float = 0f,
                       val dailyAward: Float = 0f,
                       val days: Int = 0, //duration needed to perform task
                       val daysPassed: Int = 0, //launches already done
                       val time_delay: Int = 24, //hours between each launch
                       val keywords: List<String> = emptyList(),
                       val launchDate: String = "", //date when launch will be active
                       val rateKeywords: List<String> = emptyList(),
                       val rateType: Int = 0,
                       var isAvailable: Boolean = true, //is user can launch task again
                       val isRatingAvailable: Int = 0 //status of rating the task. Is user can rate the task
) : net.makemoney.android.data.models.tasks.TaskItem(), Parcelize {

    var text: String = ""
        get() = appRes.getQuantityString(R.plurals.own_task_description_days, days, days) +
                " " +
                appRes.getQuantityString(R.plurals.own_task_description_hours, time_delay, time_delay)

    var title: String = ""
        get() = getString(R.string.tasks_number, id)

    var type: Int = 0 //0 - DIRECT, 1 - SEARCH
        @net.makemoney.android.data.models.tasks.OwnTaskType get

    private constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readInt(),
            parcel.readFloat(),
            parcel.readFloat(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.createStringArrayList() ?: emptyList<String>(),
            parcel.readString() ?: "",
            parcel.createStringArrayList() ?: emptyList<String>(),
            parcel.readInt(),
            parcel.readBoolean(),
            parcel.readInt()){
        type = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeInt(id)
        writeString(name)
        writeString(packageName)
        writeString(imageUrl)
        writeString(description)
        writeString(trackingLink)
        writeInt(durationInApp)
        writeFloat(award)
        writeFloat(dailyAward)
        writeInt(days)
        writeInt(daysPassed)
        writeInt(time_delay)
        writeStringList(keywords)
        writeString(launchDate)
        writeStringList(rateKeywords)
        writeInt(rateType)
        writeBoolean(isAvailable)
        writeInt(isRatingAvailable)
        writeInt(type)
    }

    companion object {
        @JvmField
        val CREATOR = parcelableCreator(::OwnTaskItem)
    }
}