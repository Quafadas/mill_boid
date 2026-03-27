package boid

import org.scalajs.dom
import scala.util.Random
import vecxt.all.*
import vecxt.BoundsCheck.DoBoundsCheck.yes

import BoidConfig.*
import BoidForces.*
import BoidRenderer.*

@main def main =
  val (canvas, ctx) = createCanvas()
  val width = canvas.width.toDouble
  val height = canvas.height.toDouble

  println("Canvas initialized with dimensions: " + width + "x" + height)

  var intervalId: Int = 0

  def startSimulation(): Unit =
    dom.window.clearInterval(intervalId)

    val n = numBoids
    val positions = Matrix.zeros[Double](n, 2)
    val velocities = Matrix.zeros[Double](n, 2)
    val accelerations = Matrix.zeros[Double](n, 2)

    for i <- 0 until n do
      positions((i, 0)) = Random.nextDouble() * width
      positions((i, 1)) = Random.nextDouble() * height
      velocities((i, 0)) = (Random.nextDouble() - 0.5) * maxSpeed
      velocities((i, 1)) = (Random.nextDouble() - 0.5) * maxSpeed

    def animate(): Unit =
      val currentN = numBoids
      val count = math.min(currentN, n)

      var i = 0
      while i < count do
        val (sepX, sepY, aliX, aliY, cohX, cohY) =
          calculateForces(positions, velocities, i, count)

        accelerations((i, 0)) = sepX * 1.5 + aliX * 1.0 + cohX * 1.0
        accelerations((i, 1)) = sepY * 1.5 + aliY * 1.0 + cohY * 1.0
        i += 1

      for i <- 0 until count do
        velocities((i, 0)) = velocities((i, 0)) + accelerations((i, 0))
        velocities((i, 1)) = velocities((i, 1)) + accelerations((i, 1))

        val speed = math.hypot(velocities(i, 0), velocities(i, 1))
        if speed > maxSpeed then
          velocities((i, 0)) = velocities((i, 0)) * maxSpeed / speed
          velocities((i, 1)) = velocities((i, 1)) * maxSpeed / speed

        positions((i, 0)) = positions((i, 0)) + velocities((i, 0))
        positions((i, 1)) = positions((i, 1)) + velocities((i, 1))

        wrapBoundaries(positions, i, width, height)

      drawBoids(ctx, positions, width, height)

    intervalId = dom.window.setInterval(() => animate(), 25)

  createControlPanel(() => startSimulation())
  startSimulation()
