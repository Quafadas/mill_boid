package boid

import org.scalajs.dom
import org.scalajs.dom.{document, html}
import vecxt.all.*
import vecxt.BoundsCheck.DoBoundsCheck.yes

object BoidRenderer:
  import BoidConfig.*

  def createCanvas(): (html.Canvas, dom.CanvasRenderingContext2D) =
    val width = dom.window.innerWidth
    val height = dom.window.innerHeight
    val canvas = document.createElement("canvas").asInstanceOf[html.Canvas]
    canvas.width = width.toInt
    canvas.height = height.toInt
    canvas.style.display = "block"
    document.body.appendChild(canvas)

    val ctx = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
    ctx.fillStyle = "red"
    ctx.strokeStyle = "black"
    ctx.lineWidth = 1
    ctx.font = "16px Arial"
    ctx.textAlign = "center"
    ctx.textBaseline = "middle"

    (canvas, ctx)

  def createControlPanel(onRestart: () => Unit): html.Div =
    val link = document.createElement("link").asInstanceOf[html.Link]
    link.rel = "stylesheet"
    link.href = "panel.css"
    document.head.appendChild(link)

    val panel = document.createElement("div").asInstanceOf[html.Div]
    panel.classList.add("boid-panel")

    def addSlider(
        label: String,
        min: Double,
        max: Double,
        step: Double,
        initial: Double,
        onChange: Double => Unit
    ): Unit =
      val container = document.createElement("label").asInstanceOf[html.Label]

      val text = document.createElement("span").asInstanceOf[html.Span]
      text.textContent = label

      val valueDisplay = document.createElement("span").asInstanceOf[html.Span]
      valueDisplay.textContent = initial.toString
      valueDisplay.classList.add("slider-value")

      val input = document.createElement("input").asInstanceOf[html.Input]
      input.`type` = "range"
      input.min = min.toString
      input.max = max.toString
      input.step = step.toString
      input.value = initial.toString

      input.addEventListener("input", (_: dom.Event) =>
        val v = input.value.toDouble
        valueDisplay.textContent =
          if step >= 1 then v.toInt.toString else f"$v%.2f"
        onChange(v)
        onRestart()
      )

      container.appendChild(text)
      container.appendChild(input)
      container.appendChild(valueDisplay)
      panel.appendChild(container)

    addSlider("Boids:", 10, 300, 10, numBoids, v => BoidConfig.numBoids = v.toInt)
    addSlider("Speed:", 1, 20, 0.5, maxSpeed, v => BoidConfig.maxSpeed = v)
    addSlider("Force:", 0.01, 1.0, 0.01, maxForce, v => BoidConfig.maxForce = v)
    addSlider("Sep:", 5, 100, 1, separationDistance, v => BoidConfig.separationDistance = v)
    addSlider("Align:", 5, 150, 1, alignmentDistance, v => BoidConfig.alignmentDistance = v)
    addSlider("Cohesion:", 5, 150, 1, cohesionDistance, v => BoidConfig.cohesionDistance = v)

    document.body.appendChild(panel)
    panel

  def drawBoids(
      ctx: dom.CanvasRenderingContext2D,
      positions: Matrix[Double],
      width: Double,
      height: Double
  ): Unit =
    ctx.clearRect(0, 0, width, height)
    for i <- 0 until numBoids do
      ctx.beginPath()
      ctx.arc(positions(i, 0), positions(i, 1), 5, 0, 2 * math.Pi)
      ctx.fill()
