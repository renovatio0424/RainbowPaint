package com.reno.rainbowpaint.paint

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

private const val TOUCH_TOLERANCE = 10f

class DoodleView : View {

    // drawing area for displaying or saving
    private var bitmap: Bitmap? = null

    // used to to draw on the bitmap
    private var bitmapCanvas: Canvas? = null

    // used to draw bitmap onto screen
    private var paintScreen: Paint = Paint()

    // used to draw lines onto bitmap
    private var paintLine: Paint = Paint()

    // Maps of current Paths being drawn and Points in those Paths
    private val pathMap: MutableMap<Int, Path> = HashMap()
    private val previousPointMap: MutableMap<Int, Point> = HashMap()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        paintLine.isAntiAlias = true
        paintLine.strokeWidth = 10f
        paintLine.strokeCap = Paint.Cap.ROUND
        paintLine.color = Color.BLACK
        paintLine.style = Paint.Style.STROKE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap?.let {
            bitmapCanvas = Canvas(it)
            it.eraseColor(Color.WHITE)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        bitmap?.let {
            canvas?.drawBitmap(it, 0f, 0f, paintScreen)
        }

        for (key in pathMap.keys) {
            pathMap[key]?.let {
                canvas?.drawPath(it, paintLine)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked
        val actionIndex = event.actionIndex
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex), event.getPointerId(actionIndex))
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex))
        } else {
            touchMoved(event)
        }
        invalidate() // redraw
        return true
    }

    private fun touchStarted(x: Float, y: Float, pointerId: Int) {
        val path: Path? // used to store the path for the given touch id
        val point: Point? // used to store the last point in path
        if (pathMap.containsKey(pointerId)) {
            path = pathMap[pointerId] // get the Path
            path?.reset() // resets the Path because a new touch has started
            point = previousPointMap[pointerId] // get Path's last point
        } else {
            path = Path()
            pathMap[pointerId] = path // add the Path to Map
            point = Point() // create a new Point
            previousPointMap[pointerId] = point // add the Point to the Map
        }

        // move to the coordinates of the touch
        path?.moveTo(x, y)
        point?.x = x.toInt()
        point?.y = y.toInt()
    }

    private fun touchMoved(event: MotionEvent) {
        // for each of the pointers in the given MotionEvent
        for (i in 0 until event.pointerCount) {
            // get the pointer ID and pointer index
            val pointerID = event.getPointerId(i)
            val pointerIndex = event.findPointerIndex(pointerID)

            // if there is a path associated with the pointer
            if (pathMap.containsKey(pointerID)) {
                // get the new coordinates for the pointer
                val newX = event.getX(pointerIndex)
                val newY = event.getY(pointerIndex)

                // get the path and previous point associated with
                // this pointer
                val path = pathMap[pointerID]
                val point = previousPointMap[pointerID]

                point ?: continue
                // calculate how far the user moved from the last update
                val deltaX = abs(newX - point.x)
                val deltaY = abs(newY - point.y)

                // if the distance is significant enough to matter
                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    // move the path to the new location
                    path?.quadTo(point.x.toFloat(), point.y.toFloat(), (newX + point.x) / 2, (newY + point.y) / 2)

                    // store the new coordinates
                    point.x = newX.toInt()
                    point.y = newY.toInt()
                }
            }
        }
    }

    private fun touchEnded(pointerId: Int) {
        val path = pathMap[pointerId] // get the corresponding Path
        path?.let {
            bitmapCanvas?.drawPath(path, paintLine) // draw to bitmapCanvas
            path.reset() // reset the Path
        }
    }
}