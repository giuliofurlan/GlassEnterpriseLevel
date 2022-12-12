package com.giufu.level

import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.text.TextPaint
import kotlin.math.tan

class GraphicsUtils(resources: Resources) {
    inner class DegreeTextPaint(color: Int): TextPaint() {
        init {
            this.isAntiAlias = true
            this.textSize = sp(60)
            this.color = color
            this.textAlign = Align.CENTER
        }
    }

    private val resources: Resources
    private var width: Int = Resources.getSystem().displayMetrics.widthPixels
    private var height: Int = Resources.getSystem().displayMetrics.heightPixels
    private val w = width.toFloat()
    private val h = height.toFloat()
    private val backgroundPaint = Paint()
    private val foregroundPaint = Paint()
    private val backGroundRect = Rect(0 ,0, width, height)
    private  var foregroundTextPaint: DegreeTextPaint
    private var backgroundTextPaint: DegreeTextPaint
    private var yPos: Float
    private var bg: Bitmap
    private val maxPitch = 90 // degrees

    private var linesForegroundPaint = Paint()
    private var linesBackgroundPaint = Paint()

    init {
        this.resources = resources
        backgroundPaint.color = Color.WHITE
        foregroundPaint.isAntiAlias = true

        linesForegroundPaint.color = Color.BLACK
        linesForegroundPaint.style = Paint.Style.STROKE
        linesForegroundPaint.strokeWidth = sp(3)

        linesBackgroundPaint.color = Color.WHITE
        linesBackgroundPaint.style = Paint.Style.STROKE
        linesBackgroundPaint.strokeWidth = sp(3)

        foregroundTextPaint = DegreeTextPaint(Color.WHITE)
        backgroundTextPaint = DegreeTextPaint(Color.BLACK)
        yPos = (height / 2 - (foregroundTextPaint.descent() + backgroundTextPaint.ascent()) / 2)
        bg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }

    fun levelPolygon(pitch: Int, roll: Int): BitmapDrawable {
        var c = Color.BLACK
        if (roll == 0) {
            c = Color.GREEN
        }
        foregroundPaint.color = c
        backgroundTextPaint.color = c
        linesForegroundPaint.color = c

        val canvas = Canvas(bg)
        var xPitch = pitch
        if (xPitch > maxPitch){
            xPitch = maxPitch
        } else if (xPitch < -maxPitch) {
            xPitch = -maxPitch
        }
        val rectHeight = (((height * xPitch )/ maxPitch) + (height / 2))
        val rads = Math.toRadians(roll.toDouble())
        //c2 = c1 * tan(a)
        val rHeight = w * tan(rads).toFloat()
        val path = Path()
        path.reset() // only needed when reusing this path for a new build
        path.moveTo(0f, h) // used for first point
        path.lineTo(w, h)
        path.lineTo(w, h - rectHeight - (rHeight /2))
        path.lineTo(0f, h- rectHeight + (rHeight /2))
        path.lineTo(0f, h)
        canvas.drawRect(backGroundRect, backgroundPaint)
        canvas.drawPath(path, foregroundPaint)
        // I have to draw text and lines twice so it is always visible
        canvas.clipPath(path, Region.Op.XOR)
        canvas.drawText("$roll°", w/2, yPos, backgroundTextPaint)
        canvas.drawLine(sp(30), h/2, sp(90), h/2, linesForegroundPaint)
        canvas.drawLine(w - sp(30), h/2, w -sp(90), h/2, linesForegroundPaint)
        canvas.clipRect(backGroundRect, Region.Op.XOR)
        canvas.drawText("$roll°", w/2, yPos, foregroundTextPaint)
        canvas.drawLine(sp(30), h/2, sp(90), h/2, linesBackgroundPaint)
        canvas.drawLine(w - sp(30), h/2, w - sp(90), h/2, linesBackgroundPaint)
        return BitmapDrawable(resources, bg)
    }

    private fun sp(size: Int): Float {
        return size * resources.displayMetrics.density
    }
}