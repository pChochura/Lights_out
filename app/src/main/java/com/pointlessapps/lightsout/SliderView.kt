package com.pointlessapps.lightsout

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import com.google.android.material.color.MaterialColors

@SuppressLint("ClickableViewAccessibility")
class SliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var measuredTextHeight: Int = 0

    var progress: Float = 0f
        set(value) {
            field = value
            onValueChangedListener?.invoke(progress)
            invalidate()
        }
    var color: Int = 0
        set(value) {
            field = value
            paint.color = value
            invalidate()
        }
    var colorBackground: Int = 0
        set(value) {
            field = value
            backgroundPaint.color = value
            invalidate()
        }
    var textColor: Int = 0
        set(value) {
            field = value
            textPaint.color = value
            invalidate()
        }
    var text: String? = null
        set(value) {
            field = value
            invalidate()
        }
    var value: String? = null
        set(value) {
            field = value
            invalidate()
        }
    var textSize: Float = 18f.sp
        set(value) {
            field = value
            textPaint.textSize = value
            measuredTextHeight = Rect().also { textPaint.getTextBounds("0", 0, 1, it) }.height()
            invalidate()
        }
    var fontFamily: Int = 0
        set(value) {
            field = value
            textPaint.typeface = ResourcesCompat.getFont(context, field)
            measuredTextHeight = Rect().also { textPaint.getTextBounds("0", 0, 1, it) }.height()
            invalidate()
        }

    private var backgroundPaint = Paint()
    private val paint = Paint()
    private val textPaint = TextPaint()

    private var onValueChangedListener: ((Float) -> Unit)? = null

    private var radius = 0f

    init {
        context.withStyledAttributes(attrs, R.styleable.SliderView, defStyleAttr) {
            text = getString(R.styleable.SliderView_android_text)
            value = getString(R.styleable.SliderView_android_value)
            color = getColor(
                R.styleable.SliderView_android_color,
                MaterialColors.getColor(this@SliderView, R.attr.colorPrimary)
            )
            textColor = getColor(
                R.styleable.SliderView_android_textColor,
                MaterialColors.getColor(this@SliderView, android.R.attr.textColorPrimary)
            )
            colorBackground = getColor(
                R.styleable.SliderView_android_colorBackground,
                MaterialColors.getColor(this@SliderView, R.attr.colorPrimary)
            )
            fontFamily = getResourceId(R.styleable.SliderView_android_fontFamily, R.font.montserrat)
            textSize = getDimension(R.styleable.SliderView_android_textSize, 18f.sp)
            progress = getFloat(R.styleable.SliderView_android_progress, 0f)
            radius = getDimension(R.styleable.SliderView_android_radius, 0f)
        }

        paint.style = Paint.Style.FILL
        backgroundPaint.style = Paint.Style.FILL
        textPaint.style = Paint.Style.FILL

        setOnTouchListener { _, event ->
            if (event.action in arrayOf(MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE)) {
                progress = (event.x / width).coerceIn(0f, 1f)
                return@setOnTouchListener true
            }

            return@setOnTouchListener false
        }
    }

    fun setOnValueChangedListener(onValueChangedListener: (Float) -> Unit) {
        this.onValueChangedListener = onValueChangedListener
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(
            0f, 0f,
            width.toFloat(), height.toFloat(),
            radius, radius,
            backgroundPaint
        )
        canvas.drawRoundRect(
            0f, 0f,
            width * progress, height.toFloat(),
            radius, radius,
            paint
        )

        text?.also {
            textPaint.textAlign = Paint.Align.LEFT
            canvas.drawText(
                it,
                paddingLeft.toFloat(),
                (height + measuredTextHeight) * 0.5f,
                textPaint
            )
        }

        value?.also {
            textPaint.textAlign = Paint.Align.RIGHT
            canvas.drawText(
                it,
                width - paddingLeft.toFloat(),
                (height + measuredTextHeight) * 0.5f,
                textPaint
            )
        }
    }
}