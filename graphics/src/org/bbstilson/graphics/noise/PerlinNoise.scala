package org.bbstilson.graphics.noise

import org.bbstilson.graphics._

import Math._
import java.awt.Color

object PerlinNoise {

  def main(args: Array[String]): Unit = {
    val width = 300
    val height = 300

    new PerlinNoise(width, height, 6, 6).generate()
  }
}

class PerlinNoise(width: Int, height: Int, cellCountX: Int, cellCountY: Int) {
  val subPixelValue = cellCountX.toDouble / width

  val grid = {
    for {
      x <- 0 to cellCountX
      y <- 0 to cellCountY
    } yield Vector2(x, y) -> Vector2.random
  }.toMap

  val img = Image(width, height, Some(s"perlin_noise_${System.currentTimeMillis}"))

  def generate(): Unit = img.generateWith(generator)

  def generator(x: Int, y: Int): Color = {
    val subX = x * subPixelValue
    val subY = y * subPixelValue

    val subPixelVector = Vector2(subX, subY)

    val minX = subX.toInt
    val minY = subY.toInt
    val maxX = minX + 1
    val maxY = minY + 1

    val unitX = fade(subX - minX)
    val unitY = fade(subY - minY)

    val fadedX = fade(unitX)
    val fadedY = fade(unitY)

    val neighbors = List(
      Vector2(minX, minY),
      Vector2(maxX, minY),
      Vector2(minX, maxY),
      Vector2(maxX, maxY)
    )
    val neighborGradients = neighbors.map(grid)
    val unitVectors = neighbors.map(subPixelVector - _)
    val influences = unitVectors
      .zip(neighborGradients)
      .map { case (v1, v2) => v1.dotP(v2) }

    val List(i0, i1, i2, i3) = influences
    val x1 = lerp(i0, i1, unitX)
    val x2 = lerp(i2, i3, unitX)
    val p = lerp(x1, x2, unitY)

    val c = (((p + 1) / 2) * 255).toInt
    new Color(c, c, c)
  }

  // A n-dimensional candidate point can be viewed as falling into a n-dimensional
  // grid cell, where the corners are the n-dimensional grid defined previously defined.
  // Fetch the 2^n closest gradient values, located at the 2^n corners of the grid cell
  // the candidate point falls into.

  // Then, for each gradient value, compute a distance vector defined as the offset
  // vector from each corner node of that cell to the candidate point.
  // After that, compute the dot product between each gradient vector and the distance
  // offset vector.
  // This function has a value of 0 at the node and a gradient equal to the precomputed
  // node gradient.

  // Fade function as defined by Ken Perlin.  This eases coordinate values
  // so that they will ease towards integral values.  This ends up smoothing
  // the final output.
  def fade(t: Double): Double = t * t * t * (t * (t * 6 - 15) + 10) // 6t^5 - 15t^4 + 10t^3

  // Linear Interpolate
  def lerp(a: Double, b: Double, x: Double): Double = a + x * (b - a)
}
