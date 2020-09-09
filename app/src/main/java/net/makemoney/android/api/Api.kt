package net.makemoney.android.api

import net.makemoney.android.data.models.profile.ProfileItem
import net.makemoney.android.data.models.withdrawal.WithdrawalBuyCardItem
import net.makemoney.android.data.models.withdrawal.WithdrawalUseCardItem
import net.makemoney.android.data.responses.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

/**
 * Contains all server requests
 */
interface Api {

    //  Login

    @POST("v1/user/login")
    @FormUrlEncoded
    fun auth(@Field("username") name: String,
             @Field("email") email: String,
             @Field("country_id") countryId: Int): Call<net.makemoney.android.data.responses.AuthResponse>

    @GET("v1/user/countries")
    fun getCountries(@Query("email") email: String): Call<net.makemoney.android.data.responses.GetCountriesResponse>

    @GET("v1/user/code-activate")
    fun registerPromo(@Query("promo_code") promo: String): Call<net.makemoney.android.data.responses.EmptyResponse>

    @GET("v1/user/fcm")
    fun setFCMToken(@Query("fcm_token") fcmToken: String): Call<net.makemoney.android.data.responses.EmptyResponse>

    //  Profile

    @GET("v1/balance")
    fun getBalance(): Call<Float>

    @GET("v1/profile")
    fun getProfileData(): Call<net.makemoney.android.data.responses.ProfileResponse>

    @POST("v1/profile")
    fun saveProfileDetails(@Body profileItem: net.makemoney.android.data.models.profile.ProfileItem): Call<net.makemoney.android.data.responses.EmptyResponse>

    //  Referral

    @GET("v1/balance/referral")
    fun getReferralBalanceDetails(): Call<net.makemoney.android.data.responses.ReferralResponse>

    //  Balance

    @GET("v1/balance/details")
    fun getBalanceDetails(): Call<net.makemoney.android.data.responses.BalanceDetailsResponse>

    //  Withdrawal

    @GET("v1/balance/methods")
    fun getWithdrawalNewCards(): Call<net.makemoney.android.data.responses.WithdrawalNewCardsResponse>

    @GET("v1/balance/inventory")
    fun getWithdrawalInventoryCards(): Call<List<net.makemoney.android.data.responses.WithdrawalInventoryResponse>>

    @POST("v1/balance/buy")
    fun buyCard(@Body card: net.makemoney.android.data.models.withdrawal.WithdrawalBuyCardItem): Call<net.makemoney.android.data.responses.EmptyResponse>

    @POST("v1/balance/use")
    fun useCard(@Body card: net.makemoney.android.data.models.withdrawal.WithdrawalUseCardItem): Call<net.makemoney.android.data.responses.EmptyResponse>

    //  Marathon

    @GET("v1/safe/progress")
    fun getMarathon(): Call<net.makemoney.android.data.responses.MarathonResponseItem>

    @GET("v1/safe/checkpoint")
    fun takeMarathonAward(@Query("id") id: Int): Call<Float>

    // Tasks

    @GET("v1/tasks/new")
    fun getNewTasks(): Call<List<net.makemoney.android.data.responses.TaskResponseItem>>

    @GET("v1/tasks/get")
    fun getActiveTasks(): Call<List<net.makemoney.android.data.responses.TaskResponseItem>>

    @GET("v1/partners")
    fun getPartnersTasks(): Call<List<net.makemoney.android.data.responses.PartnerResponseItem>>

    /**
     * Used to send callback to server that user installed task from our application
     */
    @GET("v1/tasks/install")
    fun taskInstalledFromOurApp(@Query("task_id") taskId: Int): Call<net.makemoney.android.data.responses.EmptyResponse>

    @GET("v1/tasks/update")
    fun taskCompleted(@Query("task_id") taskId: Int): Call<net.makemoney.android.data.responses.TaskResponseItem>

    @Multipart
    @POST("v1/tasks/review")
    fun sendScreenshot(@Query("task_id") taskId: Int,
                       @Part("screen\";filename=\"pp.png") screen: RequestBody): Call<net.makemoney.android.data.responses.EmptyResponse>

    //  Entertainment

    @GET("v1/quizzes/get")
    fun getQuizzes(): Call<List<net.makemoney.android.data.responses.QuizzesResponse>>

    @GET("v1/quizzes/questions/get")
    fun getQuizzesQuestions(@Query("id") quizUniqueNumber: Int) : Call<net.makemoney.android.data.responses.QuizQuestionsResponseItem>

    @GET("v1/quizzes/update")
    fun quizCompleted(@Query("id") quizUniqueNumber: Int,
                      @Query("score") score: Int) : Call<net.makemoney.android.data.responses.EmptyResponse>

    @GET("v1/games/get")
    fun getGames(): Call<List<net.makemoney.android.data.responses.GameResponseItem>>

    @GET("v1/games/update")
    fun updateGame(@Query("id") gameId: Int,
                   @Query("score") score: Float) : Call<net.makemoney.android.data.responses.BalanceSimple>

    //  Videos

    @GET("v1/videos")
    fun getVideos(): Call<net.makemoney.android.data.responses.VideoResponse>

    @GET("v1/videos/update")
    fun getVideoReward(@Query("id") id: Int): Call<net.makemoney.android.data.responses.EmptyResponse>
}
