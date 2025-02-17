package org.bbstilson.graphics.noise

import org.bbstilson.graphics.Image

import java.awt.Color
import scala.collection.parallel.CollectionConverters._

val IMAGE_HEIGHT = 540;
val IMAGE_WIDTH = IMAGE_HEIGHT * 2;

object LinearPerlinNoise {

  def main(args: Array[String]): Unit = {
    // single()
    moving()
  }

  def single(): Unit = {
    val image = Image(IMAGE_WIDTH, IMAGE_HEIGHT, Some(s"linear_perlin"))
    image.fill(Color.WHITE)
    val (perlinWidth, perlinHeight) = (10000, 10000)
    val perlin = new PerlinNoise2d(perlinWidth, perlinHeight, 10, 10)
    val numPoints = 4000
    val (centerX, centerY) = (perlinWidth / 2, perlinHeight / 2)
    new LinearPerlinNoise(
      image,
      IMAGE_WIDTH,
      IMAGE_HEIGHT,
      perlin,
      perlinWidth,
      perlinHeight,
      numPoints,
      centerX,
      centerY
    ).generate()
  }

  def moving(): Unit = {
    val (perlinWidth, perlinHeight) = (10000, 10000)
    val perlin = new PerlinNoise2d(perlinWidth, perlinHeight, 10, 10)
    val numPoints = 4000
    val (centerX, centerY) = (perlinWidth / 2, perlinHeight / 2)

    val numLayers = 300
    val moveRadius = 500
    radianSteps(numLayers).zipWithIndex.toList.par.foreach { (radians, idx) =>
      {
        val cX = Math.floor(centerX + moveRadius * Math.cos(radians)).toInt
        val cY = Math.floor(centerY + moveRadius * Math.sin(radians)).toInt
        val image =
          Image(IMAGE_WIDTH, IMAGE_HEIGHT, Some(f"perlin/$idx%04d"))
        image.fill(Color.WHITE)

        new LinearPerlinNoise(
          image,
          IMAGE_WIDTH,
          IMAGE_HEIGHT,
          perlin,
          perlinWidth,
          perlinHeight,
          numPoints,
          cX,
          cY
        ).generate()
      }
    }
  }

  def radianSteps(numSteps: Int) = {
    val stepSize = 360d / numSteps
    Iterator.unfold(0d) {
      case d if d > 360 => None
      case degree       => Some(Math.toRadians(degree), degree + stepSize)
    }
  }
}

class LinearPerlinNoise(
    image: Image,
    imgWidth: Int,
    imgHeight: Int,
    perlin: PerlinNoise2d,
    perlinWidth: Int,
    perlinHeight: Int,
    numPoints: Int,
    centerX: Int,
    centerY: Int
) {
  import LinearPerlinNoise._

  val window = 0.8 * imgHeight
  val radius = Math.floor(Math.min(perlinWidth, perlinHeight) / 2 * 0.8)

  /** Perlin returns a Color where all values (RGB) are all the same integer
    * between 0 and 255. We treat the 128 (256 / 2) as the center line on our
    * Linear Perlin image. That is, if the 2d perlin noise image was entirely
    * Color(128,128,128), then this algorithm would output a flat line.
    */
  def generate(): Unit = {
    radianSteps(numPoints).foreach { radians =>
      val percent = (radians / (2 * Math.PI))
      val px = Math.floor(centerX + radius * Math.cos(radians)).toInt
      val py = Math.floor(centerY + radius * Math.sin(radians)).toInt
      val c = perlin.generator(px, py).getBlue()

      val lineY = Math
        .floor((c.toDouble / 255) * window + ((imgHeight - window) / 2))
        .toInt
      val lineX = Math.floor(percent * imgWidth).toInt
      image.writePixel(lineX, lineY, Color.BLACK)
    }

    image.writeImage()
  }
}
