package org.bbstilson.graphics.noise

import org.bbstilson.graphics._

import scala.collection.parallel.CollectionConverters._
import java.awt.Color

/**
  * Check out PerlinNoise2d for a commented description of the algorithm.
  */
object PerlinNoise3d {

  def main(args: Array[String]): Unit = {
    val dim = 500
    val grid = 5

    new PerlinNoise3d(dim, dim, 250, grid, grid, grid).generate()
  }
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

  import PerlinUtils._

  val subPixelValueX = cellCountX.toDouble / width
  val subPixelValueY = cellCountY.toDouble / height
  val subPixelValueZ = cellCountZ.toDouble / depth

  val grid = {
    for {
      x <- 0 to cellCountX
      y <- 0 to cellCountY
      z <- 0 to cellCountZ
    } yield Vector3(x, y, z) -> Vector3.random
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

    val unitPosition = Vector3(subX, subY, subZ)

    val minX = subX.toInt
    val minY = subY.toInt
    val minZ = subZ.toInt

    val unitX = fade(subX - minX)
    val unitY = fade(subY - minY)
    val unitZ = fade(subZ - minZ)

    // format: off
    val neighbors = List(
      Vector3(minX    , minY    , minZ    ), // aaa
      Vector3(minX    , minY + 1, minZ    ), // aba
      Vector3(minX    , minY    , minZ + 1), // aab
      Vector3(minX    , minY + 1, minZ + 1), // abb
      Vector3(minX + 1, minY    , minZ    ), // baa
      Vector3(minX + 1, minY + 1, minZ    ), // bba
      Vector3(minX + 1, minY    , minZ + 1), // bab
      Vector3(minX + 1, minY + 1, minZ + 1), // bbb
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
