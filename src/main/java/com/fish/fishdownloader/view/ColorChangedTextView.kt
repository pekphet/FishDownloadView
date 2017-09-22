package com.fish.fishdownloader.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.widget.TextView

/**
 * Created by fish on 17-9-22.
 */
class ColorChangedTextView(ctx: Context, attr: AttributeSet) : TextView(ctx, attr) {

    val mBound = Rect()
    val mProgressChangedColor: Int = 0xffffffff.toInt()
    val mPaint: Paint by lazy {
        Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            textSize = this@ColorChangedTextView.textSize
            getTextBounds(this@ColorChangedTextView.text.toString(), 0, this@ColorChangedTextView.text.length, mBound)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(onMesureZ(true, widthMeasureSpec), onMesureZ(false, heightMeasureSpec))
    }

    private fun onMesureZ(isWidth: Boolean, oldMs: Int): Int {
        val mode = MeasureSpec.getMode(oldMs)
        val oldSize = MeasureSpec.getSize(oldMs)
        return when (mode) {
            MeasureSpec.EXACTLY -> oldSize
            MeasureSpec.AT_MOST, MeasureSpec.UNSPECIFIED -> {
                if (isWidth) (paddingLeft + paddingRight + mPaint.measureText(this@ColorChangedTextView.text.toString())).toInt()
                else (paddingTop + paddingBottom + Math.abs(mPaint.getFontMetrics().let { it.bottom - it.top }).toInt())
            }
            else -> oldSize
        }
    }

    var mStartX = 0F
    var mStartY = 0F
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mStartX = (measuredWidth / 2 - mPaint.measureText(this@ColorChangedTextView.text.toString()) / 2)
        mStartY = measuredHeight / 2 + mPaint.fontMetrics.let { (it.bottom - it.top) / 2 - it.descent }
        onDrawZ(canvas ?: return)
    }

    @SuppressLint("WrongConstant")
    fun onDrawText(canvas: Canvas, isChangedColor: Boolean, startPoi: Int, endPoi: Int) {
        Log.d("DRAWTEXT", "ISCHCL:$isChangedColor, startPoi: $startPoi, endPoi: $endPoi, h:$measuredHeight")
//        canvas.save(Canvas.CLIP_SAVE_FLAG)
        canvas.clipRect(startPoi, 0, endPoi, measuredHeight)
        mPaint.color = if (isChangedColor) mProgressChangedColor else mTextColor
        canvas.drawText(this@ColorChangedTextView.text.toString(), mStartX, mStartY, mPaint)
    }

    var mSpliteXProg = 0
    private fun onDrawZ(canvas: Canvas) {
        var drawTotalW = 0

        var spliteXMax = 0
        drawTotalW = mPaint.measureText(this.text.toString()).toInt()
        spliteXMax = (mStartX + drawTotalW).toInt()

        onDrawText(canvas, true, 0, mSpliteXProg)
        onDrawText(canvas, false, mSpliteXProg, spliteXMax)
    }

    var mTextColor = currentTextColor
    override fun setTextColor(color: Int) {
        super.setTextColor(color)
        mTextColor = color
    }

    fun setTextProg(position: Int) {
        mSpliteXProg = position
        invalidate()
    }

    fun stopTextProg() = stopTextProg(mTextColor)
    fun stopTextProg(textColor: Int) {
        post {
            this.setTextColor(textColor)
            setTextProg(0)
            invalidate()
        }
    }

}