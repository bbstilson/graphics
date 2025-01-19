package org.bbstilson.graphics

import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.Color

case class Image(width: Int, height: Int, fileName: Option[String] = None) {
  private val imageBuffer =
    new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

  private val outputFile = {
    val desktopPath = s"${System.getProperty("user.home")}/Desktop/"
    val finalFileName = fileName.getOrElse(System.currentTimeMillis)
    val f = new File(List(desktopPath, finalFileName, ".jpg").mkString)
    f.getParentFile().mkdirs()
    f.createNewFile()
    f
  }

  /** Populates every pixel in the image with a color.
    */
  def fill(color: Color): Unit = {
    for {
      x <- 0 until width
      y <- 0 until height
    } writePixel(x, y, color)
  }

  /** Generates a full jpg image by providing every pixel (x, y) to a generator
    * function which returns a Color that the pixel should be.
    *
    * @param generator
    */
  def generateWith(generator: (Int, Int) => Color): Unit = {
    for {
      x <- 0 until width
      y <- 0 until height
    } writePixel(x, y, generator(x, y))

    writeImage()
  }

  /** Sets a color to a pixel.
    *
    * @param x
    * @param y
    * @param color
    */
  def writePixel(x: Int, y: Int, color: Color): Unit =
    imageBuffer.setRGB(x, y, color.getRGB())

  /** Writes the image buffer to the file.
    */
  def writeImage(): Unit = ImageIO.write(imageBuffer, "jpg", outputFile)
}
