package org.bbstilson.graphics.noise

import org.bbstilson.graphics.Image

import eu.timepit.refined._
import eu.timepit.refined.numeric._
import eu.timepit.refined.boolean._

import java.awt.Color
import scala.util.Random

object RandomNoise {
  type ZeroToOne = Not[Less[0.0]] And Not[Greater[1.0]]
  val r = Random
  val img = Image(300, 300, Some("random_noise"))

  def main(args: Array[String]): Unit = img.generateWith(generator)

  def generator(x: Int, y: Int): Color = {
    val c = refineV[ZeroToOne](r.nextDouble)
      .map(t => (t.value * 255).toInt)
      .getOrElse(0)

    new Color(c, c, c)
  }
}
