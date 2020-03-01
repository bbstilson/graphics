package org.bbstilson.graphics

import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.awt.Color

case class Image(width: Int, height: Int, fileName: Option[String] = None) {

  def generateWith(generator: (Int, Int) => Color): Unit = {
    val out = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val desktopPath = s"${System.getProperty("user.home")}/Desktop/"
    val finalFileName = fileName.getOrElse(System.currentTimeMillis)
    val outputFile = new File(List(desktopPath, finalFileName, ".jpg").mkString)

    for {
      x <- 0 until width
      y <- 0 until height
    } {
      out.setRGB(x, y, generator(x, y).getRGB)
    }

    ImageIO.write(out, "jpg", outputFile)
  }
}
