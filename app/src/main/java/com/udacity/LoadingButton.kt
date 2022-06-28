package com.udacity

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var defaultBackgroundColor: Int = 0
    private var progressBarColor: Int = 0
    private var defaultText: String = ""
    private var progressBarText: String = ""

    private val valueAnimator = ValueAnimator()

    private var currentBackgroundColor: Int
    private var textColor: Int = 0

    var currentText: String = resources.getString(R.string.download)

    var progressBarEnd = 0f

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> onButtonStateLoading()
            ButtonState.Completed -> onButtonStateCompleted()
        }
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            defaultBackgroundColor = getColor(R.styleable.LoadingButton_defaultBackgroundColor, 0)
            defaultText = getString(R.styleable.LoadingButton_defaultText) ?: ""
            progressBarColor = getColor(R.styleable.LoadingButton_progressBarColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            progressBarText = getString(R.styleable.LoadingButton_progressBarText) ?: ""
        }
        currentBackgroundColor = defaultBackgroundColor
        currentText = defaultText
        isClickable = true
    }

    private fun onButtonStateLoading() {
        currentText = progressBarText
        val animator = animateProgressBar()
        animator.start()
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

    private fun onButtonStateCompleted() {
        paint.color = defaultBackgroundColor
        currentText = defaultText
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(currentBackgroundColor)
        if (buttonState == ButtonState.Loading) {
            canvas.drawProgressBar()
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
        private const val LOADING_ANIMATION_DURATION_MS = 1000L
    }
}