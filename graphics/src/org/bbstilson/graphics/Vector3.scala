package org.bbstilson.graphics

import scala.util.Random

case class Vector3(x: Double, y: Double, z: Double) {
  def -(o: Vector3): Vector3 = Vector3(x - o.x, y - o.y, z - o.z)
  def dotP(o: Vector3): Double = x * o.x + y * o.y + z * o.z
}

object Vector3 {
  private val r = Random

  private val gradients = Vector(
    Vector3(1, 1, 0),
    Vector3(-1, 1, 0),
    Vector3(1, -1, 0),
    Vector3(-1, -1, 0),
    Vector3(1, 0, 1),
    Vector3(-1, 0, 1),
    Vector3(1, 0, -1),
    Vector3(-1, 0, -1),
    Vector3(0, 1, 1),
    Vector3(0, -1, 1),
    Vector3(0, 1, -1),
    Vector3(0, -1, -1)
  )

  def random: Vector3 = gradients(r.nextInt(gradients.size))
}
