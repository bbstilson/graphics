package org.bbstilson.graphics.noise

import org.bbstilson.graphics._
import org.bbstilson.graphics.VectorOps._

import java.awt.Color
import scala.util.Random

object PerlinNoise2d {

  def main(args: Array[String]): Unit = {
    val width = 300
    val height = 300

    new PerlinNoise2d(width, height, 10, 1).generate()
  }

  private val r = Random
  private val gradients: Vector[Vector2] = Vector((1, 1), (-1, 1), (1, -1), (-1, -1))
  def randomGradient: Vector2 = gradients(r.nextInt(gradients.size))
}

class PerlinNoise2d(width: Int, height: Int, cellCountX: Int, cellCountY: Int) {
  require(width % cellCountX == 0, "cellCountX must evenly divide the width.")
  require(height % cellCountY == 0, "cellCountY must evenly divide the height.")

  import PerlinNoise2d._
  import PerlinUtils._

  val subPixelValueX = cellCountX.toDouble / width
  val subPixelValueY = cellCountY.toDouble / height

  val grid = {
    for {
      x <- 0 to cellCountX
      y <- 0 to cellCountY
    } yield (x.toDouble, y.toDouble) -> randomGradient
  }.toMap

  val img = Image(width, height, Some(s"perlin_noise_2d_${System.currentTimeMillis}"))

  def generate(): Unit = img.generateWith(generator)

  def generator(x: Int, y: Int): Color = {
    // Convert an integer pixel value to a floating point value that lies in the grid.
    val subX = x * subPixelValueX
    val subY = y * subPixelValueY
    val subPixelVector = (subX, subY)

    val minX = subX.toInt.toDouble
    val minY = subY.toInt.toDouble

    // Without the use of a fade function (also called an ease curve), the final result would
    // look bad because linear interpolation, while computationally cheap, looks unnatural.
    // We need a smoother transition between gradients.
    val unitX = fade(subX - minX)
    val unitY = fade(subY - minY)

    // A n-dimensional candidate point can be viewed as falling into a n-dimensional
    // grid cell, where the corners are the n-dimensional grid defined previously defined.
    // Fetch the 2^n closest gradient values, located at the 2^n corners of the grid cell
    // the candidate point falls into.
    val neighbors = List(
      (minX, minY),
      (minX + 1, minY),
      (minX, minY + 1),
      (minX + 1, minY + 1)
    )
    val neighborGradients = neighbors.map(grid)

    // Then, for each gradient value, compute a distance vector defined as the offset
    // vector from each corner node of that cell to the candidate point.
    val unitVectors = neighbors.map(subPixelVector - _)

    // After that, compute the dot product between each gradient vector and the distance
    // offset vector.
    val influences = unitVectors
      .zip(neighborGradients)
      .map { case (v1, v2) => v1.dotP(v2) }

    val List(i0, i1, i2, i3) = influences
    val p = lerp(
      lerp(i0, i1, unitX),
      lerp(i2, i3, unitX),
      unitY
    )

    val c = (((p + 1) / 2) * 255).toInt
    new Color(c, c, c)
  }
}
