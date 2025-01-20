package org.bbstilson.graphics.automata

import scala.collection.mutable

import org.bbstilson.graphics.Image
import java.awt.Color

val HEIGHT = 200
val WIDTH = 200
val ITERATIONS = 300
val INIT_LIKELIHOOD = 0.2

// ./mill graphics.runMain org.bbstilson.graphics.automata.GameOfLife
// ffmpeg -framerate 15 -pattern_type glob -i '*.jpg' -c:v libx264 out.mp4
object GameOfLife {
  def main(args: Array[String]): Unit = {
    val board = Board(WIDTH, HEIGHT)
    // initialize with random values
    for (idx <- 0 until board.length) {
      if (math.random() <= INIT_LIKELIHOOD) {
        board.set(idx, true)
      }
    }

    for (i <- 0 until ITERATIONS) {
      board.step

      val image =
        Image(WIDTH, HEIGHT, Some(f"conway/$i%04d"))
      image.fill(Color.WHITE)
      for (idx <- 0 until board.length) {
        if (board.at(idx)) {
          val x = idx / WIDTH
          val y = idx % WIDTH
          image.writePixel(x, y, Color.BLACK)
        }
      }
      image.writeImage()
    }
  }

}

final case class Board(width: Int, var states: mutable.ArraySeq[Boolean]) {
  def at(idx: Int): Boolean = {
    states(idx)
  }

  def set(idx: Int, value: Boolean): Unit = {
    states(idx) = value
  }

  def length: Int = {
    states.length
  }

  def step: Unit = {
    val next = states.clone()
    for (idx <- 0 until states.length) {
      next(idx) = num_alive_neighbors(idx) match {
        // Any live cell with two or three live neighbours lives.
        case 2 | 3 if states(idx) => true
        // Any dead cell with exactly three live neighbours will come to life.
        case 3 if !states(idx) => true
        // Any live cell with more than three live neighbours dies (overpopulation).
        case n if n > 3 && states(idx) => false
        // Any live cell with fewer than two live neighbours dies (underpopulation).
        case n => false
      }
    }
    states = next
  }

  def num_alive_neighbors(idx: Int): Int = {
    List(
      up,
      upRight,
      right,
      downRight,
      down,
      downLeft,
      left,
      upLeft
    )
      .flatMap(f => f(idx))
      .filter(states)
      .length
  }

  def upRight(idx: Int): Option[Int] = {
    for {
      u <- up(idx)
      r <- right(u)
    } yield r
  }

  def upLeft(idx: Int): Option[Int] = {
    for {
      u <- up(idx)
      r <- left(u)
    } yield r
  }

  def downLeft(idx: Int): Option[Int] = {
    for {
      d <- down(idx)
      l <- left(d)
    } yield l
  }

  def downRight(idx: Int): Option[Int] = {
    for {
      d <- down(idx)
      r <- right(d)
    } yield r
  }

  def up(idx: Int): Option[Int] = {
    val n = idx - width
    if (n > 0) {
      Some(n)
    } else {
      None
    }
  }

  def down(idx: Int): Option[Int] = {
    val n = idx + width
    if (n < states.length) {
      Some(n)
    } else {
      None
    }
  }

  def left(idx: Int): Option[Int] = {
    if (idx % width == 0) { None }
    else { Some(idx - 1) }
  }

  def right(idx: Int): Option[Int] = {
    if ((idx + 1) % width == 0) { None }
    else { Some(idx + 1) }
  }

  override def toString(): String = {
    val sb = StringBuilder()
    sb.append("-" * width)
    for (i <- 0 until states.length) {
      if (i % width == 0) {
        sb.addOne('\n')
      }
      sb.addOne(if (states(i)) '#' else ' ')
    }
    sb.addOne('\n')
    sb.append("-" * width)
    sb.toString()
  }
}

object Board {
  def apply(width: Int, height: Int): Board = {
    Board(
      width,
      Array.fill(width * height)(false)
    )
  }
}
