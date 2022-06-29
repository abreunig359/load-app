package com.udacity

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    // custom attributes
    private var defaultBackgroundColor: Int = 0
    private var progressBarColor: Int = 0
    private var progressCircleColor: Int = 0
    private var progressCircleSize: Float = 0f
    private var defaultText: String = ""
    private var progressBarText: String = ""

    private val valueAnimator = ValueAnimator()

    private var currentBackgroundColor: Int
    private var textColor: Int = 0

    var currentText: String = resources.getString(R.string.download)

    var progressBarEnd = 0f
    var progressCircleEnd = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Clicked -> onButtonStateClicked()
            ButtonState.Loading -> onButtonStateLoading()
            ButtonState.Completed -> onButtonStateCompleted()
        }
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultBackgroundColor = getColor(R.styleable.LoadingButton_defaultBackgroundColor, 0)
            defaultText = getString(R.styleable.LoadingButton_defaultText) ?: ""
            progressBarColor = getColor(R.styleable.LoadingButton_progressBarColor, 0)
            progressCircleColor = getColor(R.styleable.LoadingButton_progressCircleColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            progressBarText = getString(R.styleable.LoadingButton_progressBarText) ?: ""
            progressCircleSize = getDimension(R.styleable.LoadingButton_progressCircleSize, 30.0f)
        }
        currentBackgroundColor = defaultBackgroundColor
        currentText = defaultText
        isClickable = true
    }

    private fun onButtonStateClicked() {
        isClickable = false
        Toast.makeText(
            this.context,
            R.string.select_download_file_message,
            Toast.LENGTH_SHORT
        ).show()
        changeButtonState(ButtonState.Completed)
    }

    private fun onButtonStateCompleted() {
        paint.color = defaultBackgroundColor
        currentText = defaultText
        isClickable = true
    }

    private fun onButtonStateLoading() {
        currentText = progressBarText
        isClickable = false
        val progressBarAnimator = animateProgressBar()
        val progressCircleAnimator = animateProgressCircle()
        val animatorSet = AnimatorSet().apply {
            playTogether(progressBarAnimator, progressCircleAnimator)
        }
        animatorSet.start()
    }

    private fun animateProgressBar(): Animator {
        val animator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
        animator.addUpdateListener {
            progressBarEnd = it.animatedValue as Float
            invalidate()
        }
        animator.duration = LOADING_ANIMATION_DURATION_MS
        return animator
    }

    private fun animateProgressCircle(): Animator {
        val animator = ValueAnimator.ofFloat(0f, 360f)
        animator.addUpdateListener {
            progressCircleEnd = it.animatedValue as Float
            invalidate()
        }
        animator.duration = LOADING_ANIMATION_DURATION_MS
        return animator
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(currentBackgroundColor)
        if (buttonState == ButtonState.Loading) {
            canvas.drawProgressBar()
            canvas.drawProgressCircle()
        }
        canvas.drawCurrentText()
    }

    private fun Canvas.drawCurrentText() {
        paint.color = textColor
        val xCenter = width / 2f
        val yCenter = (height / 2 - (paint.descent() + paint.ascent()) / 2)

        drawText(currentText, xCenter, yCenter, paint)
    }

    private fun Canvas.drawProgressBar() {
        paint.color = progressBarColor
        drawRect(0f, 0f, progressBarEnd, height.toFloat(), paint)
    }

    private fun Canvas.drawProgressCircle() {
        paint.color = progressCircleColor
        val circleCoordinates = RectF(
            width.toFloat() * 0.75F - progressCircleSize,
            height.toFloat() / 2 - progressCircleSize,
            width.toFloat() * 0.75F + progressCircleSize,
            height.toFloat() / 2 + progressCircleSize,
        )

        drawArc(
            circleCoordinates,
            0.0f,
            progressCircleEnd,
            true,
            paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun performClick(): Boolean {
        super.performClick()
        Log.i("LoadingButton", "performClick called")
        invalidate()
        return true
    }

    fun changeButtonState(newState: ButtonState) {
        if (buttonState != newState) {
            buttonState = newState
            invalidate()
        }
    }

    companion object {

        private const val LOADING_ANIMATION_DURATION_MS = 2000L
    }
}