package com.giufu.level

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

interface OrientationListener {
    fun onOrientationChanged(pitchDegrees: Float, rollDegrees: Float, azimuthDegrees: Float)
}

const val ALPHA: Float = 1 / 16f //adjust sensitivity

class OrientationManager(context: Context, listener: OrientationListener): SensorEventListener {
    private val manager: SensorManager
    private val accelerometer: Sensor
    private val magnetometer: Sensor
    private val context: Context
    private val listener: OrientationListener
    private val orientation = FloatArray(3)
    private var startOrientation: FloatArray? = null
    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null
    private var azimuth = 0f
    private var pitch = 0f
    private var roll = 0f

    init {
        this.context = context
        this.listener = listener
        manager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        magnetometer = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    fun resume() {
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        manager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME)
    }

    fun pause() {
        manager.unregisterListener(this, magnetometer)
        manager.unregisterListener(this, accelerometer)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            gravity = lowPassFilter(sensorEvent.values.clone(), gravity)
        } else if (sensorEvent.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = lowPassFilter(sensorEvent.values.clone(), geomagnetic)
        }
        if (gravity != null && geomagnetic != null) {
            val r = FloatArray(9)
            val i = FloatArray(9)
            val success = SensorManager.getRotationMatrix(r, i, gravity, geomagnetic
            )
            if (success) {
                SensorManager.getOrientation(r, orientation)
                if (startOrientation == null) {
                    startOrientation = FloatArray(orientation.size)
                    System.arraycopy(orientation, 0, startOrientation!!, 0, orientation.size)
                }
                //Full rotation is equal to 6. 360 / 6 = 60 which is why the roll/pitch must be multiplied by 60
                azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                pitch = Math.toDegrees(orientation[1].toDouble()).toFloat()
                roll = Math.toDegrees(orientation[2].toDouble()).toFloat()
                listener.onOrientationChanged(pitch, roll, azimuth)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, i: Int) {
        // in this case I do not need to know the accuracy
    }

    // https://stackoverflow.com/questions/27846604/how-to-get-smooth-orientation-data-in-android
    private fun lowPassFilter(input: FloatArray, output: FloatArray?): FloatArray {
        if (output == null) return input
        for (i in input.indices) {
            output[i] = output[i] + ALPHA * (input[i] - output[i])
        }
        return output
    }
}
