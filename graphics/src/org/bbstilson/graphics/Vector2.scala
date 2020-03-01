package org.bbstilson.graphics

import scala.util.Random

case class Vector2(x: Double, y: Double) {
  def -(o: Vector2): Vector2 = Vector2(x - o.x, y - o.y)
  def dotP(o: Vector2): Double = x * o.x + y * o.y
}

object Vector2 {
  private val r = Random

  private val gradients = Vector(
    Vector2(1, 1),
    Vector2(-1, 1),
    Vector2(1, -1),
    Vector2(-1, -1)
  )

  def random: Vector2 = gradients(r.nextInt(gradients.size))
}
