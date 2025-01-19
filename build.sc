import mill._, scalalib._

object graphics extends ScalaModule {
  def scalaVersion = "3.3.4"

  def scalacOpts = Seq("-deprecation")

  def ivyDeps = Agg(
    ivy"eu.timepit::refined:0.11.3",
    ivy"org.scala-lang.modules::scala-parallel-collections:1.2.0"
  )

  object test extends ScalaTests {

    def ivyDeps = Agg(
      ivy"org.scalactic::scalactic:3.2.19",
      ivy"org.scalatest::scalatest:3.2.19"
    )

    def testFramework = "org.scalatest.tools.Framework"
  }
}
