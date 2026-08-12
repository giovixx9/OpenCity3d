package com.giovanni.opencity

import kotlin.random.Random

data class Building(
    val x: Float,
    val z: Float,
    val width: Float,
    val depth: Float,
    val height: Float,
    val r: Float,
    val g: Float,
    val b: Float
)

object CityGenerator {

    // Griglia grande: 20x20 isolati, ogni isolato ha una via libera intorno (strada)
    // e un edificio dentro. Mappa totale: 20 * blockSize metri circa 600m
    const val GRID_SIZE = 20
    const val BLOCK_SIZE = 30f
    const val ROAD_WIDTH = 8f

    fun generate(seed: Long = 1234L): List<Building> {
        val rnd = Random(seed)
        val buildings = mutableListOf<Building>()

        val half = GRID_SIZE / 2

        for (gx in -half until half) {
            for (gz in -half until half) {
                // Ogni tanto lascia un isolato vuoto (piazza/parco)
                if (rnd.nextFloat() < 0.12f) continue

                val centerX = gx * (BLOCK_SIZE + ROAD_WIDTH)
                val centerZ = gz * (BLOCK_SIZE + ROAD_WIDTH)

                val width = BLOCK_SIZE * (0.5f + rnd.nextFloat() * 0.4f)
                val depth = BLOCK_SIZE * (0.5f + rnd.nextFloat() * 0.4f)
                val height = 4f + rnd.nextFloat() * 40f

                // Colore edificio: grigio/azzurrino variabile
                val base = 0.35f + rnd.nextFloat() * 0.35f
                val r = base
                val g = base + rnd.nextFloat() * 0.08f
                val b = base + 0.05f + rnd.nextFloat() * 0.1f

                buildings.add(Building(centerX, centerZ, width, depth, height, r, g, b))
            }
        }
        return buildings
    }

    fun mapExtent(): Float = (GRID_SIZE / 2) * (BLOCK_SIZE + ROAD_WIDTH) + BLOCK_SIZE
}
