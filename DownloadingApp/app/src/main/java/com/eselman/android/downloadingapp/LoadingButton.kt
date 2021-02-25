package com.eselman.android.downloadingapp

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var bgColor: Int = Color.BLACK
    private var textColor: Int = Color.WHITE

    private var widthSize = 0
    private var heightSize = 0

    @Volatile
    private var buttonAnimationProgress: Double = 0.0

    private var valueAnimator: ValueAnimator

    private var btnText = ""

    private val paint = Paint()

    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        if (new is ButtonState.Completed) {
            hasCompletedDownload()
        }
    }

    private val rect = RectF()
    private val updateListener = ValueAnimator.AnimatorUpdateListener {updatedAnimation ->
        buttonAnimationProgress = (updatedAnimation.animatedValue as Float).toDouble()
        invalidate()
        requestLayout()
    }


    init {
        isClickable = true
        btnText = context.getString(R.string.btn_download_text)

        val attr = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0,
            0
        )
        try {
            bgColor = attr.getColor(
                R.styleable.LoadingButton_bgColor,
                ContextCompat.getColor(context, R.color.colorPrimary)
            )

            textColor = attr.getColor(
                R.styleable.LoadingButton_textColor,
                ContextCompat.getColor(context, R.color.white)
            )
        } finally {
            attr.recycle()
        }

        valueAnimator = AnimatorInflater.loadAnimator(
            context, R.animator.loading_animation
        ) as ValueAnimator
        valueAnimator.addUpdateListener(updateListener)
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.RESTART
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color = bgColor
        val padding = 16
        canvas?.drawRect(padding.toFloat(), 0f, (width - padding).toFloat(), (height-padding).toFloat(), paint)

        if (buttonState == ButtonState.Loading) {
            paint.color = context.getColor(R.color.colorPrimaryDark)
            canvas?.drawRect(
                padding.toFloat(), 0f,
                ((width-padding) * (buttonAnimationProgress / 100)).toFloat(), (height-padding).toFloat(), paint
            )
            paint.color = context.getColor(R.color.colorAccent)
            rect.set(740f, (height / 2).toFloat(), 810f, ((height / 2) + 50).toFloat())
            canvas?.drawArc(rect, 0f, (360 * (buttonAnimationProgress / 100)).toFloat(), true, paint)
            isClickable = false
        } else {
            isClickable = true
        }

        btnText = if (buttonState == ButtonState.Loading) resources.getString(R.string.btn_loading_text) else resources.getString(R.string.btn_download_text)
        paint.color = textColor
        paint.textSize = 48f
        paint.textAlign = Paint.Align.CENTER

        canvas?.drawText(btnText, (width / 2).toFloat(), ((height + 30) / 2).toFloat(),
            paint)
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
        if (buttonState == ButtonState.Completed) buttonState = ButtonState.Loading
        valueAnimator.start()
        return true
    }

    private fun hasCompletedDownload() {
        valueAnimator.cancel()
        invalidate()
        requestLayout()
    }
}
