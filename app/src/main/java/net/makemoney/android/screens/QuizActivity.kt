package net.makemoney.android.screens

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.synthetic.main.activity_quiz.*
import kotlinx.android.synthetic.main.dialog_quiz_complete.view.*
import kotlinx.android.synthetic.main.dialog_simple_info.view.*
import net.makemoney.android.R
import net.makemoney.android.data.models.entertainment.QuizQuestionItem
import net.makemoney.android.data.models.entertainment.QuizTaskItem
import net.makemoney.android.extensions.*
import net.makemoney.android.mvp.contracts.QuizContract
import net.makemoney.android.mvp.presenters.QuizPresenter
import net.makemoney.android.skeleton.activity.BaseActivity
import net.makemoney.android.utils.InstantDialog
import org.jetbrains.anko.toast
import timber.log.Timber


class QuizActivity : BaseActivity<QuizContract.Presenter>(), QuizContract.View {

    private var dialog: AlertDialog? = null
    private lateinit var quizItem: net.makemoney.android.data.models.entertainment.QuizTaskItem

    private var score: Int = 0
    private var earned: Float = 0f
    private var quizUniqueNumber = 0
    private var quizCompleted = false

    private var questionItem: List<net.makemoney.android.data.models.entertainment.QuizQuestionItem> = listOf()
    private var questionIndex: Int = 0
        set(value) {
            field = if (value > 9) -1 else value
        }

    private var correctAnswerOfQuestion: String = ""

    override fun getLayoutId(): Int = net.makemoney.android.R.layout.activity_quiz

    override fun createPresenter(): QuizPresenter = QuizPresenter(this)

    override fun initViews() {
        quizItem = intent.getParcelableExtra(EXTRA_QUIZ_ITEM)
        quizUniqueNumber = quizItem.id
        toolbar_quiz.title = quizItem.title
        setHomeButton()
        if (quizUniqueNumber == 0) {
            toast("Такой викторины не существует")
        } else {
            presenter.getQuizQuestions(quizUniqueNumber)
        }
        showProgressView()
        score_info.setOnClickListener {
            val dialogView = inflate(net.makemoney.android.R.layout.dialog_simple_info)
            dialog = InstantDialog(this, dialogView).show()

            dialogView.tv_info_simple_dialog_text.text = applicationContext.getString(net.makemoney.android.R.string.quiz_how_to_get_award_info)
            dialogView.btn_info_simple_dialog_ok.setOnClickListener {
                dialog?.dismiss()
            }
        }
        profit_text.text = appContext.getString(net.makemoney.android.R.string.quiz_earned, earned.asPrice(false))
        score_text.text = appContext.getString(net.makemoney.android.R.string.quiz_correct_answer, score)
    }

    override fun fetchedQuizzes(quizUniqueNumber: Int, items: List<net.makemoney.android.data.models.entertainment.QuizQuestionItem>) {
        if (items.isEmpty()) {
            toast("No questions")
            return
        }
        questionItem = items
        questionIndex = 0
        setQuestionData(items[questionIndex])
        hideProgressView()
    }

    override fun onPause() {
        super.onPause()
        Timber.i("onPause")
        finishActivity()
    }

    private fun setQuestionData(item: net.makemoney.android.data.models.entertainment.QuizQuestionItem) {
        with(item) {
            tvQuizCondition.text = question
            correctAnswerOfQuestion = correctAnswer
            answer_A.text = A
            answer_B.text = B
            answer_C.text = C
            answer_D.text = D
        }
        setClickListeners()
    }

    private fun setHomeButton() {
        setSupportActionBar(toolbar_quiz)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_quiz.setNavigationIcon(R.drawable.ic_toolbar_back)
        toolbar_quiz.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setClickListeners() {
        answer_A.setOnClickListener {
            checkAnswer(answer_A.text.toString(), it)
        }
        answer_B.setOnClickListener {
            checkAnswer(answer_B.text.toString(), it)
        }
        answer_C.setOnClickListener {
            checkAnswer(answer_C.text.toString(), it)
        }
        answer_D.setOnClickListener {
            checkAnswer(answer_D.text.toString(), it)
        }
    }

    private fun checkAnswer(answer: String, view: View) {
        quizCompleted = true
        if (answer.toLowerCase() == correctAnswerOfQuestion.toLowerCase()) {
            score++
            score_text.text = appContext.getString(net.makemoney.android.R.string.quiz_correct_answer, score)
            if (score >= 5) {
                earned = 5 / 10f
            }
            if (score == 10) {
                earned = 10 / 10f
            }
            profit_text.text = appContext.getString(net.makemoney.android.R.string.quiz_earned, earned.asPrice(false))
            answerAnimation(getColor(source = net.makemoney.android.R.color.colorQuizCorrectAnswer), R.drawable.ic_quiz_correct, quiz_answers_background)
        } else {
            answerAnimation(getColor(source = net.makemoney.android.R.color.colorQuizIncorrectAnswer), R.drawable.ic_quiz_uncorrect, quiz_answers_background)
        }
        questionIndex++
        when (questionIndex) {
            -1 -> {
                val dialogProfit = appContext.getString(net.makemoney.android.R.string.quiz_dialog_earned, earned.asPrice(false))
                val dialogEarned = appContext.getString(net.makemoney.android.R.string.quiz_dialog_correct_answers, score)

                val dialogView = inflate(net.makemoney.android.R.layout.dialog_quiz_complete)
                dialog = InstantDialog(this, dialogView).show()

                dialogView.tv_quiz_dialog_info_profit.text = dialogProfit
                dialogView.tv_quiz_dialog_info_score.text = dialogEarned
                dialogView.btn_quiz_dialog_ok.setOnClickListener {
                    finish()
                }
            }
            else -> {
                if (questionIndex > 6) {
                    setResult(Activity.RESULT_OK)
                } else {
                    setResult(Activity.RESULT_CANCELED)
                }
                setQuestionData(questionItem[questionIndex])
            }
        }
    }

    private fun finishActivity() {
        Timber.i("finishActivity: $questionIndex")
        dialog?.dismiss()
        if (quizCompleted) {
            presenter.quizCompleted(quizUniqueNumber, score)
        }
        finish()
    }

    private fun answerAnimation(color: Int, image: Int, view: FrameLayout) {
        quiz_answers_background.setBackgroundColor(color)
        ivAnswer.setImageResource(image)
        AnimatorSet().apply {
            playSequentially(
                    showAnimatorSet(view),
                    hideAnimatorSet(view))
            addListener(getAnimationListener(view))
            start()
        }
    }

    private fun getAnimationListener(view: FrameLayout): AnimatorListenerAdapter = object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            super.onAnimationStart(animation)
            view.visible()
        }

        override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            view.gone()
        }
    }

    private fun showAnimatorSet(view: View): AnimatorSet = AnimatorSet().apply {
        setDuration(DURATION).playTogether(ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f))
    }

    private fun hideAnimatorSet(view: View): AnimatorSet = AnimatorSet().apply {
        setDuration(DURATION).playTogether(ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f))
    }

    companion object {
        const val EXTRA_QUIZ_ITEM = "quiz_item"
        const val RC_QUIZ_ACTIVITY = 15769
        const val DURATION = 250L
    }
}