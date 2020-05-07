import Game.MouseInteractionType.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.awt.*
import java.awt.event.*
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

                KeyEvent.VK_D -> {
                    keyInputChannel.offer(KeyInput.RIGHT)
                }

            }
        }

        override fun keyReleased(e: KeyEvent?) {

        }

    }

    private val mouseListener = object : MouseListener {
        override fun mouseReleased(e: MouseEvent?) {
        }

        override fun mouseEntered(e: MouseEvent?) {
        }

        override fun mouseClicked(e: MouseEvent?) {
        }

        override fun mouseExited(e: MouseEvent?) {
        }

        override fun mousePressed(e: MouseEvent?) {
            entities.forEach { entity ->
                if (entity.containsPoint(e!!.x, e!!.y)) {
                    mouseInteractionChannel.offer(MouseInteraction(entity, ENTITY_CLICKED))
                }
            }
        }
    }

    enum class MouseInteractionType {
        ENTITY_CLICKED,
        NOTHING_CLICKED
    }

    data class MouseInteraction(val entity: Entity, val interactionType: MouseInteractionType)

    val mouseInteractionChannel = ConflatedBroadcastChannel<MouseInteraction>()


    val entities = listOf<Entity>(
        Entity(50, 50, generateTriangle(50, 50)),
        Entity(100, 100, generateTriangle(100, 100)),
        Entity(150, 150, generateTriangle(150, 150)),
        Entity(200, 200, generateTriangle(200, 200)),
        Entity(250, 250, generateTriangle(250, 250))
    )

    var currentEntity = entities[0]


    private const val moveIncrement = 50

    @JvmStatic
    fun main(args: Array<String>) {
        gameFrame.display()
        gameFrame.setKeyListener(keyListener)
        gameFrame.setMouseListener(mouseListener)
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
                        currentEntity.adjustCoordinates(0, -moveIncrement)
                        soundPlayer.play()
                    }

                    KeyInput.DOWN -> {
                        currentEntity.adjustCoordinates(0, moveIncrement)
                        soundPlayer.play()
                    }

                    KeyInput.LEFT -> {
                        currentEntity.adjustCoordinates(-moveIncrement, 0)
                        soundPlayer.play()
                    }

                    KeyInput.RIGHT -> {
                        currentEntity.adjustCoordinates(moveIncrement, 0)
                        soundPlayer.play()
                    }

                    KeyInput.PAUSE -> {

                    }

                    KeyInput.QUIT -> {
                        exitProcess(0)
                    }
                }


            }.launchIn(this)

            mouseInteractionChannel.asFlow().onEach { interaction ->
                when (interaction.interactionType) {
                    ENTITY_CLICKED -> {
                        currentEntity = interaction.entity
                        println("Clicked on entity")
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

                graphics.color = Color.DARK_GRAY
                graphics.fillRect(0, 0, width, height)

                entities.forEach { entity ->
                    graphics.color = if (entity == currentEntity) Color.RED else Color.BLUE
                    val triangle = entity.getPolyAtAdjustedCoords()
                    graphics.fillPolygon(triangle)
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