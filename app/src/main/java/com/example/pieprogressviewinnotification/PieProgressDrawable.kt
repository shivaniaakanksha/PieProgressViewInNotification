package com.example.pieprogressviewinnotification

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics


class PieProgressDrawable(val mContext: Context) : Drawable() {
    var mPaint: Paint
    var mBoundsF: RectF? = null
    var mInnerBoundsF: RectF? = null
    val START_ANGLE = 0f
    var mDrawTo = 0f

    /**
     * Set the border width.
     * @param widthDp in dip for the pie border
     */
    fun setBorderWidth(widthDp: Float, dm: DisplayMetrics) {
        val borderWidth = widthDp * dm.density
        mPaint.strokeWidth = borderWidth
    }

    /**
     * @param color you want the pie to be drawn in
     */
    fun setColor(color: Int) {
        mPaint.color = color
    }

    override fun draw(canvas: Canvas) {
        // Rotate the canvas around the center of the pie by 90 degrees
        // counter clockwise so the pie stars at 12 o'clock.
        canvas.rotate(-90f, bounds.centerX().toFloat(), bounds.centerY().toFloat())
        //whole un-progressed circle/oval
        mPaint.style = Paint.Style.FILL

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPaint.color = mContext.getColor(R.color.teal_200)
        }else{
            mPaint.color = mContext.resources.getColor(R.color.teal_200)
        }
        canvas.drawOval(mBoundsF!!, mPaint)

        //to create progress arc
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPaint.color = mContext.getColor(R.color.white)
        }else{
            mPaint.color = mContext.resources.getColor(R.color.white)
        }
        mPaint.style = Paint.Style.FILL
        canvas.drawArc(mInnerBoundsF!!, START_ANGLE, mDrawTo, true, mPaint)

        //to create a border for the progress circle/oval
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mPaint.color = mContext.getColor(R.color.white)
        }else{
            mPaint.color = mContext.resources.getColor(R.color.white)
        }
        mPaint.style = Paint.Style.STROKE
        canvas.drawOval(mBoundsF!!, mPaint)

        // Draw inner oval and text on top of the pie (or add any other
        // decorations such as a stroke) here..
        // Don't forget to rotate the canvas back if you plan to add text!
        // ...
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        mInnerBoundsF = RectF(bounds)
        mBoundsF = mInnerBoundsF
        val halfBorder = (mPaint.strokeWidth / 2f + 0.5f).toInt()
        mInnerBoundsF!!.inset(halfBorder.toFloat(), halfBorder.toFloat())
    }

    override fun onLevelChange(level: Int): Boolean {
        val drawTo = START_ANGLE + 360.toFloat() * level / 100f
        val update = drawTo != mDrawTo
        mDrawTo = drawTo
        return update
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return mPaint.alpha
    }

    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

}