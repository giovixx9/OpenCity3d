package com.giovanni.opencity

import android.view.MotionEvent

class TouchControls(private val renderer: GameRenderer) {

    private var moveTouchId = -1
    private var lookTouchId = -1
    private var moveStartX = 0f
    private var moveStartY = 0f
    private var lookLastX = 0f
    private var lookLastY = 0f

    fun onTouch(event: MotionEvent) {
        val screenMid = renderer.screenWidth / 2f

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val idx = event.actionIndex
                val id = event.getPointerId(idx)
                val x = event.getX(idx)
                val y = event.getY(idx)

                if (x < screenMid && moveTouchId == -1) {
                    moveTouchId = id
                    moveStartX = x
                    moveStartY = y
                } else if (x >= screenMid && lookTouchId == -1) {
                    lookTouchId = id
                    lookLastX = x
                    lookLastY = y
                }
            }

            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    val id = event.getPointerId(i)
                    val x = event.getX(i)
                    val y = event.getY(i)

                    if (id == moveTouchId) {
                        val dx = (x - moveStartX) / 150f
                        val dy = (y - moveStartY) / 150f
                        renderer.setMoveInput(dx.coerceIn(-1f, 1f), dy.coerceIn(-1f, 1f))
                    } else if (id == lookTouchId) {
                        val dx = x - lookLastX
                        val dy = y - lookLastY
                        renderer.rotateCamera(dx * 0.3f, dy * 0.3f)
                        lookLastX = x
                        lookLastY = y
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val idx = event.actionIndex
                val id = event.getPointerId(idx)
                if (id == moveTouchId) {
                    moveTouchId = -1
                    renderer.setMoveInput(0f, 0f)
                } else if (id == lookTouchId) {
                    lookTouchId = -1
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                moveTouchId = -1
                lookTouchId = -1
                renderer.setMoveInput(0f, 0f)
            }
        }
    }
}
