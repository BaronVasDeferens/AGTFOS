import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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


@FlowPreview
@ExperimentalCoroutinesApi
object Game {

    private const val width = 800
    private const val height = 800

    private val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)

    private val gameFrame = GameFrame(width, height)

    private val continuousRender = AtomicBoolean(true)
    private val running = AtomicBoolean(true)

    enum class KeyInput {
        UP,
        DOWN,
        LEFT,
        RIGHT,

        PAUSE,

        QUIT
    }
    val keyInputChannel = ConflatedBroadcastChannel<KeyInput>()

    private val keyListener = object : KeyListener {
        override fun keyTyped(e: KeyEvent?) {

        }

        override fun keyPressed(e: KeyEvent?) {
            when (e?.keyCode) {

                // PAUSE
                KeyEvent.VK_SPACE -> {
                    keyInputChannel.offer(KeyInput.PAUSE)
                }

                //QUIT
                KeyEvent.VK_ESCAPE -> {
                    keyInputChannel.offer(KeyInput.QUIT)
                }
                // SAVE
//                KeyEvent.VK_ENTER -> {
//                    saveImageToDisk()
//                }

                // DIRECTIONS
                KeyEvent.VK_W -> {
                    keyInputChannel.offer(KeyInput.UP)
                }

                KeyEvent.VK_A -> {
                    keyInputChannel.offer(KeyInput.LEFT)
                }

                KeyEvent.VK_S -> {
                    keyInputChannel.offer(KeyInput.DOWN)
                }

                KeyEvent.VK_D-> {
                    keyInputChannel.offer(KeyInput.RIGHT)
                }

            }
        }

        override fun keyReleased(e: KeyEvent?) {

        }

    }

    val entity = Entity(100, 100, generateTriangle(100,100))


    private const val moveIncrement = 50

    @JvmStatic
    fun main(args: Array<String>) {
        gameFrame.display()
        gameFrame.setKeyListener(keyListener)
        startUp()
    }


    private fun startUp() {

//        renderTriangles()
        playGame()


    }

    fun playGame() {
        GlobalScope.launch {

            val soundPlayer = SoundPlayer("walk.wav")

            keyInputChannel.asFlow().onEach { keyPress ->

                    when (keyPress) {

                        KeyInput.UP -> {
                            entity.adjustCoordinates(0, -moveIncrement)
                            // entity.y -= moveIncrement
                            soundPlayer.play()
                        }

                        KeyInput.DOWN -> {
                            entity.adjustCoordinates(0, moveIncrement)
//                            entity.y += moveIncrement
                            soundPlayer.play()
                        }

                        KeyInput.LEFT -> {
                            entity.adjustCoordinates(-moveIncrement, 0)

//                            entity.x -= moveIncrement
                            soundPlayer.play()
                        }

                        KeyInput.RIGHT -> {
                            entity.adjustCoordinates(moveIncrement, 0)
//                            entity.x += moveIncrement
                            soundPlayer.play()
                        }

                        KeyInput.PAUSE -> {

                        }

                        KeyInput.QUIT -> {
                            exitProcess(0)
                        }
                    }


            }.launchIn(this)
        }

        GlobalScope.launch{
            val graphics = image.graphics as Graphics2D
            graphics.addRenderingHints(RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON))

            while (running.get()) {

                graphics.color = Color.RED
                graphics.fillRect(0,0,width, height)

                graphics.color = Color.BLACK

                val triangle = entity.getPolyAtCoords()
                graphics.fillPolygon(triangle)
                gameFrame.drawImage(image)
            }

        }
    }

    fun renderTriangles() {
        GlobalScope.launch {

            val graphics = image.graphics as Graphics2D
            graphics.addRenderingHints(RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON))

            var renderCount = 0

            while (running.get()) {

                if (continuousRender.get()) {
                    val color = Color(
                        Random.nextInt(205) + 50,
                        Random.nextInt(78),
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

        frame.setSize(width, height)
        frame.preferredSize = Dimension(width, height)

        canvas.background = Color.BLUE
        canvas.size = Dimension(width, height)

        frame.add(canvas)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()

        canvas.requestFocus()
    }

    fun setKeyListener(listener: KeyListener) {
        canvas.addKeyListener(listener)
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