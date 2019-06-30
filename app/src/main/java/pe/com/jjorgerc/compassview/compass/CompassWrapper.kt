package pe.com.jjorgerc.compassview.compass

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import pe.com.jjorgerc.compassview.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CompassWrapper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var mIndicatorStep = resources.getInteger(R.integer.default_indicator_step)
    private var mBorderWidth = resources.getDimension(R.dimen.default_border_width) // Not yet applied
    private var mIndicatorColor = ContextCompat.getColor(context, android.R.color.black)

    private val degrees: IntRange = 0..360

    private val paintIndicator = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintBorder = Paint(Paint.ANTI_ALIAS_FLAG)


    init {
        initPaints()
    }

    private fun initPaints() {
        paintIndicator.apply {
            style = Paint.Style.FILL_AND_STROKE
            strokeCap = Paint.Cap.SQUARE
        }

        paintBorder.apply {
            style = Paint.Style.STROKE
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val dimension = calculateMinimumDimension(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(dimension, dimension)
    }

    override fun onDraw(canvas: Canvas) {
        val dimension = calculateMinimumDimension(width, height)

        drawIndicators(canvas, dimension)
    }

    private fun calculateMinimumDimension(width: Int, height: Int) = min(width, height)

    private fun calculateRadius(dimension: Int) = (dimension / 2) - (mBorderWidth / 2)

    private fun calculateCenter(dimension: Int) = dimension / 2

    private fun drawIndicators(canvas: Canvas, dimension: Int) {
        paintIndicator.apply { color = mIndicatorColor }

        val center = calculateCenter(dimension)

        for (degree in degrees step mIndicatorStep) {

            var sizeIndicator: Float
            val space = center - (dimension * 0.01f).toInt()
            when {
                degree % 90 == 0 -> {
                    sizeIndicator = center - (dimension * 0.075f)
                    paintIndicator.strokeWidth = dimension * 0.02f
                }
                else -> {
                    sizeIndicator = center - (dimension * 0.035f)
                    paintIndicator.alpha = 200
                    paintIndicator.strokeWidth = dimension * 0.015f
                }
            }

            val startX = center + space * cos(Math.toRadians(degree.toDouble())).toFloat()
            val startY = center - space * sin(Math.toRadians(degree.toDouble())).toFloat()

            val stopX = center + sizeIndicator * cos(Math.toRadians(degree.toDouble())).toFloat()
            val stopY = center - sizeIndicator * sin(Math.toRadians(degree.toDouble())).toFloat()

            canvas.drawLine(startX, startY, stopX, stopY, paintIndicator)
        }
    }

    fun setIndicatorSteps(steps: Int) {
        mIndicatorStep = steps
        invalidate()
    }

    fun setIndicatorColor(color: Int) {
        mIndicatorColor = color
        invalidate()
    }

}