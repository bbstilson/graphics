import mill._, scalalib._

object graphics extends ScalaModule {
  def scalaVersion = "2.13.1"

  def scalacOpts = Seq("-deprecation")

  def ivyDeps = Agg(
    ivy"eu.timepit::refined:0.9.12"
  )

  object test extends Tests {

    def ivyDeps = Agg(
      ivy"org.scalactic::scalactic:3.1.1",
      ivy"org.scalatest::scalatest:3.1.1"
    )

    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
}
