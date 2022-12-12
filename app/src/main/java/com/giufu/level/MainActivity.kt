package com.giufu.level

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : BaseActivity(), OrientationListener {
    private lateinit var orientationManager: OrientationManager
    private lateinit var layout: ConstraintLayout
    private lateinit var graphicsUtils: GraphicsUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        layout = findViewById(R.id.main_layout)
        graphicsUtils = GraphicsUtils(resources)
        orientationManager = OrientationManager(this, this)
    }

    private fun createRect(pitch: Float, roll: Float): BitmapDrawable {
        return graphicsUtils.levelPolygon(pitch.toInt(), roll.toInt())
    }

    override fun onResume() {
        super.onResume()
        orientationManager.resume()
    }

    override fun onPause() {
        super.onPause()
        orientationManager.pause()
    }

    override fun onOrientationChanged(pitchDegrees: Float, rollDegrees: Float, azimuthDegrees: Float) {
        layout.invalidate()
        layout.background = createRect(pitchDegrees, rollDegrees)
    }
}
