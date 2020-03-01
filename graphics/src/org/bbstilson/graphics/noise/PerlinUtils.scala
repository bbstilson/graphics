package org.bbstilson.graphics.noise

object PerlinUtils {
  // Fade function as defined by Ken Perlin.  This eases coordinate values
  // so that they will ease towards integral values.  This ends up smoothing
  // the final output.
  // fade = 6t^5 - 15t^4 + 10t^3
  def fade(t: Double): Double = t * t * t * (t * (t * 6 - 15) + 10)

  // Linear Interpolate
  def lerp(a: Double, b: Double, x: Double): Double = a + x * (b - a)
}
