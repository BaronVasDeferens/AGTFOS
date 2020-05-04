import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.imageio.ImageIO
import javax.swing.JFrame
import kotlin.random.Random
import kotlin.system.exitProcess


object Game {

    private val width = 2560 + 200
    private val height = 1440 + 200

    private val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

    private val gameFrame = GameFrame(width, height)

    private val continuousRender = AtomicBoolean(true)
    private val running = AtomicBoolean(true)

    private val keyListener = object : KeyListener {
        override fun keyTyped(e: KeyEvent?) {

        }

        override fun keyPressed(e: KeyEvent?) {
            when (e?.keyCode) {

                // PAUSE
                KeyEvent.VK_SPACE -> {
                    continuousRender.set(!continuousRender.get())
                }
                //QUIT
                KeyEvent.VK_ESCAPE -> {
                    running.set(false)
                }
                // SAVE
                KeyEvent.VK_ENTER -> {
                    saveImageToDisk()
                }

            }
        }

        override fun keyReleased(e: KeyEvent?) {

        }

    }

    @JvmStatic
    fun main(args: Array<String>) {
        gameFrame.display()
        gameFrame.setKeyListener(keyListener)
        startUp()
    }


    private fun startUp() {


        GlobalScope.launch {

            val graphics = image.graphics as Graphics2D
            graphics.addRenderingHints(RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON))

            var renderCount = 0

            while (running.get()) {

                if (continuousRender.get()) {
                    val color = Color(
                        Random.nextInt(205) + 50,
                        0,
                        Random.nextInt(255)
                    )
                    graphics.color = color


                    val triangle = generateTriangle(
                        Random.nextInt(width),
                        Random.nextInt(height),
                        Random.nextInt(250),
                        Random.nextBoolean()
                    )


                    val bodyComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f)
                    graphics.composite = bodyComposite
                    graphics.fillPolygon(triangle)

                    val outlineComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)
                    graphics.composite = outlineComposite
                    graphics.drawPolygon(triangle)

                    gameFrame.drawImage(image)
                    renderCount++
//                delay(100)
                }
            }

            println("rendered ${renderCount} items")

            exitProcess(0)
        }
    }

    private fun generateTriangle(x: Int, y: Int, sideLength: Int = 50, pointUp: Boolean = true): Polygon {

        val poly = Polygon()
        poly.addPoint(x, y)
        poly.addPoint(x + sideLength, y)

        val tip = (-0.84 * sideLength).toInt() * if (pointUp) 1 else -1
        poly.addPoint(x + (sideLength / 2), y + tip)
        return poly
    }


    fun saveImageToDisk() {
        continuousRender.set(false)

        val file = File("${System.currentTimeMillis()}.png")
        file.createNewFile()
        ImageIO.write(image, "png", file)
        println("wrote image to disk: ${file.name}")
    }

}

class GameFrame (private val width: Int = 1000, private val height: Int = 1000){


    private val frame = JFrame()
    private val canvas = Canvas()

    init {
        frame.title = "TEST"

        frame.requestFocus()
        frame.setSize(width, height)
        frame.preferredSize = Dimension(width, height)

        canvas.background = Color.BLUE
        canvas.size = Dimension(width, height)

        frame.add(canvas)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()
    }

    fun setKeyListener(listener: KeyListener) {
        frame.addKeyListener(listener)
    }

    fun display() {
        frame.isVisible = true
    }

    fun drawImage(image: BufferedImage, x: Int = 0, y: Int = 0, clearBeforeDrawing: Boolean = false) {
        val graphics = canvas.graphics as Graphics2D

        if (clearBeforeDrawing) {
            graphics.clearRect(0, 0, width, height)
        }

        graphics.drawImage(image, x, y, null)
        graphics.dispose()
    }

    fun changeBackgroundColor(color: Color) {
        val graphics = canvas.graphics as Graphics2D
        graphics.color = color
        graphics.fillRect(0, 0, width, height)
        graphics.dispose()
    }

}