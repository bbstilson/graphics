package org.bbstilson.graphics

object VectorOps {
  type Vector2 = (Double, Double)
  type Vector3 = (Double, Double, Double)

  implicit class Vector2Ops(v: Vector2) {
    def -(o: Vector2): Vector2 = (v._1 - o._1, v._2 - o._2)

    def dotP(o: Vector2): Double = v._1 * o._1 + v._2 * o._2
  }

  implicit class Vector3Ops(v: Vector3) {
    def -(o: Vector3): Vector3 = (v._1 - o._1, v._2 - o._2, v._3 - o._3)

    def dotP(o: Vector3): Double = v._1 * o._1 + v._2 * o._2 + v._3 * o._3
  }
}
