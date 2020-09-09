package net.makemoney.android.data.responses

import com.google.gson.annotations.SerializedName
import net.makemoney.android.data.models.entertainment.QuizQuestionItem


data class QuizQuestionsResponseItem(@SerializedName("quiz_id")val quizUniqueNumber: Int = 0,
                                     @SerializedName("questions")val items: List<net.makemoney.android.data.models.entertainment.QuizQuestionItem> = listOf())