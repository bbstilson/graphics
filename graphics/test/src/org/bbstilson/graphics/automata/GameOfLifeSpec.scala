package test.org.bbstilson.graphics.automata

import utest._
import org.bbstilson.graphics.automata.GameOfLife
import org.bbstilson.graphics.automata.Board

object GameOfLifeSpec extends TestSuite {
  def tests = Tests {
    test("num_alive_neighbors") {
      // val dim = 4
      // val init = List(5, 6, 9, 10) // block

      val dim = 5
      val init = List(11, 12, 13) // blinker

      val board = Board(dim, dim)

      init.foreach { i =>
        board.set(i, true)
      }
      println(board)
      println("~~~~~~~~~~~~")

      val steps = 3
      for (_ <- 0 until steps) {
        board.step
        println(board)
        println("~~~~~~~~~~~~")
      }
    }
  }
}
