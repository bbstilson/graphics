package org.bbstilson.graphics.noise

import org.bbstilson.graphics.Image
import java.awt.Color

object LinearPerlinNoise {

  def main(args: Array[String]): Unit = {
    new LinearPerlinNoise(2000, 500, 10000, 10000, 4000).generate()
  }
}

class LinearPerlinNoise(
  width: Int,
  height: Int,
  perlinWidth: Int,
  perlinHeight: Int,
  numPoints: Int
) {
  val window = 0.8 * height
  val perlin2d = new PerlinNoise2d(perlinWidth, perlinHeight, 10, 10)
  val image = Image(width, height, Some(s"linear_perlin_${System.currentTimeMillis()}"))
  val centerX = perlinWidth / 2
  val centerY = perlinHeight / 2
  val radius = Math.floor(Math.min(perlinWidth, perlinHeight) / 2 * 0.75)

  def radiansIter(stepSize: Double) = Iterator.unfold(0d) {
    case d if d > 360 => None
    case degree       => Some(Math.toRadians(degree), degree + stepSize)
  }

  /**
    * Perlin2d returns a Color where all values (RGB) are all the same integer between 0 and 255.
    * We treat the 128 (256 / 2) as the center line on our Linear Perlin image.
    * That is, if the 2d perlin noise image was entirely Color(128,128,128), then this
    * algorithm would output a flat line.
    */
  def generate(): Unit = {
    radiansIter(360d / numPoints).foreach { radians =>
      val percent = (radians / (2 * Math.PI))
      val px = Math.floor(centerX + radius * Math.cos(radians)).toInt
      val py = Math.floor(centerY + radius * Math.sin(radians)).toInt
      val c = perlin2d.generator(px, py).getBlue()

      val lineY = Math.floor((c.toDouble / 255) * window + ((height - window) / 2)).toInt
      val lineX = Math.floor(percent * width).toInt
      image.writePixel(lineX, lineY, Color.WHITE)
    }

    image.writeImage()
  }
}
