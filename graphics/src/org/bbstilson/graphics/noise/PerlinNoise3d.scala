package org.bbstilson.graphics.noise

import org.bbstilson.graphics._
import org.bbstilson.graphics.VectorOps._

import scala.collection.parallel.CollectionConverters._
import scala.util.Random
import java.awt.Color

/**
  * Check out PerlinNoise2d for a commented description of the algorithm.
  */
object PerlinNoise3d {

  def main(args: Array[String]): Unit = {
    val dim = 1000
    val grid = 10

    new PerlinNoise3d(dim, dim, 250, grid, grid, grid).generate()
  }

  // format: off
  private val r = Random
  private val gradients: Vector[(Double,Double,Double)] = Vector(
    ( 1,  1,  0), (-1, 1,  0), ( 1, -1,  0),
    (-1, -1,  0), ( 1, 0,  1), (-1,  0,  1),
    ( 1,  0, -1), (-1, 0, -1), ( 0,  1,  1),
    ( 0, -1,  1), ( 0, 1, -1), ( 0, -1, -1)
  )
  def randomGradient: Vector3 = gradients(r.nextInt(gradients.size))
  // format: on
}

class PerlinNoise3d(
  width: Int,
  height: Int,
  depth: Int,
  cellCountX: Int,
  cellCountY: Int,
  cellCountZ: Int
) {
  require(width % cellCountX == 0, "cellCountX must evenly divide the width.")
  require(height % cellCountY == 0, "cellCountY must evenly divide the height.")
  require(depth % cellCountZ == 0, "cellCountZ must evenly divide the depth.")

  import PerlinNoise3d._
  import PerlinUtils._

  val subPixelValueX = cellCountX.toDouble / width
  val subPixelValueY = cellCountY.toDouble / height
  val subPixelValueZ = cellCountZ.toDouble / depth

  val grid = {
    for {
      x <- 0 to cellCountX
      y <- 0 to cellCountY
      z <- 0 to cellCountZ
    } yield (x.toDouble, y.toDouble, z.toDouble) -> randomGradient
  }.toMap

  def generate(): Unit = (0 until depth).toList.par.foreach { d =>
    val img = Image(width, height, Some(s"perlin/perlin_noise_3d_frame_$d.jpg"))
    println(s"Generating layer $d...")
    img.generateWith(generator(d))
  }

  def generator(z: Int)(x: Int, y: Int): Color = {
    val subX = x * subPixelValueX
    val subY = y * subPixelValueY
    val subZ = z * subPixelValueZ

    val unitPosition = (subX, subY, subZ)

    val minX = subX.toInt.toDouble
    val minY = subY.toInt.toDouble
    val minZ = subZ.toInt.toDouble

    val unitX = fade(subX - minX)
    val unitY = fade(subY - minY)
    val unitZ = fade(subZ - minZ)

    // format: off
    val neighbors = List(
      (minX    , minY    , minZ    ), // aaa
      (minX    , minY + 1, minZ    ), // aba
      (minX    , minY    , minZ + 1), // aab
      (minX    , minY + 1, minZ + 1), // abb
      (minX + 1, minY    , minZ    ), // baa
      (minX + 1, minY + 1, minZ    ), // bba
      (minX + 1, minY    , minZ + 1), // bab
      (minX + 1, minY + 1, minZ + 1), // bbb
    )
    // format: on
    val neighborGradients = neighbors.map(grid)
    val distanceVectors = neighbors.map(unitPosition - _)
    val influences = distanceVectors
      .zip(neighborGradients)
      .map { case (v1, v2) => v1.dotP(v2) }

    val List(aaa, aba, aab, abb, baa, bba, bab, bbb) = influences
    val p = lerp(
      lerp(
        lerp(aaa, baa, unitX),
        lerp(aba, bba, unitX),
        unitY
      ),
      lerp(
        lerp(aab, bab, unitX),
        lerp(abb, bbb, unitX),
        unitY
      ),
      unitZ
    )

    val b = (((p + 1) / 2) * 255).toInt
    val g = Math.max(b - 40, 0)
    val r = Math.max((b - 40) / 3, 0)
    new Color(r, g, b)
  }
}
