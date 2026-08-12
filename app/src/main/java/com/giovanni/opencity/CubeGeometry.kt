package com.giovanni.opencity

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class CubeGeometry {

    private val vertices = floatArrayOf(
        -0.5f, 0f, 0.5f,  0f, 0f, 1f,
         0.5f, 0f, 0.5f,  0f, 0f, 1f,
         0.5f, 1f, 0.5f,  0f, 0f, 1f,
        -0.5f, 1f, 0.5f,  0f, 0f, 1f,
         0.5f, 0f, -0.5f,  0f, 0f, -1f,
        -0.5f, 0f, -0.5f,  0f, 0f, -1f,
        -0.5f, 1f, -0.5f,  0f, 0f, -1f,
         0.5f, 1f, -0.5f,  0f, 0f, -1f,
        -0.5f, 0f, -0.5f,  -1f, 0f, 0f,
        -0.5f, 0f, 0.5f,  -1f, 0f, 0f,
        -0.5f, 1f, 0.5f,  -1f, 0f, 0f,
        -0.5f, 1f, -0.5f,  -1f, 0f, 0f,
         0.5f, 0f, 0.5f,  1f, 0f, 0f,
         0.5f, 0f, -0.5f,  1f, 0f, 0f,
         0.5f, 1f, -0.5f,  1f, 0f, 0f,
         0.5f, 1f, 0.5f,  1f, 0f, 0f,
        -0.5f, 1f, 0.5f,  0f, 1f, 0f,
         0.5f, 1f, 0.5f,  0f, 1f, 0f,
         0.5f, 1f, -0.5f,  0f, 1f, 0f,
        -0.5f, 1f, -0.5f,  0f, 1f, 0f,
        -0.5f, 0f, -0.5f,  0f, -1f, 0f,
         0.5f, 0f, -0.5f,  0f, -1f, 0f,
         0.5f, 0f, 0.5f,  0f, -1f, 0f,
        -0.5f, 0f, 0.5f,  0f, -1f, 0f
    )

    private val indices = shortArrayOf(
        0,1,2, 0,2,3,
        4,5,6, 4,6,7,
        8,9,10, 8,10,11,
        12,13,14, 12,14,15,
        16,17,18, 16,18,19,
        20,21,22, 20,22,23
    )

    val vertexCount = indices.size

    val vertexBuffer: FloatBuffer = ByteBuffer
        .allocateDirect(vertices.size * 4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .apply { put(vertices); position(0) }

    val indexBuffer: java.nio.ShortBuffer = ByteBuffer
        .allocateDirect(indices.size * 2)
        .order(ByteOrder.nativeOrder())
        .asShortBuffer()
        .apply { put(indices); position(0) }
}
