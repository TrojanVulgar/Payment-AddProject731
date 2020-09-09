package net.makemoney.android.data.models.entertainment

import com.google.gson.annotations.SerializedName


data class QuizQuestionItem (val id: Int = 0,
                             val question: String = "",
                             val A: String = "A",
                             val B: String = "B",
                             val C: String = "C",
                             val D: String = "D",
                             @SerializedName("correct_answer") val correctAnswer: String = "")