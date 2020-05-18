import Game.MouseClickType.*
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
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.image.BufferedImage
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.imageio.ImageIO
import javax.swing.JFrame
import javax.swing.event.MouseInputAdapter
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
                //QUIT
                KeyEvent.VK_ESCAPE -> {
                    keyInputChannel.offer(KeyInput.QUIT)
                }

            }
        }

        override fun keyReleased(e: KeyEvent?) {

        }

    }


    enum class MouseClickType {
        NADA,
        MOUSE_MOVE,
        MOUSE_CLICK_PRIMARY_DOWN,
        MOUSE_CLICK_PRIMARY_UP,
        MOUSE_CLICK_PRIMARY_DRAG
    }

    data class MouseClick(val type: MouseClickType, val point: Point)

    val mouseClickChannel = ConflatedBroadcastChannel<MouseClick>()


    private val mouseClickAdapter = object : MouseInputAdapter() {

        override fun mousePressed(e: MouseEvent?) {
            super.mousePressed(e)
            mouseClickChannel.offer(MouseClick(MOUSE_CLICK_PRIMARY_DOWN, e!!.point))
        }

        override fun mouseReleased(e: MouseEvent?) {
            super.mouseReleased(e)
            mouseClickChannel.offer(MouseClick(MOUSE_CLICK_PRIMARY_UP, e!!.point))
        }

        override fun mouseMoved(e: MouseEvent?) {
            super.mouseMoved(e)
            mouseClickChannel.offer(MouseClick(MOUSE_MOVE, e!!.point))
        }

        override fun mouseDragged(e: MouseEvent?) {
            super.mouseDragged(e)
            mouseClickChannel.offer(MouseClick(MOUSE_CLICK_PRIMARY_DRAG, e!!.point))
        }
    }


    val entities = listOf<Entity>(
        ImageEntity(50, 50, "explorer.png"),
        ImageEntity(100, 100, "explorer.png"),
        ImageEntity(150, 150, "explorer.png"),
        ImageEntity(200, 200, "explorer.png"),
        ImageEntity(250, 250, "explorer.png"),
        PolygonEntity(300, 300, generateTriangle(300, 300), Color.RED)
    )

    var currentEntity = entities[0]


    private const val moveIncrement = 50

    var targetEntity: Entity? = null

    @JvmStatic
    fun main(args: Array<String>) {
        gameFrame.display()
        gameFrame.setKeyListener(keyListener)
        gameFrame.setMouseAdapter(mouseClickAdapter)

        mouseClickChannel.offer(MouseClick(NADA, Point()))
        startUp()
    }


    private fun startUp() {
        playGame()
    }

    fun playGame() {
        GlobalScope.launch {


            mouseClickChannel.asFlow()
                .onEach { click: MouseClick ->


                    when (click.type) {

                        MOUSE_CLICK_PRIMARY_DOWN -> {
                            // Has user clicked an Entity?
                            entities.firstOrNull { it.containsPoint(click.point.x, click.point.y) }?.apply {
                                targetEntity = this
                                println(">>> ENTITY TARGETED: $targetEntity")
                            }

                        }

                        MOUSE_CLICK_PRIMARY_UP -> {
                            targetEntity = null
                        }

                        MOUSE_CLICK_PRIMARY_DRAG -> {
                            targetEntity?.apply {
                                centerOnPoint(click.point)
                            }
                        }

                        else -> {

                        }
                    }

                }.launchIn(this)

            keyInputChannel.asFlow().onEach { input ->
                when (input) {
                    KeyInput.QUIT -> {
                        exitProcess(0)
                    }
                }
            }.launchIn(this)

        }

        GlobalScope.launch {

            val graphics = image.graphics as Graphics2D

            graphics.addRenderingHints(
                RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                )
            )

            while (running.get()) {

                graphics.color = Color.LIGHT_GRAY
                graphics.fillRect(0, 0, width, height)

                entities.forEach { entity ->
                    if (entity == targetEntity) {
                        entity.applyImage(graphics, listOf(RenderingDirective.DRAW_RED_BORDER))
                    } else {
                        entity.applyImage(graphics)
                    }

                }

                gameFrame.drawImage(image)
            }
        }
    }

    fun renderTriangles() {
        GlobalScope.launch {

            val graphics = image.graphics as Graphics2D
            graphics.addRenderingHints(
                RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
                )
            )

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

class GameFrame(private val width: Int = 1000, private val height: Int = 1000) {

    private val frame = JFrame()
    private val canvas = Canvas()

    init {
        frame.title = "TEST"

        frame.setSize(width, height)
        frame.preferredSize = Dimension(width, height)

        canvas.size = Dimension(width, height)

        frame.add(canvas)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.pack()

        canvas.requestFocus()
    }

    fun setKeyListener(listener: KeyListener) {
        canvas.addKeyListener(listener)
    }

    fun setMouseListener(listener: MouseListener) {
        canvas.addMouseListener(listener)
    }

    fun setMouseAdapter(clickListener: MouseInputAdapter) {
        canvas.addMouseMotionListener(clickListener)
        canvas.addMouseListener(clickListener)
    }

    fun display() {
        frame.isVisible = true
    }

    fun drawImage(image: BufferedImage, x: Int = 0, y: Int = 0, clearBeforeDrawing: Boolean = false) {
        val graphics = canvas.graphics as Graphics2D
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