package pe.com.jjorgerc.compassview.compass

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import kotlinx.android.synthetic.main.layout_base.view.*
import pe.com.jjorgerc.compassview.R
import kotlin.math.min

class Compass @JvmOverloads constructor(
    context: Context,
    private val attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), SensorEventListener {

    private var mDefaultDegrees = 0f

    private var mIndicatorStep = resources.getInteger(R.integer.default_indicator_step)
    private var mIndicatorColor = ContextCompat.getColor(context, R.color.colorPrimary)
    private var mNeedleResource = R.drawable.aguja

    private var mListener: CompassSensorListener? = null
    private var manager: SensorManager? = null

    init {
        init()
        setProperties()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.layout_base, this, true);
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.Compass, 0, 0)

        mIndicatorColor = attributes.getColor(R.styleable.Compass_compass_indicatorColor, mIndicatorColor)
        mIndicatorStep = attributes.getInteger(R.styleable.Compass_compass_indicatorSteps, mIndicatorStep)
        mNeedleResource = attributes.getResourceId(R.styleable.Compass_compass_needle, mNeedleResource)

        attributes.recycle()
    }

    private fun calculateMinimumDimension(width: Int, height: Int) = min(width, height)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val dimension = calculateMinimumDimension(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(dimension, dimension)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        mListener?.onAccuracyChanged(sensor, accuracy)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        mListener?.apply {
            val value = event?.values?.get(0) ?: 0f
            compass_degrees?.text = "${value}°"
            this.onSensorChanged(value)
            createAnimation(value)
        }
    }

    private fun setProperties() {
        compass_wrapper?.setIndicatorColor(mIndicatorColor)
        compass_wrapper?.setIndicatorSteps(mIndicatorStep)
        compass_needle?.setImageResource(mNeedleResource)
    }

    fun subscribeSensor(listener: CompassSensorListener) {
        mListener = listener
        subscribeSensor()
    }

    private fun subscribeSensor() {
        manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = manager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        manager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

    fun unsubscribeSensor() {
        manager?.unregisterListener(this)
    }

    private fun createAnimation(degrees: Float) {
        val rotation = RotateAnimation(
            mDefaultDegrees,
            -degrees,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = 300
            fillAfter = true
        }
        mDefaultDegrees = -degrees
        compass_needle?.startAnimation(rotation)
    }

    fun setIndicatorSteps(steps: Int) {
        compass_wrapper?.setIndicatorSteps(mIndicatorStep)
    }

    fun setIndicatorColor(color: Int) {
        compass_wrapper?.setIndicatorColor(mIndicatorColor)
    }

    interface CompassSensorListener {
        fun onAccuracyChanged(sensor: Sensor?, accuracy: Int)
        fun onSensorChanged(degrees: Float)
    }
}