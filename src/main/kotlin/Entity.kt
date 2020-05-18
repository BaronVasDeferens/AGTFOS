import java.awt.Color
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Polygon
import java.awt.image.BufferedImage
import java.util.concurrent.atomic.AtomicInteger
import javax.imageio.ImageIO

abstract class Entity(
    initialX: Int = 100,
    initialY: Int = 100
){

    open val x = AtomicInteger(initialX)
    open val y = AtomicInteger(initialY)



    fun adjustCoordinates(xAdj: Int, yAdj: Int) {
        x.set( x.get() + xAdj)
        y.set( x.get() + yAdj)
    }

    abstract fun centerOnPoint(point: Point)


    abstract fun applyImage(graphics: Graphics2D)
    abstract fun containsPoint(clickX: Int, clickY: Int): Boolean


}


data class PolygonEntity(
    val initialX: Int = 100,
    val initialY: Int = 100,
    val polygon: Polygon,
    val polygonColor: Color
) : Entity(initialX, initialY) {

    override fun centerOnPoint(point: Point) {


    }

    override fun applyImage(graphics: Graphics2D) {
        graphics.color = polygonColor
        graphics.fillPolygon(getPolyAtAdjustedCoords())
    }

    fun getPolyAtAdjustedCoords(): Polygon {
        val newPoly = Polygon()
        for (i in 0 until polygon.npoints) {
            newPoly.addPoint(polygon.xpoints[i] + x.get(), polygon.ypoints[i] + y.get())
        }
        return newPoly
    }

    override fun containsPoint(clickX: Int, clickY: Int): Boolean {
        return getPolyAtAdjustedCoords().contains(clickX, clickY)
    }


}


data class ImageEntity(
    val initialX: Int = 100,
    val initialY: Int = 100,
    val imageName: String
) : Entity(initialX, initialY) {

    val image: BufferedImage = ImageIO.read(this.javaClass.getResourceAsStream(imageName))

    override fun centerOnPoint(point: Point) {
        x.set(point.x - (image.width / 2))
        y.set(point.y - (image.height / 2))
    }

    override fun applyImage(graphics: Graphics2D) {
        graphics.drawImage(image, x.get(), y.get(), null)
    }

    override fun containsPoint(clickX: Int, clickY: Int): Boolean {
        return (clickX >= x.get()) && (clickX <= x.get() + image.width) && (clickY >= y.get()) && (clickY <= y.get() + image.height)
    }

}