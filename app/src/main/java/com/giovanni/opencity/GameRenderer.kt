package com.giovanni.opencity

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.max
import kotlin.math.min

class GameRenderer(private val context: Context) : GLSurfaceView.Renderer {

    var screenWidth = 1
    var screenHeight = 1

    private var program = 0
    private var aPositionLoc = 0
    private var aNormalLoc = 0
    private var uMVPLoc = 0
    private var uColorLoc = 0

    private lateinit var cube: CubeGeometry
    private var buildings: List<Building> = emptyList()

    // Camera / player
    private var playerX = 0f
    private var playerZ = 0f
    private var cameraYaw = 0f      // rotazione orizzontale
    private var cameraPitch = 18f   // inclinazione verso il basso
    private val cameraDistance = 12f

    private var moveDx = 0f
    private var moveDy = 0f
    private val moveSpeed = 14f // unità al secondo

    private var lastFrameTime = System.nanoTime()

    private val projMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val vpMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val mvpMatrix = FloatArray(16)

    fun setMoveInput(dx: Float, dy: Float) {
        moveDx = dx
        moveDy = dy
    }

    fun rotateCamera(dYaw: Float, dPitch: Float) {
        cameraYaw -= dYaw
        cameraPitch = min(70f, max(5f, cameraPitch - dPitch))
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.55f, 0.75f, 0.95f, 1f) // cielo azzurro
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glEnable(GLES20.GL_CULL_FACE)

        program = ShaderUtil.buildProgram(ShaderUtil.VERTEX_SHADER, ShaderUtil.FRAGMENT_SHADER)
        aPositionLoc = GLES20.glGetAttribLocation(program, "aPosition")
        aNormalLoc = GLES20.glGetAttribLocation(program, "aNormal")
        uMVPLoc = GLES20.glGetUniformLocation(program, "uMVPMatrix")
        uColorLoc = GLES20.glGetUniformLocation(program, "uColor")

        cube = CubeGeometry()
        buildings = CityGenerator.generate()

        lastFrameTime = System.nanoTime()
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        screenWidth = width
        screenHeight = height
        GLES20.glViewport(0, 0, width, height)
        val aspect = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projMatrix, 0, 60f, aspect, 0.5f, 800f)
    }

    override fun onDrawFrame(gl: GL10?) {
        val now = System.nanoTime()
        val dt = ((now - lastFrameTime) / 1_000_000_000f).coerceAtMost(0.1f)
        lastFrameTime = now

        updatePlayer(dt)
        updateCamera()

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        GLES20.glUseProgram(program)

        GLES20.glEnableVertexAttribArray(aPositionLoc)
        GLES20.glEnableVertexAttribArray(aNormalLoc)

        cube.vertexBuffer.position(0)
        GLES20.glVertexAttribPointer(aPositionLoc, 3, GLES20.GL_FLOAT, false, 6 * 4, cube.vertexBuffer)
        cube.vertexBuffer.position(3)
        GLES20.glVertexAttribPointer(aNormalLoc, 3, GLES20.GL_FLOAT, false, 6 * 4, cube.vertexBuffer)

        // Terreno/strada: un grande cubo piatto grigio scuro
        drawCube(
            x = playerX.let { 0f }, y = -0.55f, z = 0f,
            sx = CityGenerator.mapExtent() * 2.2f, sy = 0.5f, sz = CityGenerator.mapExtent() * 2.2f,
            r = 0.25f, g = 0.25f, b = 0.27f
        )

        // Disegna tutti gli edifici visibili (semplice frustum a distanza)
        for (b in buildings) {
            val dist = distance(playerX, playerZ, b.x, b.z)
            if (dist > 220f) continue // culling a distanza per prestazioni
            drawCube(b.x, 0f, b.z, b.width, b.height, b.depth, b.r, b.g, b.b)
        }

        // Il player stesso: piccolo cubo colorato per riferimento visivo (facoltativo)
        drawCube(playerX, 0f, playerZ, 1f, 1.8f, 1f, 0.9f, 0.2f, 0.2f)

        GLES20.glDisableVertexAttribArray(aPositionLoc)
        GLES20.glDisableVertexAttribArray(aNormalLoc)
    }

    private fun updatePlayer(dt: Float) {
        if (moveDx == 0f && moveDy == 0f) return

        val yawRad = Math.toRadians(cameraYaw.toDouble())
        // Avanti/indietro rispetto alla direzione della camera
        val forwardX = sin(yawRad).toFloat()
        val forwardZ = -cos(yawRad).toFloat()
        val rightX = cos(yawRad).toFloat()
        val rightZ = sin(yawRad).toFloat()

        val moveForward = -moveDy // joystick su = avanti
        val moveRight = moveDx

        playerX += (forwardX * moveForward + rightX * moveRight) * moveSpeed * dt
        playerZ += (forwardZ * moveForward + rightZ * moveRight) * moveSpeed * dt

        val extent = CityGenerator.mapExtent()
        playerX = playerX.coerceIn(-extent, extent)
        playerZ = playerZ.coerceIn(-extent, extent)
    }

    private fun updateCamera() {
        val yawRad = Math.toRadians(cameraYaw.toDouble())
        val pitchRad = Math.toRadians(cameraPitch.toDouble())

        val camX = playerX - (sin(yawRad) * cos(pitchRad) * cameraDistance).toFloat()
        val camY = 4f + (sin(pitchRad) * cameraDistance).toFloat()
        val camZ = playerZ + (cos(yawRad) * cos(pitchRad) * cameraDistance).toFloat()

        Matrix.setLookAtM(
            viewMatrix, 0,
            camX, camY, camZ,
            playerX, 1.2f, playerZ,
            0f, 1f, 0f
        )
        Matrix.multiplyMM(vpMatrix, 0, projMatrix, 0, viewMatrix, 0)
    }

    private fun drawCube(x: Float, y: Float, z: Float, sx: Float, sy: Float, sz: Float, r: Float, g: Float, b: Float) {
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, x, y, z)
        Matrix.scaleM(modelMatrix, 0, sx, sy, sz)
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)

        GLES20.glUniformMatrix4fv(uMVPLoc, 1, false, mvpMatrix, 0)
        GLES20.glUniform4f(uColorLoc, r, g, b, 1f)

        cube.indexBuffer.position(0)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, cube.vertexCount, GLES20.GL_UNSIGNED_SHORT, cube.indexBuffer)
    }

    private fun distance(x1: Float, z1: Float, x2: Float, z2: Float): Float {
        val dx = x1 - x2
        val dz = z1 - z2
        return kotlin.math.sqrt(dx * dx + dz * dz)
    }
}
