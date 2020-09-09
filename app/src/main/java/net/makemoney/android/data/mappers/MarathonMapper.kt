package net.makemoney.android.data.mappers

import net.makemoney.android.data.models.marathon.MarathonItem
import net.makemoney.android.data.responses.MarathonResponseItem


class MarathonMapper {

    fun transformTo(data: net.makemoney.android.data.responses.MarathonResponseItem): net.makemoney.android.data.models.marathon.MarathonItem = with(data) {
        net.makemoney.android.data.models.marathon.MarathonItem(id, current, max, status, award)
    }
}