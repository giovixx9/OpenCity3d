package com.giovanni.opencity

import android.app.Activity
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.FrameLayout

class MainActivity : Activity() {

    private lateinit var glView: GLSurfaceView
    private lateinit var renderer: GameRenderer
    private lateinit var touchControls: TouchControls

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        renderer = GameRenderer(this)
        touchControls = TouchControls(renderer)

        glView = object : GLSurfaceView(this) {
            override fun onTouchEvent(event: MotionEvent): Boolean {
                touchControls.onTouch(event)
                return true
            }
        }
        glView.setEGLContextClientVersion(2)
        glView.setRenderer(renderer)

        val root = FrameLayout(this)
        root.addView(glView)
        setContentView(root)
    }

    override fun onPause() {
        super.onPause()
        glView.onPause()
    }

    override fun onResume() {
        super.onResume()
        glView.onResume()
    }
}
