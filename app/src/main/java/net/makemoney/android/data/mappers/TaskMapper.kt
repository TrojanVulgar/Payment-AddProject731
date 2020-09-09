package net.makemoney.android.data.mappers

import net.makemoney.android.data.models.entertainment.GameTaskItem
import net.makemoney.android.data.models.entertainment.QuizTaskItem
import net.makemoney.android.data.models.tasks.OwnTaskItem
import net.makemoney.android.data.models.tasks.PartnerTaskItem
import net.makemoney.android.data.models.tasks.VideoTaskItem
import net.makemoney.android.data.responses.*
import net.makemoney.android.utils.CardBackgroundProvider


/**
 * Transforms server task models into model which used in project
 */
class TaskMapper {

    fun transformTask(data: net.makemoney.android.data.responses.TaskResponseItem): net.makemoney.android.data.models.tasks.OwnTaskItem {
        val item = net.makemoney.android.data.models.tasks.OwnTaskItem(id = data.id, name = data.title, packageName = data.package_name, imageUrl = data.image_url, description = data.description,
                trackingLink = data.tracking_link
                        ?: "", durationInApp = data.duration, award = data.award, dailyAward = data.daily_award,
                days = convertDays(data.days, data.time_delay), daysPassed = (data.pivot?.times
                ?: 0) + (data.pivot?.failed_times ?: 0),
                time_delay = data.time_delay, keywords = data.keywords
                ?: emptyList(), launchDate = data.available_time
                ?: "", rateKeywords = data.rate_keywords ?: emptyList(),
                rateType = data.rate_type, isAvailable = data.pivot?.is_available ?: true,
                isRatingAvailable = data.pivot?.is_rating_available ?: 0)
        item.type = data.type
        return item
    }

    fun transform(data: List<net.makemoney.android.data.responses.TaskResponseItem>): List<net.makemoney.android.data.models.tasks.OwnTaskItem> = data.map {
        val item = net.makemoney.android.data.models.tasks.OwnTaskItem(it.id, it.title, it.package_name, it.image_url, it.description, it.tracking_link
                ?: "", it.duration, it.award, it.daily_award,
                convertDays(it.days, it.time_delay), (it.pivot?.times
                ?: 0) + (it.pivot?.failed_times ?: 0), it.time_delay, it.keywords ?: emptyList(),
                it.available_time ?: "", it.rate_keywords ?: emptyList(),
                it.rate_type,
                it.pivot?.is_available ?: true,
                it.pivot?.is_rating_available ?: 0)
        item.type = it.type
        item
    }

    fun transformPartners(data: List<net.makemoney.android.data.responses.PartnerResponseItem>): List<net.makemoney.android.data.models.tasks.PartnerTaskItem> = data.map {
        net.makemoney.android.data.models.tasks.PartnerTaskItem(it.id, it.title, it.image_url, it.description, it.award, CardBackgroundProvider.getPartnerBackgroundCard())
    }

    fun transformVideos(data: List<net.makemoney.android.data.responses.VideoResponse.VideoItem>): List<net.makemoney.android.data.models.tasks.VideoTaskItem> = data.map {
        net.makemoney.android.data.models.tasks.VideoTaskItem(it.id, it.title, it.award, it.image_url, it.pivot?.is_available
                ?: true, CardBackgroundProvider.getVideoBackgroundCard())
    }

    fun transformQuizzes(data: List<net.makemoney.android.data.responses.QuizzesResponse>): List<net.makemoney.android.data.models.entertainment.QuizTaskItem> = data.map {
        net.makemoney.android.data.models.entertainment.QuizTaskItem(it.id, it.title, it.image_url, it.pivot.today_times, it.pivot.limit, it.pivot.is_available, CardBackgroundProvider.getQuizBackgroundCard(it.id))
    }

    fun transformGames(data: List<net.makemoney.android.data.responses.GameResponseItem>): List<net.makemoney.android.data.models.entertainment.GameTaskItem> = data.map {
        net.makemoney.android.data.models.entertainment.GameTaskItem(it.id, it.title, it.image_url, it.pivot.is_available, CardBackgroundProvider.getGameBackgroundCard(it.id))
    }

    //Cuz days with considering time_delay delay. When time_delay delay 48 -> days = 10, but expected that days = 5
    private fun convertDays(days: Int, timeDelay: Int) = days / (timeDelay / 24)
}