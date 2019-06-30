package pe.com.jjorgerc.compassview

import android.hardware.Sensor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import pe.com.jjorgerc.compassview.compass.Compass

class MainActivity : AppCompatActivity(), Compass.CompassSensorListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        compass?.subscribeSensor(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    override fun onSensorChanged(degrees: Float) = Unit


    override fun onDestroy() {
        super.onDestroy()
        compass?.unsubscribeSensor()
    }
}
